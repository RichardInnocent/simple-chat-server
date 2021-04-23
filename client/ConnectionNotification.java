import java.io.PrintWriter;
import java.util.Objects;

/**
 * A notification to indicate that a new user has joined the chat.
 */
public class ConnectionNotification implements ChatNotification {

  private String username;

  @Override
  public void process(ChatClient chatClient, PrintWriter cmdOutput) {
    // Print the details out to the console, in the user's colour.
    Colour newUserColour = SynchronizedUserColourMapper.getInstance().getColour(username);

    String prefix = Objects.equals(username, chatClient.getUsername()) ? "You" : username + " has";

    cmdOutput.println(newUserColour.wrapText(prefix) + " joined the chat!");
    cmdOutput.flush();
  }

  /**
   * Gets the username of the user that joined.
   * @return The username of the user that joined.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user that joined.
   * @param username The username of the user that joined.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConnectionNotification)) {
      return false;
    }
    ConnectionNotification that = (ConnectionNotification) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
