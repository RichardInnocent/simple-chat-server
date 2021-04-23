import java.io.InputStream;

/**
 * Responsible for parsing notifications received from the server into a usable object format.
 */
public interface ChatNotificationParser {

  /**
   * Creates a chat notification from the received XML.
   * @param xmlString The XML received from the server.
   * @return A {@link ChatNotification} corresponding to the XML.
   * @throws XmlParseException Thrown if there is a problem converting the XML into a
   * {@link ChatNotification}.
   */
  ChatNotification fromXml(String xmlString) throws XmlParseException;

  /**
   * Creates a chat request from the received XML.
   * @param xmlInputStream The XML input stream to read from. Note that the input stream won't be
   * parsed in its entirety - only from the first opening tag up until its matching closing tag.
   * @return A {@link ChatNotification} corresponding to the parsed XML.
   * @throws XmlParseException Thrown if there is a problem converting the XML into a
   * {@link ChatNotification}.
   */
  ChatNotification fromXml(InputStream xmlInputStream) throws XmlParseException;

}
