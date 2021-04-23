import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Responsible for handling input from the terminal.
 */
public class TerminalInputHandler implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(TerminalInputHandler.class.getName());

  private final InputStream inputStream;
  private final ChatMediator chatMediator;

  /**
   * Creates a new handler, responsible for processing input from the terminal.
   * @param inputStream The input stream from the terminal.
   * @param chatMediator The chat group that will be affected by terminal commands.
   */
  public TerminalInputHandler(InputStream inputStream, ChatMediator chatMediator) {
    this.inputStream = Objects.requireNonNull(inputStream, "Input stream is null");
    this.chatMediator = Objects.requireNonNull(chatMediator, "Chat mediator is null");
  }

  @Override
  public void run() {
    try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream))) {
      // Keep listening for input, line by line
      String input;
      while ((input = inputReader.readLine()) != null && !input.equals("EXIT")) {
        // Input is not recognised, so continue looping
        System.err.println("Unknown command: \"" + input + "\". Did you mean EXIT?");
      }
      // End the loop as "EXIT" was entered
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to listen to server cmd input. Closing down server");
    }
    // Start shutting down the server
    initiateServerTermination();
  }

  private void initiateServerTermination() {
    TerminateRequest terminateRequest = new TerminateRequest();
    try {
      BlockingChatRequestProcessor.getInstance().process(terminateRequest, chatMediator);
    } catch (RequestProcessingException e) {
      LOGGER.log(Level.SEVERE, "Failed to terminate server", e);
    }
  }
}
