import java.util.Objects;

/**
 * Represents a notification to clients sent by the system.
 */
public class SystemNotification {

  private final String message;

  /**
   * Creates a new notification to client sent by the system.
   * @param message The message to be delivered to the clients.
   * @throws NullPointerException Thrown if {@code message == null}.
   */
  public SystemNotification(String message) throws NullPointerException {
    this.message = Objects.requireNonNull(message, "Message is null");
  }

  /**
   * Gets the message that should be delivered to the clients.
   * @return The message that should be delivered to the clients.
   */
  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SystemNotification)) {
      return false;
    }
    SystemNotification that = (SystemNotification) o;
    return Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }
}
