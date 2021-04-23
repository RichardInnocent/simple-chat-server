import javax.xml.stream.XMLStreamReader;

/**
 * Reads XML and serialises it into an object.
 */
public interface XmlReader {

  /**
   * Reads XML from the given {@code streamReader} and creates an object. The stream will not be
   * read in its entirety, only from the first opening tag to its matching closing tag.
   * @param streamReader The XML stream to read from.
   * @param targetType The type of class to transform the XML into.
   * @param <T> The target type.
   * @return The object that represents the given XML.
   * @throws XmlParseException Thrown if there is a problem converting the XML into the target type.
   */
  <T> T readFromXml(XMLStreamReader streamReader, Class<T> targetType) throws XmlParseException;

  /**
   * Reads an XML string and creates an object.
   * @param xml THe XML string to parse.
   * @param targetType The type of class to transform the XML into.
   * @param <T> The target type.
   * @return The object that represents the given XML.
   * @throws XmlParseException Thrown if there is a problem converting the XML into the target type.
   */
  <T> T fromXml(String xml, Class<T> targetType) throws XmlParseException;

}
