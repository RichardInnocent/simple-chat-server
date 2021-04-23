import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Generates a map of responses to inputs based on a CSV file.
 */
public class CsvBasedChatResponseFactory implements ChatResponseFactory {

  private final Map<String, String> responseMap;
  private final VariableInjector variableInjector = new StandardVariableInjector();

  /**
   * Creates a new response factory according to the contents of the given file. It's expected that
   * the file displays information in the following format, with no headers:
   * <pre><code>{input},{response}</code></pre>
   * For example:
   * <pre><code>hello,Hello there!<br>bye,Goodbye!<br>Who are you?,I'm a chat bot!</code></pre>
   * @param filePath The file path to the CSV file.
   * @throws IOException Thrown if there is a problem reading the file.
   */
  public CsvBasedChatResponseFactory(String filePath) throws IOException {
    responseMap = buildResponseMap(filePath);
  }

  /**
   * Reads all the lines of the CSV file and adds valid entry to a map of inputs to responses.
   */
  private Map<String, String> buildResponseMap(String filePath) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      Map<String, String> responseMap = new HashMap<>();
      String input;

      // Read every line
      while ((input = reader.readLine()) != null) {
        if (input.isBlank()) {
          continue; // Ignore as it's an empty line
        }

        String[] components = input.split(",", 2);
        if (components.length < 2) {
          continue; // Ignore as this lines looks to be incomplete
        }

        // Add to the map of inputs and responses
        responseMap.put(components[0], components[1]);
      }
      return responseMap;
    }
  }

  /**
   * Generates a response based on some input. It may be appropriate that no response is generated,
   * in which case the result will be an empty {@link Optional}.
   * @param notification The input message.
   * @return The response that should be sent to the chat.
   */
  @Override
  public Optional<String> getResponse(ChatEntryNotification notification) {
    return Optional.ofNullable(responseMap.get(notification.getMessage()))
                   .map(response -> variableInjector.injectVariables(response, notification));
  }
}
