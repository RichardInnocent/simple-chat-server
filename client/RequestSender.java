import java.io.PrintWriter;
import java.util.Objects;

/**
 * Responsible for sending requests to the server.
 */
public class RequestSender {

  private final ReflectiveXmlWriter xmlWriter = ReflectiveXmlWriter.getInstance();

  private final PrintWriter output;

  /**
   * Creates a new sender, responsible for sending requests to the server.
   * @param output The mechanism through which data should be sent to the server.
   * @throws NullPointerException Thrown if {@code output == null}.
   */
  public RequestSender(PrintWriter output) throws NullPointerException {
    this.output = Objects.requireNonNull(output, "Output is null");
  }

  /**
   * Converts the given {@code request} to XML, and then sends it to the server.
   * @param request The data to send.
   * @throws RequestSendingException Thrown if there is a problem sending the request.
   */
  public void send(Object request) throws RequestSendingException {
    try {
      // Convert the request to XML
      String xml = xmlWriter.toXml(request);

      // Send the request to the server
      output.println(xml);
    } catch (XmlParseException e) {
      throw new RequestSendingException(
          "Failed to create XML for object type " + request.getClass(), e
      );
    }
  }

}
