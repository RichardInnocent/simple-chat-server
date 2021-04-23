import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A notification that a user has left a message in the chat.
 */
public class ChatEntryNotification implements ChatNotification {

  private static final Logger LOGGER = Logger.getLogger(ChatEntryNotification.class.getName());

  private String message;
  private String sender;
  private Collection<String> recipients;

  /**
   * Processes the request. This may provoke a response from the bot.
   * @param requestSender The instance used to send data to the chat server.
   * @param chatResponseFactory The factory that creates responses based on inputs.
   */
  @Override
  public void process(RequestSender requestSender, ChatResponseFactory chatResponseFactory) {
    chatResponseFactory
        .getResponse(this) // Does this response warrant a response?
        .map(this::toRequest) // If so, create a response
        .ifPresent(request -> sendRequest(request, requestSender)); // send the response
  }

  private ChatEntryRequest toRequest(String message) {
    return new ChatEntryRequest(message);
  }

  private void sendRequest(ChatEntryRequest request, RequestSender requestSender) {
    try {
      requestSender.send(request);
    } catch (RequestSendingException e) {
      LOGGER.log(Level.WARNING, "Failed to send message", e);
    }
  }

  /**
   * Gets the message that was sent.
   * @return The message that was sent.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message that was sent.
   * @param message The message that was sent.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the username of the sender of the message.
   * @return The username of the sender of the message.
   */
  public String getSender() {
    return sender;
  }

  /**
   * Sets the username of the sender of the message.
   * @param sender The username of the sender of the message.
   */
  public void setSender(String sender) {
    this.sender = sender;
  }

  /**
   * Gets the usernames of the recipients of the message.
   * @return The usernames of the recipients of the message.
   */
  public Collection<String> getRecipients() {
    return recipients;
  }

  /**
   * Sets the usernames of the recipients of the message.
   * @param recipients The usernames of the recipients of the message.
   */
  public void setRecipients(Collection<String> recipients) {
    this.recipients = recipients;
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
    return Objects.equals(message, that.message)
        && Objects.equals(sender, that.sender)
        && Objects.equals(recipients, that.recipients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, sender, recipients);
  }
}
