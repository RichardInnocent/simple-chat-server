import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a request to the server to send a chat message to one or more members of the chat
 * group.
 */
public class ChatEntryRequest {

  private final String message;

  private final Set<String> recipients;

  /**
   * Creates a request to the server to send a chat message to all members in the chat group.
   * @param message The message to send.
   */
  public ChatEntryRequest(String message) {
    this(message, Collections.emptySet());
  }

  /**
   * Creates a request to the server to send a chat message to only specific members of the chat
   * group.
   * @param message The message to send.
   * @param recipients The recipients of the message. If all users should be notified, this can be
   * {@code null} or empty.
   */
  public ChatEntryRequest(String message, Set<String> recipients) {
    this.message = Objects.requireNonNull(message, "Message is null");
    this.recipients = recipients == null ? Collections.emptySet() : recipients;
  }

  /**
   * Gets the message to send.
   * @return The message to send.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the recipients of the message.
   * @return The recipients of the message. If this is {@code null} or empty, the message will be
   * sent to all members of the chat group.
   */
  public Set<String> getRecipients() {
    return recipients;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChatEntryRequest)) {
      return false;
    }
    ChatEntryRequest that = (ChatEntryRequest) o;
    return Objects.equals(message, that.message)
        && Objects.equals(recipients, that.recipients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, recipients);
  }
}

