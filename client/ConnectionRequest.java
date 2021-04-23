import java.util.Objects;

/**
 * Represents a request to the server to connect to a chat group.
 */
public class ConnectionRequest {

  private final String username;

  /**
   * Creates a new request to the server to connect to a chat group.
   * @param username The desired username of the connecting user.
   * @throws NullPointerException Thrown if {@code username == null}.
   */
  public ConnectionRequest(String username) throws NullPointerException {
    this.username = Objects.requireNonNull(username, "Username is null");
  }

  /**
   * Gets the desired username of the connecting user.
   * @return The desired username of the connecting user.
   */
  public String getUsername() {
    return username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConnectionRequest)) {
      return false;
    }
    ConnectionRequest that = (ConnectionRequest) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
