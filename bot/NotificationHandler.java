import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles notifications received from the server.
 */
public class NotificationHandler implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(NotificationHandler.class.getName());

  private final InputStream socketInputStream;
  private final RequestSender requestSender;
  private final ChatResponseFactory responseFactory;
  private final ChatNotificationParser notificationParser;

  /**
   * Creates a new handler to process notifications received from the server.
   * @param socketInputStream The input from the socket connection to the server. This is where
   * new notifications will be read from.
   * @param requestSender The mechanism through which responses can be sent to the server.
   * @param responseFactory Responsible for creating new responses based on notification input.
   * @param notificationParser Responsible for parsing notifications received from the server.
   * @throws NullPointerException Thrown if any of the arguments ar {@code null}.
   */
  public NotificationHandler(
      InputStream socketInputStream,
      RequestSender requestSender,
      ChatResponseFactory responseFactory,
      ChatNotificationParser notificationParser
  ) throws NullPointerException {
    this.socketInputStream =
        Objects.requireNonNull(socketInputStream, "Socket input stream is null");
    this.requestSender =
        Objects.requireNonNull(requestSender, "Request sender is null");
    this.responseFactory =
        Objects.requireNonNull(responseFactory, "Response factory is null");
    this.notificationParser =
        Objects.requireNonNull(notificationParser, "Notification parser is null");
  }

  @Override
  public void run() {
    try {
      runAndThrowExceptions();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to process input from server", e);
    }
  }

  private void runAndThrowExceptions() throws IOException {
    try {
      // Keep reading from the socket until we get disconnected
      continuallyProcessInputFromSocket();
    } catch (SocketException e) {
      LOGGER.info("Lost connection to server");
    }
  }

  private void continuallyProcessInputFromSocket() throws IOException {
    // Keep reading from the server line by line
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(socketInputStream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        try {
          // Parse the XML and process it
          notificationParser.fromXml(line).process(requestSender, responseFactory);
        } catch (XmlParseException e) {
          LOGGER.log(Level.WARNING, "Received notification could not be parsed", e);
        }
      }
    }
  }
}