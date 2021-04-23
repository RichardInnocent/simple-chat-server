import java.util.Objects;

/**
 * A notification to indicate that a user has disconnected from the chat group.
 */
public class DisconnectNotification {

  private final String username;

  /**
   * Creates a new notification to indicate that a user has disconnected from the chat group.
   * @param username The username of the user that disconnected.
   * @throws NullPointerException Thrown if {@code username == null}.
   */
  public DisconnectNotification(String username) throws NullPointerException {
    this.username = Objects.requireNonNull(username, "Username is null");
  }

  /**
   * Gets the username of the user that disconnected.
   * @return The username of the user that disconnected.
   */
  public String getUsername() {
    return username;
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
