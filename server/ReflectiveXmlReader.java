import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This is a simple reflection-based XML reader based on the
 * <a href="https://github.com/FasterXML/jackson-databind">FasterXML Jackson Databind library</a>.
 * The concept is to be able to take an XML input data and serialise it into an object. The reader
 * analyses the XML element names and compares it with the field names of the target object type.
 * This implementation is fairly limited in scope, but should handle all of the serialisation
 * required for this coursework.
 */
public class ReflectiveXmlReader implements XmlReader {

  private static final Logger LOGGER = Logger.getLogger(ReflectiveXmlReader.class.getName());

  @Override
  public <T> T fromXml(String xml, Class<T> targetType) throws XmlParseException {
    try (
        InputStream xmlInputStream =
            new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
    ) {
      XMLStreamReader xmlStreamReader =
          XMLInputFactory.newInstance().createXMLStreamReader(xmlInputStream);
      xmlStreamReader.next(); // Skip the first element as this contains the name of the class
      return fromXmlThrowException(xmlStreamReader, targetType);
    } catch (Exception e) {
      throw new XmlParseException("Failed to close XML input stream", e);
    }
  }

  @Override
  public <T> T readFromXml(XMLStreamReader reader, Class<T> type) throws XmlParseException {
    try {
      return fromXmlThrowException(reader, type);
    } catch (Exception e) {
      throw new XmlParseException("Failed to parse XML for type " + type, e);
    }
  }

  private <T> T fromXmlThrowException(XMLStreamReader reader, Class<T> type) throws Exception {
    T result = type.getDeclaredConstructor().newInstance();
    while(reader.hasNext()) {
      if (reader.next() != XMLStreamConstants.START_ELEMENT) {
        // Skip all elements apart from a start element. This will be the starting point of the
        // stream for the setFieldFromXml method call below
        continue;
      }
      setFieldFromXml(reader, result);
    }
    return result;
  }

  private <U> void setFieldFromXml(XMLStreamReader reader, U baseObject) throws Exception {
    // Get the name of the element
    String name = reader.getLocalName();
    try {
      // Try to set the field based on the element's name and value
      setField(name, baseObject, reader);
    } catch (ReflectiveOperationException e) {
      LOGGER.log(
          Level.WARNING, "Failed to parse XML for element type " + baseObject.getClass(), e
      );

      // Skip the everything else in this element until we get to a closing tag for this item
      skipUntilCloseOfElement(reader);
      throw e;
    }
  }

  private void setField(String fieldName, Object baseObject, XMLStreamReader reader)
      throws ReflectiveOperationException, XMLStreamException {
    // What type of object are we expecting for tis field name?
    Field field = baseObject.getClass().getDeclaredField(fieldName);

    // Skip to the next part of the XML, hopefully the content of the element
    int elementType = reader.next();

    while (elementType != XMLStreamConstants.CHARACTERS) {
      // Skip any elements that isn't the content of XML
      elementType = reader.next();

      // If we hit the end element, we know the XML has no value assigned so don't set the field
      if (elementType == XMLStreamConstants.END_ELEMENT) {
        return;
      }
    }

    // Should we ignore the field?
    if (field.getAnnotation(XmlIgnore.class) == null) {
      // The field is not marked to be ignored so let's try to set it
      setField(field, baseObject, getAllTextFromElement(reader));
    }
  }

  private String getAllTextFromElement(XMLStreamReader reader) throws XMLStreamException {
    StringBuilder text = new StringBuilder(reader.getText());

    // For some reason, entity-encoded less/greater than symbols cause cause the XML reader to
    // chunk up the data into several components. Concatenate them all here.
    while (reader.next() == XMLStreamConstants.CHARACTERS) {
      text.append(reader.getText());
    }
    return text.toString();
  }

  private void setField(Field field, Object baseObject, String value)
      throws ReflectiveOperationException {
    // Making sure we can set the field
    boolean isAccessible = field.canAccess(baseObject);
    if (!isAccessible) {
      field.setAccessible(true);
    }
    // Try to set the field - this time we don't have to worry about accessibility
    setFieldIgnoreAccessibility(field, baseObject, value);

    // Clean up any changes we made
    if (!isAccessible) {
      field.setAccessible(false);
    }
  }

