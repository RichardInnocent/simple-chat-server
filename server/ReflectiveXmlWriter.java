import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This is a simple reflection-based XML writer based on the
 * <a href="https://github.com/FasterXML/jackson-databind">FasterXML Jackson Databind library</a>.
 * The concept is to be able to take an object and create corresponding XML for it without any
 * additional methods required on the class. The writes analyses the field names of the instance
 * and writes the XML elements out with the same names. This implementation is fairly limited in
 * scope, but should handle all of the de-serialisation required for this coursework.
 */
public class ReflectiveXmlWriter implements XmlWriter {

  private static final Logger LOGGER = Logger.getLogger(ReflectiveXmlWriter.class.getName());
  private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

  private static final ReflectiveXmlWriter INSTANCE = new ReflectiveXmlWriter();

  private ReflectiveXmlWriter() {}

  /**
   * Gets the singleton instance.
   * @return The singleton instance.
   */
  public static ReflectiveXmlWriter getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> void writeToXml(T object, XMLStreamWriter streamWriter) throws XmlParseException {
    try {
      writeToXmlThrowExceptions(object, streamWriter);
    } catch (Exception e) {
      throw new XmlParseException(
          "Failed to write element of type " + object.getClass().getName() + " to XML", e
      );
    }
  }

  @Override
  public <T> String toXml(T object) throws XmlParseException {
    // Create an output stream to collect the data
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      XMLStreamWriter streamWriter = null; // Not auto-closeable
      try {
        // Create a new XML stream writer
        streamWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream);

        // Write out the results via the stream writer
        writeToXmlThrowExceptions(object, streamWriter);

        // Convert the data in the output stream to a string and return this
        return outputStream.toString(StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new XmlParseException(
            "Failed to write element of type " + object.getClass().getName() + " to XML", e
        );
      } finally {
        // Clean up the stream writer
        if (streamWriter != null) {
          try {
            streamWriter.close();
          } catch (XMLStreamException e) {
            LOGGER.log(Level.WARNING, "Failed to close XML stream writer", e);
          }
        }
      }

    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Could not close XML output stream");
      throw new XmlParseException("Could not close XML output stream", e);
    }
  }

  private <T> void writeToXmlThrowExceptions(T object, XMLStreamWriter streamWriter)
      throws XMLStreamException, IllegalAccessException {
    // Write out the name of the class
    streamWriter.writeStartElement(object.getClass().getSimpleName());
    try {
      // Write out the fields contained within the element
      writeFields(object, streamWriter);
    } finally {
      // Close the element
      streamWriter.writeEndElement();
      streamWriter.flush();
    }
  }

  private <T> void writeFields(T object, XMLStreamWriter streamWriter)
      throws XMLStreamException, IllegalAccessException {
    // Iterate through each field in the class
    for (Field field : object.getClass().getDeclaredFields()) {
      // If the field is marked with XmlIgnore, don't bother processing it
      if (field.getAnnotation(XmlIgnore.class) == null) {
        // Write out the value of the field for this instance
        writeField(object, field, streamWriter);
      }
    }
  }

  private <T> void writeField(T object, Field field, XMLStreamWriter streamWriter)
      throws XMLStreamException, IllegalAccessException {
    // Write out the name of the field
    streamWriter.writeStartElement(field.getName());
    try {
      // Write out the value of the field
      writeElementCharacters(object, field, streamWriter);
    } finally {
      // Close the field tag
      streamWriter.writeEndElement();
    }
  }

  private <T> void writeElementCharacters(T object, Field field, XMLStreamWriter streamWriter)
      throws IllegalAccessException, XMLStreamException {
    // This is a rudimentary implementation that only supports a limited number of types
    Class<?> fieldType = field.getType();
    if (fieldType == String.class) {
      writeStringField(object, field, streamWriter);
    } else if (fieldType == Boolean.TYPE || fieldType == Boolean.class) {
      writeBooleanField(object, field, streamWriter);
    } else if (fieldType == Integer.TYPE || fieldType == Integer.class) {
      writeIntField(object, field, streamWriter);
    } else if (Collection.class.isAssignableFrom(fieldType)) {
      writeCollectionField(object, field, streamWriter);
    } else {
      // Type not supported
      throw new IllegalArgumentException(
          "This stream writer does not support elements of type " + fieldType
      );
    }
  }

  private <T> void writeStringField(T object, Field field, XMLStreamWriter streamWriter)
      throws IllegalAccessException, XMLStreamException {
    String value = (String) getFieldValue(object, field);
    if (value != null) {
      streamWriter.writeCharacters(value);
    }
  }

  private <T> void writeBooleanField(T object, Field field, XMLStreamWriter streamWriter)
      throws IllegalAccessException, XMLStreamException {
    Boolean value = (Boolean) getFieldValue(object, field);
    if (value != null) {
      streamWriter.writeCharacters(value.toString());
    }
  }

  private <T> void writeIntField(T object, Field field, XMLStreamWriter streamWriter)
      throws IllegalAccessException, XMLStreamException {
    Integer value = (Integer) getFieldValue(object, field);
    if (value != null) {
      streamWriter.writeCharacters(value.toString());
    }
  }

  /**
   * Collections are difficult to write out as it's difficult to determine their generic type. So
   * that we can ignore all of these difficulties, collections are written out as a
   * comma-separated list of values. This is a brittle solution, but it should be fine for this
   * implementation provided that we are careful about what goes into the values.
   */
  private <T> void writeCollectionField(T object, Field field, XMLStreamWriter streamWriter)
      throws IllegalAccessException, XMLStreamException {
    Collection<?> collection = (Collection<?>) getFieldValue(object, field);
    if (collection != null) {
      streamWriter.writeCharacters(
          collection.stream()
                    .filter(Objects::nonNull) // Ignore null values so we don't get NPEs
                    .map(Object::toString) // Map to a string
                    .collect(Collectors.joining(",")) // Separate by commas
      );
    }
  }

  private <T> Object getFieldValue(T object, Field field) throws IllegalAccessException {
    boolean fieldIsAccessible = field.canAccess(object);
    if (!fieldIsAccessible) {
      // Make the field accessible so we can get its value
      field.setAccessible(true);
    }

    Object value = field.get(object);

    // Clean up any changes we made
    if (!fieldIsAccessible) {
      field.setAccessible(false);
    }

    return value;
  }

}
