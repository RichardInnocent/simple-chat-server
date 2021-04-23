import java.io.PrintWriter;
import java.util.Objects;

/**
 * A notification to indicate that a user has disconnected from the chat group.
 */
public class DisconnectNotification implements ChatNotification {

  private static final UserColourMapper COLOUR_MAPPER = SynchronizedUserColourMapper.getInstance();

  private String username;

  /**
   * Gets the username of the user that disconnected.
   * @return The username of the user that disconnected.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user that disconnected.
   * @param username The username of the user that disconnected.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public void process(ChatClient chatClient, PrintWriter cmdOutput) {
    // Display the information
    Colour colour = COLOUR_MAPPER.getColour(username);
    cmdOutput.println(colour.wrapText(username) + " has left the chat");

    // Clean up so we don't have a memory leak
    COLOUR_MAPPER.removeMapping(username);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DisconnectNotification)) {
      return false;
    }
    DisconnectNotification that = (DisconnectNotification) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
