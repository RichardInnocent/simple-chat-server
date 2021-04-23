import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles notifications received from the server.
 */
public class NotificationHandler implements Runnable, ManagedLifeCycle {

  private static final Logger LOGGER = Logger.getLogger(NotificationHandler.class.getName());

  private final BufferedReader socketInputReader;
  private final PrintWriter cmdOutput;
  private final StandardChatNotificationParser notificationParser =
      StandardChatNotificationParser.getInstance();
  private final ChatClient chatClient;
  private final Collection<Runnable> onCloseActions = new ArrayList<>();

  private boolean closed = false;

  /**
   * Creates a new handler to process notifications received from the server.
   * @param socketInputReader The input mechanism from the socket connection to the server. This is
   * where new notifications will be read from.
   * @param cmdOutput The output mechanism for displaying text to the user.
   * @param chatClient The client instance.
   * @throws NullPointerException Thrown if any of the arguments are {@code null}.
   */
  public NotificationHandler(
      BufferedReader socketInputReader, PrintWriter cmdOutput, ChatClient chatClient
  ) throws NullPointerException {
    this.socketInputReader =
        Objects.requireNonNull(socketInputReader, "Socket input reader is null");
    this.cmdOutput = Objects.requireNonNull(cmdOutput, "Cmd output is null");
    this.chatClient = Objects.requireNonNull(chatClient, "Chat client is null");
  }

  @Override
  public void run() {
    try {
      // Keep listening to the socket, process input line by line
      continuallyProcessInputFromSocket();
    } catch (SocketException e) {
      // If it's a socket exception, we've probably just lost a connection to the server.
      // Don't print out a massive stacktrace is this is probably expected
      cmdOutput.println("Lost connection to server");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to read input from socket", e);
    } finally {
      close();
    }
  }

  private void continuallyProcessInputFromSocket() throws IOException {
    String input;
    // Process the input line by line
    while ((input = socketInputReader.readLine()) != null) {
      try {
        // Parse the data as XML into a ChatNotification and then process it
        notificationParser.fromXml(input).process(chatClient, cmdOutput);
      } catch (XmlParseException e) {
        LOGGER.log(Level.WARNING, "Failed to parse notification", e);
      }
    }
  }

  @Override
  public void close() {
    try {
      attemptToClose();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to close socket input reader", e);
    }
  }

  private void attemptToClose() throws IOException {
    socketInputReader.close();
    closed = true;
    onCloseActions.forEach(Runnable::run);
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public void addOnCloseAction(Runnable action) {
    onCloseActions.add(action);
  }
}
