import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible for handling user input.
 */
public class ClientInputHandler implements InputHandler {

  private static final Logger LOGGER = Logger.getLogger(ClientInputHandler.class.getName());

  private static final String HELP_TEXT =
      "To chat to others in the group, simply type your message and then press enter to send the "
          + "message. To quit, press Ctrl + C." + System.lineSeparator().repeat(2) + "To message a "
          + "member privately, type @ followed by their username. For example, this would send the "
          + "message \"Hi\" to a user named Lucy only:" + System.lineSeparator() + "@Lucy Hi!"
          + System.lineSeparator().repeat(2) + "To send a message to multiple people at once, "
          + "type the @ symbol, followed by their usernames, all separated by single comma. Then "
          + "put some whitespace, then the message you want to send. For example, this would send "
          + "the message \"Hi guys!\" to the users Ben and Tom:" + System.lineSeparator()
          + "@Ben,Tom Hi guys!";

  private final InputStream cmdInputStream;
  private final PrintWriter cmdOutput;
  private final ChatClient chatClient;
  private final RequestSender requestSender;
  private final Collection<Runnable> onCloseActions = new ArrayList<>();
  private boolean closed = false;

  /**
   * Crates a new input handler which will handle user input.
   * @param cmdInputStream The method of user input.
   * @param cmdOutput The method of displaying information to the user.
   * @param chatClient The chat client instance.
   * @param socketOutput The connection to the server. This should be used if the input instructs
   * data to be sent to the server.
   * @throws NullPointerException Thrown if any of the parameters are {@code null}.
   */
  public ClientInputHandler(
      InputStream cmdInputStream, PrintWriter cmdOutput, ChatClient chatClient,
      PrintWriter socketOutput
  ) throws NullPointerException {
    this.cmdInputStream = Objects.requireNonNull(cmdInputStream, "Input stream is null");
    this.cmdOutput = Objects.requireNonNull(cmdOutput);
    this.chatClient = Objects.requireNonNull(chatClient, "Chat client is null");
    this.requestSender = new RequestSender(socketOutput);
  }

  /**
   * Continually listens for user input and processes it line by line.
   */
  @Override
  public void run() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(cmdInputStream));
      String input;
      while ((input = reader.readLine()) != null) {
        processInput(input);
      }
    } catch (IOException e) {
      // If closed, this is expected so don't log anything
      if (isOpen()) {
        LOGGER.log(Level.SEVERE, "Failed to retrieve terminal input", e);
      }
    } finally {
      close();
    }
  }

  /**
   * Processes a line of user input.
   */
  private void processInput(String input) {
    // Ignore blank lines
    if (input.isBlank()) {
      return;
    }

    // user wants to be shown the help text
    if (input.equals("help")) {
      cmdOutput.println(HELP_TEXT);
      return;
    }

    if (ClientState.CONNECTED.equals(chatClient.getState())) {
      // User is connected to a chat group so their input will be a message
      buildAndSendChatEntryRequest(input);
    } else {
      // User is not connected to a chat group so their input will be a username for a connection
      // request
      buildAndSendConnectionRequest(input);
    }
  }

  private void buildAndSendChatEntryRequest(String input) {
    try {
      ChatEntryRequest request = new ChatEntryRequestFactory().build(input);
      sendRequest(request);
    } catch (InputFormatException e) {
      cmdOutput.println("Invalid input entered. Reason: " + e.getMessage());
    }
  }

  private void buildAndSendConnectionRequest(String input) {
    ConnectionRequest connectionRequest = new ConnectionRequest(input);
    chatClient.setUsername(input); // Input is the username
    sendRequest(connectionRequest);
  }

  private void sendRequest(Object request) {
    try {
      requestSender.send(request);
      if (request instanceof ConnectionRequest) {
        // Assume the client is now connected (at least until the server informs otherwise)
        chatClient.setState(ClientState.CONNECTED);
      }
    } catch (RequestSendingException e) {
      LOGGER.log(Level.WARNING, "Failed to send request to server", e);
    }
  }

  @Override
  public void close() {
    if (isOpen()) {
      try {
        attemptToClose();
      } catch (IOException e) {
        LOGGER.severe("Failed to close input log");
      }
    }
  }

  private void attemptToClose() throws IOException {
    closed = true;
    cmdInputStream.close();
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
