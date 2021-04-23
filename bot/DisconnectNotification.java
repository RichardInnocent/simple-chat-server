import java.util.Objects;
import java.util.logging.Logger;

/**
 * A notification to indicate that a user has disconnected from the chat group.
 */
public class DisconnectNotification implements ChatNotification {

  private static final Logger LOGGER = Logger.getLogger(DisconnectNotification.class.getName());

  private String username;

  @Override
  public void process(RequestSender requestSender, ChatResponseFactory chatResponseFactory) {
    LOGGER.info(username + " has left the chat");
  }

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
