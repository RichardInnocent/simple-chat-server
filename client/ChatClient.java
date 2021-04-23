import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <h1>Overview</h1>
 * <p>This client is a simple terminal-based client design for use with the Simple Chat Server.</p>
 *
 * <h1>Running the Client</h1>
 * <p>The client can be run on the command line as follows:
 * <pre><code>java ChatClient</code></pre>
 * It can also take several command line arguments. These are described in the table below.
 * <table>
 *   <tr>
 *     <th>Parameter</th>
 *     <th>Description</th>
 *     <th>Required?</th>
 *   </tr>
 *   <tr>
 *     <td>cca</td>
 *     <td>Chat client address - the hostname of the <em>server</em> that the client should try to
 *     connect to.</td>
 *     <td>No. If unspecified, this will be localhost.</td>
 *   </tr>
 *   <tr>
 *     <td>ccp</td>
 *     <td>Chat client port - the port of the <em>server</em> that the client should try to connect
 *     to.</td>
 *     <td>No. If unspecified, this will be 14001.</td>
 *   </tr>
 * </table>
 * For example, to connect the client to a server located at 192.168.1.14:8080, the client would be
 * started as follows:
 * <pre><code>java ChatClient -cca 192.168.1.14 -ccp 8080</code></pre>
 * </p>
 * <h1>Interacting with the Client</h1>
 * <p>The client will prompt for a username at start-up. If the username is deemed unacceptable by
 * the server, the user will be asked to enter a different username. Once this has been specified,
 * the user can begin to send messages to other users in the group. By default, messages will be
 * sent to all participants, although this can be modified through the use of the @ character. If
 * placed at the start of the input, the @ character can be followed by one or more users (separated
 * by commas) to send the message privately. For an example, consider the examples shown in the
 * table below.
 * <table>
 *   <tr>
 *     <th>Input</th>
 *     <th>Message</th>
 *     <th>Recipients</th>
 *   </tr>
 *   <tr>
 *     <td>Hello world!</td>
 *     <td>Hello world!</td>
 *     <td>All users in the group</td>
 *   </tr>
 *   <tr>
 *     <td>@Ben Hello!</td>
 *     <td>Hello!</td>
 *     <td>Ben</td>
 *   </tr>
 *   <tr>
 *     <td>@Ben,Alice Hello!</td>
 *     <td>Hello!</td>
 *     <td>Ben, Alice</td>
 *   </tr>
 * </table>
 * </p>
 * <p>The user can enter text and receive messages at the same time.</p>
 * <p>Assuming that the terminal this is used on is ANSI-compatible, the client uses colours to
 * help users to quickly identify the senders of messages. These colours aren't meant to be unique,
 * but can help to distinguish users nonetheless. A complete list of the colours used by this client
 * implementation can be found in {@link Colour}.</p>
 *
 * <h1>Closing the Client</h1>
 * <p>The client can be closed by pressing Ctrl + C.</p>
 */
public class ChatClient extends SimpleHttpClient {

  private final InputStream cmdInput;
  private final PrintWriter cmdOutput;
  private final AtomicReference<ClientState> state =
      new AtomicReference<>(ClientState.DISCONNECTED);
  private final AtomicReference<String> username = new AtomicReference<>();

  /**
   * Starts the client.
   * @param args The command line arguments.
   * @see ChatClient
   */
  public static void main(String[] args) {
    CommandLineArgumentParser argumentParser = new CommandLineArgumentParser(args);
    String hostname = argumentParser.getParameter("cca").orElse("localhost");
    int port = argumentParser
        .getParameter("ccp")
        .map(ChatClient::parsePortNumber)
        .orElse(14_001); // Default to 14,001 if not specified
    new ChatClient(hostname, port).run();
  }

  private static int parsePortNumber(String portNumberText) throws IllegalArgumentException {
    try {
      // Make sure that the port number is a positive integer
      int value = Integer.parseInt(portNumberText);
      if (value < 1) {
        // I'm sure there are much better rules for this, but I can let the OS handle that
        throw new IllegalArgumentException("Value for ccp must be greater than 1");
      }
      return value;
    } catch (NumberFormatException e) {
      throw new NumberFormatException("Value for ccp must be an integer");
    }
  }

  /**
   * Creates a new client, looking for a server at the provided location. User input will be
   * collected via {@link System#in} and output displayed via {@link System#out}.
   * @param hostname The hostname of the server.
   * @param port The port that the server will accept connections on.
   * @throws NullPointerException Thrown if {@code hostname == null}.
   * @throws IllegalArgumentException Thrown if {@code port} is not a positive integer.
   */
  public ChatClient(String hostname, int port)
      throws NullPointerException, IllegalArgumentException {
    this(hostname, port, System.in, System.out);
  }

  /**
   * Creates a new client, looking for a server at the provided location.
   * @param address The hostname of the server.
   * @param port The port that the server will accept connections on.
   * @param inputStream The input stream to listen for user input on.
   * @param outputStream The output stream that output shout be printed to.s
   * @throws NullPointerException Thrown if {@code hostname == null}, {@code inputStream == null},
   * or {@code outputStream == null}.
   * @throws IllegalArgumentException Thrown if {@code port} is not a positive integer.
   */
  public ChatClient(String address, int port, InputStream inputStream, OutputStream outputStream)
      throws NullPointerException, IllegalArgumentException {
    super(address, port);
    this.cmdInput = Objects.requireNonNull(inputStream, "Input stream is null");
    this.cmdOutput = new PrintWriter(outputStream, true);
  }

  /**
   * Gets the current state of the client.
   * @return The current state of the client.
   */
  public ClientState getState() {
    return state.get();
  }

  /**
   * Sets the current state of the client.
   * @param newState The current state of the client.
   */
  public void setState(ClientState newState) {
    this.state.set(newState);
  }

  /**
   * Sets the username of the user using the client.
   * @return The username of the user using the client.
   */
  public String getUsername() {
    return username.get();
  }

  /**
   * Gets the username of the user using the client.
   * @param username The username of the user using the client.
   */
  public void setUsername(String username) {
    this.username.set(username);
  }

  @Override
  protected void run(BufferedReader socketInput, PrintWriter socketOutput) {
    InputHandler inputHandler = new ClientInputHandler(cmdInput, cmdOutput, this, socketOutput);

    NotificationHandler notificationHandler =
        new NotificationHandler(socketInput, cmdOutput, this);

    // Make sure that the input handler is closed if the connection to the server drops
    addOnCloseActionToNotificationHandler(notificationHandler, inputHandler);

    // Start the input thread
    new Thread(inputHandler).start();

    cmdOutput.println("Welcome to Simple Chat.");
    cmdOutput.println("Type help at any point for instructions on how to use this service.");
    cmdOutput.println("To connect to the chat, please enter your username");

    // Start listening for notifications
    notificationHandler.run();
  }

  /**
   * Ensures that the input handler is instructed to close if the connection to the server drops.
   */
  private void addOnCloseActionToNotificationHandler(
      NotificationHandler notificationHandler, InputHandler inputHandler) {
    // Unfortunately, trying to terminate terminal input thread by closing the input stream doesn't
    // work, so the user must provide one final action to close the program.
    // See https://stackoverflow.com/questions/67110761/closing-infinite-inputstream-when-reading-in-separate-thread-causes-deadlock
    notificationHandler.addOnCloseAction(
        () -> {
          cmdOutput.println("Session over. Press enter to quit");
          inputHandler.close();
        }
    );
  }

}
