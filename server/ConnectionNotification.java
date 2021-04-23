import java.util.Objects;

/**
 * A notification to indicate that a user has joined the chat.
 */
public class ConnectionNotification {

  private final String username;

  /**
   * Creates a new notification to indicate that a user has joined the chat.
   * @param username The username of the user that joined.
   */
  public ConnectionNotification(String username) {
    this.username = Objects.requireNonNull(username, "Username is null");
  }

  /**
   * Gets the username of the user that joined the chat.
   * @return The username of the user that joined the chat.
   */
  public String getUsername() {
    return username;
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
