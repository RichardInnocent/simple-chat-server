import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * A notification that a user has left a message in the chat.
 */
public class ChatEntryNotification {

  private final String message;
  private final String sender;
  private final Collection<String> recipients;

  /**
   * Creates a new notification to indicate that a user has left a message in the chat.
   * @param message The content of the message.
   * @param senderUsername The username of the sender.
   * @param recipients The usernames of the recipients of the message. If the message should be
   * sent to all users in the chat, this can be {@code null} or empty.
   */
  public ChatEntryNotification(String message, String senderUsername, Collection<String> recipients)
      throws NullPointerException {
    this.message = Objects.requireNonNull(message, "Message is null");
    this.sender = Objects.requireNonNull(senderUsername, "Sender username is null");
    this.recipients = recipients == null ? Collections.emptyList() : recipients;
  }

  /**
   * Gets the message that was sent.
   * @return The message that was sent.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the username of the sender.
   * @return The username of the sender.
   */
  public String getSender() {
    return sender;
  }

  /**
   * Gets the usernames of the recipients of the message. If this collection is empty, the message
   * was sent to all users.
   * @return The recipients of the message.
   */
  public Collection<String> getRecipients() {
    return recipients;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChatEntryNotification)) {
      return false;
    }
    ChatEntryNotification that = (ChatEntryNotification) o;
    return Objects.equals(message, that.message) && Objects
        .equals(sender, that.sender) && Objects.equals(recipients, that.recipients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, sender, recipients);
  }
}
