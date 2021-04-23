import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the receiving of data from a user. Note that the {@link #run()} implementation is
 * blocking, so this should be run on its own thread.
 */
public class ChatInputHandler implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(ChatInputHandler.class.getName());

  private final User user;
  private final InputStream inputStream;

  /**
   * Creates a new connection handler.
   * @param user The user that the handler is associated with. All actions received from the given
   * {@link InputStream} will be assumed to be from this user.
   * @param inputStream The input stream that sends data to the server. Requests are read one line
   * at a time, so it's expected that the input stream conforms with this convention - sending a
   * request without a terminating line break will not be processed as expected.
   */
  public ChatInputHandler(User user, InputStream inputStream) {
    this.user = Objects.requireNonNull(user, "User is null");
    this.inputStream = Objects.requireNonNull(inputStream, "Input stream is null");
  }

  @Override
  public void run() {
    try {
      handleAndThrowErrors();
    } catch (IOException e) {
      handleInputException(e);
    }
  }

  private void handleAndThrowErrors() throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String text;

      // Keep reading from the input stream
      while ((text = reader.readLine()) != null) {
        if (text.isBlank()) {
          System.out.println("From socket: " + text);
          // Ignore blank lines
          continue;
        }

        // When we hit some interesting data, parse it and try to process it
        parseRequest(text).ifPresent(this::processRequest);
      }
    }
  }

  private Optional<ChatRequest> parseRequest(String text) {
    try {
      return Optional.of(StandardChatRequestParser.getInstance().fromXml(text, user));
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to parse message", e);

      // Notify the user that their message could not be parsed
      RequestFailedNotification requestFailedNotification =
          new RequestFailedNotification("Unknown", "Failed to parse request");
      user.notifyOfEvent(requestFailedNotification);
      return Optional.empty();
    }
  }

  private void processRequest(ChatRequest chatRequest) {
    try {
      // Ensure that only one request is responded to at a time
      BlockingChatRequestProcessor.getInstance().process(chatRequest, user.getChatMediator());
    } catch (Exception e) {
      String username = user.getUsername() == null ? "Unknown user" : user.getUsername();
      LOGGER.log(Level.WARNING, "Failed to process message from " + username, e);
      String message = e.getMessage() == null ? "Error" : e.getMessage();

      // Let the user know that their request could not be processed
      RequestFailedNotification requestFailedNotification =
          new RequestFailedNotification(chatRequest.getClass(), message);
      user.notifyOfEvent(requestFailedNotification);
    }
  }

  private void handleInputException(IOException e) {
    // IOException is most likely thrown when the chat mediator closes and terminates the socket. In
    // this case, we don't need to take any action as this is the expected behaviour.

    // Check if the mediator is closed
    if (!user.getChatMediator().isClosed()) {
      // Mediator is closed - log exception and gracefully disconnect the user from the group.
      LOGGER.log(
          Level.WARNING,
          "There was a problem reading the input stream for user " + user.getUsername()
              + ". User will be disconnected",
          e
      );

      disconnectUser();
    }
  }

  private void disconnectUser() {
    DisconnectRequest disconnectRequest = new DisconnectRequest();
    disconnectRequest.setAuthor(user);

    try {
      BlockingChatRequestProcessor
          .getInstance().process(disconnectRequest, user.getChatMediator());
    } catch (RequestProcessingException e) {
      LOGGER.log(
          Level.WARNING, "Failed to notify users that " + user.getUsername() + " disconnected", e
      );
    }
  }

}
