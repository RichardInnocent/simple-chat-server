import java.io.PrintWriter;
import java.util.Objects;

/**
 * Responsible for sending requests to the server in XML format.
 */
public class XmlRequestSender implements RequestSender {

  private final ReflectiveXmlWriter xmlWriter = ReflectiveXmlWriter.getInstance();

  private final PrintWriter output;

  /**
   * Creates a new instance, responsible for sending requests to the server in XML format.
   * @param output The mechanism used to deliver data to the server.
   */
  public XmlRequestSender(PrintWriter output) {
    this.output = Objects.requireNonNull(output, "Output is null");
  }

  @Override
  public void send(Object request) throws RequestSendingException {
    try {
      // Convert the data to XML
      String xml = xmlWriter.toXml(request);

      // Send the data
      output.println(xml);
      output.flush();
    } catch (XmlParseException e) {
      throw new RequestSendingException(
          "Failed to create XML for object type " + request.getClass()
      );
    }
  }
}
