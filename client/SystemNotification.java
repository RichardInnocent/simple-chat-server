import java.io.PrintWriter;
import java.util.Objects;

/**
 * Represents a generic notification from the server. System notifications are not associated with
 * any specific user.
 */
public class SystemNotification implements ChatNotification {

  private String message;

  @Override
  public void process(ChatClient chatClient, PrintWriter cmdOutput) {
    // Print it out. No further action required here.
    cmdOutput.println(message);
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
