import java.util.Objects;
import java.util.logging.Logger;

/**
 * Represents a generic notification from the server. System notifications are not associated with
 * any specific user.
 */
public class SystemNotification implements ChatNotification {

  private static final Logger LOGGER = Logger.getLogger(SystemNotification.class.getName());

  private String message;

  @Override
  public void process(RequestSender requestSender, ChatResponseFactory chatResponseFactory) {
    LOGGER.info("System message: " + message);
  }

  /**
   * Gets the message content.
   * @return The message content.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message content.
   * @param message The message content.
   */
  public void setMessage(String message) {
    this.message = message;
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
