import javax.xml.stream.XMLStreamWriter;

/**
 * Class responsible for writing objects of various types to XML.
 */
public interface XmlWriter {

  /**
   * Writes an object to {@link XMLStreamWriter}.
   * @param object The object to write out to XML.
   * @param streamWriter The writer to write the XML to.
   * @param <T> The type of the target object, {@code object}.
   * @throws XmlParseException Thrown if there is a problem writing the object to XML.
   */
  <T> void writeToXml(T object, XMLStreamWriter streamWriter) throws XmlParseException;

  /**
   * Write an object to an XML string.
   * @param object The object to write out to XML.
   * @param <T> The type of the target object, {@code object}.
   * @return The object as XML.
   * @throws XmlParseException Thrown if therre is a problem writing the object to XML.
   */
  <T> String toXml(T object) throws XmlParseException;

}
