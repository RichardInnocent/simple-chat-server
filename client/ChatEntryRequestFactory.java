import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Builds {@link ChatEntryRequest}s based on user input.
 */
public class ChatEntryRequestFactory {

  /**
   * Takes user input and transforms it into a {@link ChatEntryRequest}. For most input
   * combinations, the result will be a request where the message matches the input, and the
   * recipients is a blank array. However, the <code>@</code> character can be used to specify the
   * intended recipients if placed at the start of a string. See the table below for examples.
   * <table>
   *   <tr><th>Input</th><th>Message</th><th>Recipients</th></tr>
   *   <tr><td>Hello world!</td><td>Hello world!</td><td>{@code []}</td></tr>
   *   <tr><td>@Ben Hello world!</td><td>Hello world!</td><td>{@code ["Ben"]}</td></tr>
   *   <tr><td>@Ben,Emma Hello world!</td><td>Hello world!</td><td>{@code ["Ben", "Emma"]}</td></tr>
   *   <tr><td>@Shubh,,Anya, Hello world!</td><td>Hello world!</td><td>{@code ["Shubh", "Anya"]}
   *   </td></tr>
   *   <tr><td>Hello @Ben!</td><td>Hello @Ben!</td><td>{@code []}</td></tr>
   * </table>
   * @param input The user's input.
   * @return An appropriate {@link ChatEntryRequest}.
   * @throws InputFormatException Thrown if the input is ambiguous as to whether participants should
   * be specified.
   */
  public ChatEntryRequest build(String input) throws InputFormatException {
    return input.startsWith("@") ? buildPrivateMessage(input) : new ChatEntryRequest(input);
  }

  private ChatEntryRequest buildPrivateMessage(String input) throws InputFormatException {
    String[] components = input.substring(1).split("\\s+", 2);

    if (components.length < 2) {
      throw new InputFormatException("No message specified");
    }

    Set<String> targetedUsers = getTargetUsernames(components[0]);
    targetedUsers.removeIf(user -> user == null || user.isBlank());
    String message = components[1];

    if (message.isBlank()) {
      throw new InputFormatException("No message specified");
    }

    return new ChatEntryRequest(message, targetedUsers);
  }

  private Set<String> getTargetUsernames(String targetUsers) throws InputFormatException {
    Set<String> users = new HashSet<>(Arrays.asList(targetUsers.split(",")));
    if (users.isEmpty()) {
      throw new InputFormatException("No users specified");
    }
    return users;
  }

}
