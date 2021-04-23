import java.io.InputStream;

/**
 * Responsible for parsing requests received from clients into a usable object format.
 */
public interface ChatRequestParser {

  /**
   * Creates a chat request from the received XML.
   * @param xmlString The XML received from the client.
   * @param author The author of the request.
   * @return A {@link ChatRequest} corresponding to the XML.
   * @throws RequestProcessingException Thrown if there is a problem converting the XML into a
   * {@link ChatRequest}.
   */
  ChatRequest fromXml(String xmlString, User author) throws RequestProcessingException;

  /**
   * Creates a chat request from the received XML.
   * @param xmlInputStream The XML input stream to read from. Note that the input stream won't be
   * parsed in its entirety - only from the first opening tag up until its matching closing tag.
   * @param author The author of the request.
   * @return A {@link ChatRequest} corresponding to the parsed XML.
   * @throws RequestProcessingException Thrown if there is a problem converting the XML into a
   * {@link ChatRequest}.
   */
  ChatRequest fromXml(InputStream xmlInputStream, User author) throws RequestProcessingException;

}