  private void setFieldIgnoreAccessibility(Field field, Object baseObject, String value)
      throws ReflectiveOperationException {
    // Because this is a rudimentary parser, we only support certain fields types
    if (field.getType() == String.class) {
      setStringField(field, baseObject, value);
    } else if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE) {
      setBooleanField(field, baseObject, value);
    } else if (field.getType() == Integer.class || field.getType() == Integer.TYPE) {
      setIntField(field, baseObject, value);
    } else if (Collection.class.isAssignableFrom(field.getType())) {
      setCollectionField(field, baseObject, value);
    } else {
      // The field type isn't supported
      throw new ReflectiveOperationException(
          "This class does not support fields of type " + field.getType()
      );
    }
  }

  private void setStringField(Field field, Object baseObject, String value)
      throws IllegalAccessException {
    field.set(baseObject, value);
  }

  private void setBooleanField(Field field, Object baseObject, String value)
      throws IllegalAccessException {
    boolean booleanValue = Boolean.parseBoolean(value);
    field.set(baseObject, booleanValue);
  }

  private void setIntField(Field field, Object baseObject, String value)
      throws IllegalAccessException {
    int intValue = Integer.parseInt(value);
    field.set(baseObject, intValue);
  }

  @SuppressWarnings("unchecked")
  private void setCollectionField(Field field, Object baseObject, String value)
      throws ReflectiveOperationException {
    Class<?> type = field.getType();
    Collection<String> result;
    if (type == Collection.class || List.class.isAssignableFrom(type)) {
      // If it's a collection or a list, create a list from the value
      result = createList((Class<List<String>>) type, value);
    } else if (Set.class.isAssignableFrom(type)) {
      // If it's a set, create a set from the value
      result = createSet((Class<Set<String>>) type, value);
    } else {
      throw new IllegalArgumentException("This class does not support collections of type " + type);
    }
    field.set(baseObject, result);
  }

  private List<String> createList(Class<? extends List<String>> type, String value)
      throws ReflectiveOperationException {
    // If the type is an interface, creating a concrete implementation
    if (type.isInterface()) {
      return streamCollectionValues(value).collect(Collectors.toList());
    }
    // If not an interface, try to use the class' default constructor. This could break if the
    // implementation is an abstract class, but, again, this is a rudimentary implementation. Given
    // more time, a better XML processor could be created.
    List<String> list = type.getConstructor().newInstance();
    streamCollectionValues(value).forEach(list::add);
    return list;
  }

  private Set<String> createSet(Class<? extends Set<String>> type, String value)
      throws ReflectiveOperationException {
    // If the type is an interface, creating a concrete implementation
    if (type.isInterface()) {
      return streamCollectionValues(value).collect(Collectors.toSet());
    }
    // If not an interface, try to use the class' default constructor. This could break if the
    // implementation is an abstract class, but, again, this is a rudimentary implementation. Given
    // more time, a better XML processor could be created.
    Set<String> list = type.getConstructor().newInstance();
    streamCollectionValues(value).forEach(list::add);
    return list;
  }

  /**
   * Collections are difficult to parse as it's difficult to gain access to their generic type. So
   * that we can ignore all of these difficulties, collections are expected to be a
   * comma-separated list of values. This is a brittle solution, but it should be fine for this
   * implementation provided that we are careful about what goes into the values.
   */
  private Stream<String> streamCollectionValues(String value) {
    return Arrays.stream(value.split(",")).map(String::trim);
  }

  private void skipUntilCloseOfElement(XMLStreamReader reader) throws XMLStreamException {
    int requiredClosingElements = 1;

    // Skip the rest of the nested elements for this field
    while (requiredClosingElements > 0) {
      int nextType = reader.next();
      switch (nextType) {
        case XMLStreamConstants.START_ELEMENT:
          requiredClosingElements++;
          break;
        case XMLStreamConstants.END_ELEMENT:
          requiredClosingElements--;
          break;
      }
    }
  }

}
