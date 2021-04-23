import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A request to add a message to the chat.
 */
public class ChatEntryRequest implements ChatRequest {

  private String message;

  private Set<String> recipients;

  @XmlIgnore
  private User author;

  @Override
  public void setAuthor(User author) {
    this.author = author;
  }

  @Override
  public User getAuthor() {
    return author;
  }

  /**
   * Gets the message content to send.
   * @return The message content to send.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message content to send.
   * @param message The message content to send.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Sets the usernames of the recipients of the message. If this is {@code null} or empty, the
   * message should be sent to all users.
   * @return The usernames of the recipients of the message.
   */
  public Set<String> getRecipients() {
    return recipients;
  }

  /**
   * Gets the usernames of the recipients of the message. If this is {@code null} or empty, the
   * message should be sent to all users.
   * @param recipients The usernames of the recipients of the message.
   */
  public void setRecipients(Set<String> recipients) {
    this.recipients = recipients;
  }

  /**
   * Sends the message to the appropriate participants.
   * @param chatMediator The chat group to send the message in.
   * @throws RequestProcessingException Thrown if the user is not connected to a chat group.
   */
  @Override
  public void process(ChatMediator chatMediator) throws RequestProcessingException {
    if (chatMediator.containsUser(author)) {
      ChatEntryNotification notification =
          new ChatEntryNotification(message, author.getUsername(), recipients);
      Set<String> notificationRecipients = buildRecipientsForNotification();
      chatMediator.notifyUsersByUsername(notification, notificationRecipients);
    } else {
      throw new RequestProcessingException(
          "You are not connected. Please connect to the chat by setting your username through a "
              + ConnectionRequest.class.getSimpleName()
      );
    }
  }

  private Set<String> buildRecipientsForNotification() {
    if (recipients == null || recipients.isEmpty()) {
      return recipients;
    }

    // Make sure that the author is included
    Set<String> notificationRecipients = new HashSet<>(recipients);
    notificationRecipients.add(author.getUsername());
    return notificationRecipients;
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
        && Objects.equals(recipients, that.recipients)
        && Objects.equals(author, that.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, recipients, author);
  }
}
