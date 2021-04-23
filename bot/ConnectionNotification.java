import java.util.Objects;
import java.util.logging.Logger;

/**
 * A notification to indicate that a new user has joined the chat.
 */
public class ConnectionNotification implements ChatNotification {

  private static final Logger LOGGER = Logger.getLogger(ConnectionNotification.class.getName());

  private String username;

  @Override
  public void process(RequestSender requestSender, ChatResponseFactory chatResponseFactory) {
    // Just log this - there's no need for further action
    LOGGER.info(username + " has joined the chat");
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
