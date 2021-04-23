import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a user that connects remotely to the server.
 */
public class RemoteUser extends AbstractUser {

  private static final Logger LOGGER = Logger.getLogger(RemoteUser.class.getName());

  // The number that is appended to the thread name. This doesn't need to be unique, so it can
  // overflow, but it helps to keep keep the thread names distinct for tracking purposes.
  private static final AtomicInteger THREAD_INDEX = new AtomicInteger();

  private final Collection<Consumer<User>> onCloseActions = new ArrayList<>();
  private final Thread inputThread;
  private final Socket socket;

  private boolean closed = false;

  /**
   * Creates a new user that has connected remotely to the server.
   * @param chatMediator The chat group that the user will belong to.
   * @param socket The IO socket that will handle inbound and outbound data transfer between the
   * user and the server.
   * @throws IOException Thrown if there is a initialising a reader for the provided socket.
   */
  public RemoteUser(ChatMediator chatMediator, Socket socket) throws IOException {
    super(chatMediator);
    this.socket = Objects.requireNonNull(socket, "Socket is null");
    this.inputThread = new Thread(
        new ChatInputHandler(this, socket.getInputStream()),
        "remote-user-thread" + THREAD_INDEX.getAndIncrement()
    );
  }

  /**
   * Starts listening to client input on the provided socket.
   */
  public void startListening() {
    inputThread.start();
  }

  @Override
  public void notifyOfEvent(Object chatRequest) {
    try {
      // Write out the request to XML
      String xml = ReflectiveXmlWriter.getInstance().toXml(chatRequest);
      PrintWriter printStream = new PrintWriter(socket.getOutputStream(), true);

      // Send the XML to the client
      // Responses are always handled one line at a time to improve the stability of the XML parsing
      printStream.println(xml);
    } catch (SocketException e) {
      handleSocketException(e);
    } catch (Exception e) {
      // Any other exception we should log, but not necessarily disconnect the user
      LOGGER.log(Level.SEVERE, "Could not notify user " + getUsername(), e);
    }
  }

  // Logs the error and attempts to disconnect the user if the chat group isn't closed
  private void handleSocketException(SocketException e) {
    LOGGER.log(
        Level.WARNING,
        "Could not notify user " + getUsername()
            + " due to a socket connection problem. User will be disconnected",
        e
    );
    // Exception expected if mediator is closed
    if (!getChatMediator().isClosed()) {
      // Couldn't send data down the user's socket, implying the socket may have dropped. Attempt to
      // disconnect the user.
      initiateGracefulDisconnect();
    }
  }

  /**
   * Attempts to gracefully disconnect the user.
   */
  private void initiateGracefulDisconnect() {
    DisconnectRequest disconnectRequest = new DisconnectRequest();
    disconnectRequest.setAuthor(this);
    try {
      BlockingChatRequestProcessor.getInstance().process(disconnectRequest, getChatMediator());
    } catch (RequestProcessingException e) {
      LOGGER.log(
          Level.SEVERE,
          "User " + getUsername()
              + " appears to be disconnected but could not be removed from the chat group",
          e
      );
    }
  }

  @Override
  public void close() {
    if (!isClosed()) {
      try {
        // Attempt to close the socket
        socket.close();
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Failed to close socket for user " + getUsername(), e);
      }
      // The user was closed for the first time so run the on close actions
      onCloseActions.forEach(action -> action.accept(this));
      closed = true;
    }
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public synchronized void onClose(Consumer<User> onCloseFunction) {
    onCloseActions.add(onCloseFunction);
  }
}
