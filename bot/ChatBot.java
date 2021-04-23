import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * <h1>Overview</h1>
 * <p>The Simple Chat Bot is a bot client designed for use with the Simple Chat Server.</p>
 *
 * <h1>Running the Bot</h1>
 * <p>The bot can be run on the command line as follows:
 * <pre><code>java ChatBot [args]</code></pre>
 * It takes several command lines arguments. These are described in the table below.
 * <table>
 *   <tr>
 *     <th>Parameter</th>
 *     <th>Description</th>
 *     <th>Required?</th>
 *   </tr>
 *   <tr>
 *     <td>f</td>
 *     <td>The path to the responses file (explained in the "Response File" section).</td>
 *     <td>Yes. The path must lead to a valid file.</td>
 *   </tr>
 *   <tr>
 *     <td>n</td>
 *     <td>Name - the username of the bot. This must be unique on the chat group that the bot
 *     connects to, and the bot will automatically close if the name is not deemed appropriate by
 *     the server.</td>
 *     <td>No. If unspecified, this will be chatbot.</td>
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
 * For example, to connect the client to a server located at 192.168.1.14:8080, connecting with the
 * name "TestBot" and using a response file location at conf/scripts/twitch-style.csv, the bot
 * would be started as follows:
 * <pre><code>java ChatBot -cca 192.168.1.14 -ccp 8080 -n TestBot -f conf/scripts/twitch-style.csv</code></pre>
 * </p>
 * <h1>Interacting with the Bot</h1>
 * <p>Once the bot has successfully connected to a chat group, it will respond reactively to user
 * input.</p>
 * <h2>Responses File</h2>
 * <p>A responses file must be provided to instruct the bot on when and how to respond to input
 * from other users in the chat group. A valid response file is a headerless CSV file consisting
 * of two columns: input and output. The input column defines the user input that is expected. To
 * trigger the bot to respond, the input must be an exact (i.e. case-sensitive) match. In turn, the
 * bot will respond with whatever is defined in the output column.</p>
 * <h3>Response Variables</h3>
 * <p>The response file is not limited strictly to pure text. Sometimes, an appropriate response
 * may change over time, or change according to which user is prompting the response. These
 * variables can be included in the response file, surrounded by percentage symbols. The variables
 * that the system currently supports are described below.
 * <table>
 *   <tr>
 *     <th>Variable name</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@code username}</td>
 *     <td>The username of the user that triggered the response.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code currentTime}</td>
 *     <td>The current time on the machine where the bot is running.</td>
 *   </tr>
 * </table>
 * </p>
 */
public class ChatBot {

  private static final Logger LOGGER = Logger.getLogger(ChatBot.class.getName());

  private final String serverAddress;
  private final int serverPort;
  private final String botName;
  private final String responsesFilePath;

  /**
   * Creates a new chat bot.
   * @param serverHostname The hostname of the chat server that the bot will attempt to connect to.
   * @param serverPort The port of the chat server that the bot will attempt to connect to.
   * @param botName The username of the chat bot, as it should appear in the chat.
   * @param responsesFilePath The file path to the file that contains the bot's response
   * specification.
   * @throws NullPointerException Thrown if {@code serverHostname == null}, {@code botName == null}
   * or {@code responsesFilePath == null}.
   */
  public ChatBot(
      String serverHostname, int serverPort, String botName, String responsesFilePath
  ) throws NullPointerException {
    this.serverAddress = Objects.requireNonNull(serverHostname, "Hostname is null");
    this.serverPort = serverPort;
    this.botName = Objects.requireNonNull(botName, "Bot name is null");
    this.responsesFilePath = Objects.requireNonNull(responsesFilePath, "Responses file path is null");
  }

  /**
   * Starts the bot.
   * @throws Exception Thrown if there is a problem running the bot.
   */
  public void start() throws Exception {
    try (Socket socket = new Socket(serverAddress, serverPort)) {
      runBot(socket);
    }
  }

  /**
   * Runs the bot.
   */
  private void runBot(Socket socket) throws Exception {
    RequestSender requestSender =
        new XmlRequestSender(new PrintWriter(socket.getOutputStream(), true));

    // Send the request to specify the username. If this fails, the program will terminate.
    ConnectionRequest connectionRequest = new ConnectionRequest(botName);
    requestSender.send(connectionRequest);

    ChatResponseFactory responseFactory = new CsvBasedChatResponseFactory(responsesFilePath);

    NotificationHandler notificationHandler = new NotificationHandler(
        socket.getInputStream(),
        requestSender,
        responseFactory,
        StandardChatNotificationParser.getInstance()
    );

    LOGGER.info("Bot running with username \"" + botName + "\". Press Ctrl + C to quit.");

    // Start listening for notifications and respond as appropriate. This blocks.
    notificationHandler.run();

    LOGGER.info("Chat bot terminated");
  }

  public static void main(String[] args) throws Exception {
    CommandLineArgumentParser argumentParser = new CommandLineArgumentParser(args);

    String hostname = argumentParser.getParameter("cca").orElse("localhost");

    int portNumber = argumentParser
        .getParameter("ccp")
        .map(ChatBot::parsePortNumber)
        .orElse(14_001);

    String botUsername = argumentParser
        .getParameter("n")
        .orElse("chatbot");

    String responsesFilePath = argumentParser
        .getParameter("f")
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No script provided. Please add a -f flag, followed by a path to a responses "
                        + "file"
                )
        );

    // Start the chat bot
    new ChatBot(hostname, portNumber, botUsername, responsesFilePath).start();
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
}
