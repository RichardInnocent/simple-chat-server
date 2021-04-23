import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h1>Overview</h1>
 * <p>The Chat Server is a server that users can connect to so that they can chat with their colleagues
 * or friends.</p>
 *
 * <h1>Running the Server</h1>
 * The chat server can be run on the command line as follows:
 * <pre><code>java ChatServer</code></pre>
 * <p>It can also take several command line arguments. These are described in the table below.
 * <table>
 *   <tr>
 *     <th>Parameter</th>
 *     <th>Description</th>
 *     <th>Required?</th>
 *   </tr>
 *   <tr>
 *     <td>csp</td>
 *     <td>The port that the server should try to listen for connections on.</td>
 *     <td>No. If unspecified, this will be 14001.</td>
 *   </tr>
 * </table>
 * For example, to start the server on port 14002, the server would be started as follows:
 * <pre><code>java ChatServer -csp 14002</code></pre></p>
 *
 * <h1>Data Transfer Protocol</h1>
 * <p>All data sent to the server (referred to in future as "chat requests") from clients is
 * expected to be in XML format, in the form of one of the following request types:
 * <ul>
 *   <li>{@link ConnectionRequest}</li>
 *   <li>{@link ChatEntryRequest}</li>
 * </ul>
 * The server will also send data to the clients (herein referred to as notifications) in XML
 * format, matching in one of the following types:
 * <ul>
 *   <li>{@link ChatEntryNotification}</li>
 *   <li>{@link ConnectionNotification}</li>
 *   <li>{@link DisconnectNotification}</li>
 *   <li>{@link RequestFailedNotification}</li>
 *   <li>{@link SystemNotification}</li>
 * </ul>
 *
 * <h1>Interacting with the Server</h1>
 * This implementation of the chat server has only one group that users can connect to. After
 * establishing a successful connection, clients are expected to send a {@link ConnectionRequest},
 * stating the username that they would like to use. They will not receive notifications until this
 * process is completed. Usernames must be distinct, so username requests specifying username that
 * are already in use in the group will be rejected.</p>
 * <p>Once a username has been specified, the user will begin to receive notifications. The client
 * can also send messages to other users in the group in the form of a {@link ChatEntryRequest}.
 * These requests can be sent publicly (to all recipients), or privately.</p>
 * <p>When a user disconnects, they will be removed from the group and all other users will be
 * notified that they have left.</p>
 * <p>This server is synchronous - all requests received (or produced by the server internally) are
 * processed sequentially.</p>
 * <h1>Closing the Server</h1>
 * The server can be closed by typing the command "EXIT", or by pressing Ctrl + C.
 */
public class ChatServer {

  private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());

  private final int port;
  private final ChatMediator chatMediator = new ChatGroup();
  private final SynchronizedUserPool userPool = new SynchronizedUserPool(chatMediator);
  private final AtomicBoolean shutdown = new AtomicBoolean(false);

  /**
   * Starts the server
   * @param args The arguments to start the server with. The server only expects one (named)
   * parameter - {@code csp}. This specifies the port number. If unspecified, the port will be set
   * to 14,001. To specify the server port, {@code args} must contain the value {@code -csp},
   * followed by the desired port number. For example, this would be a valid configuration to start
   * the server on port 8080:
   * <pre><code>new String[]{"-csp", "8080"}</code></pre>
   * @throws IOException Thrown if there is a problem with the server socket.
   */
  public static void main(String[] args) throws IOException {

    // Parse the args and get the port number to start on
    CommandLineArgumentParser argumentParser = new CommandLineArgumentParser(args);
    int serverPort = argumentParser
        .getParameter("csp")
        .map(ChatServer::parsePortNumber)
        .orElse(14_001); // Default to 14,001 if not specified

    // Start the server on the given port
    new ChatServer(serverPort).run();
  }

  private static int parsePortNumber(String portNumberText) throws IllegalArgumentException {
    try {
      // Make sure that the port number is a positive integer
      int value = Integer.parseInt(portNumberText);
      if (value < 1) {
        // I'm sure there are much better rules for this, but I can let the OS handle that
        throw new IllegalArgumentException("Value for csp must be greater than 1");
      }
      return value;
    } catch (NumberFormatException e) {
      throw new NumberFormatException("Value for csp must be an integer");
    }
  }

  /**
   * Creates a new chat server. When the {@link #run()} method is called, it will start on the given
   * port.
   * @param port The port to listen on.
   */
  public ChatServer(int port) {
    this.port = port;
  }

  /**
   * Starts the server.
   * @throws IOException Thrown if there is a problem with the server socket.
   */
  public void run() throws IOException {
    // Start the thread to listen to cmd input
    new Thread(new TerminalInputHandler(System.in, chatMediator)).start();

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      addCloseAction(serverSocket);
      LOGGER.info("Server started on port " + port);

      // Listen for incoming connections
      boolean listening = true;
      while (listening) {
        try {
          // This blocks
          listenAndProcessConnections(serverSocket);
        } catch (SocketException e) {
          // A socket is exception is expected if a server shutdown has been initiated
          if (!shutdown.get()) {
            LOGGER.log(Level.SEVERE, "Failed to accept incoming connections", e);
          }
          listening = false;
        }
      }
    }
  }

  private void addCloseAction(ServerSocket serverSocket) {

    // When the chat group closes, block new connection from being received by closing the server
    // socket.
    chatMediator.addOnCloseAction(() -> {
      try {
        shutdown.set(true);
        serverSocket.close();
      } catch (IOException e) {
        LOGGER.log(
            Level.WARNING, "Failed to close server socket cleanly. Initiating forced shut down", e
        );
        System.exit(1);
      }
    });
  }

  private void listenAndProcessConnections(ServerSocket serverSocket) throws IOException {
    // This blocks, so adding a condition to the while loop for a shutdown request would mean
    // that the the server only shuts down after a new socket is created.
    Socket socket = serverSocket.accept();
    RemoteUser newlyConnectedUser = new RemoteUser(chatMediator, socket);
    newlyConnectedUser.startListening();
    userPool.addUser(newlyConnectedUser);
  }
}
