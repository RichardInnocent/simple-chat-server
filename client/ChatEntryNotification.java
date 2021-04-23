import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;

/**
 * A notification that a user has left a message in the chat.
 */
public class ChatEntryNotification implements ChatNotification {

  private static final SynchronizedUserColourMapper COLOUR_MAPPER =
      SynchronizedUserColourMapper.getInstance();

  private String message;
  private String sender;
  private Collection<String> recipients;

  /**
   * Displays the message to the user.
   * @param chatClient The client instance.
   * @param cmdOutput The mechanism to display output.
   */
  @Override
  public void process(ChatClient chatClient, PrintWriter cmdOutput) {
    cmdOutput.println(buildOutputString(chatClient));
  }

  private String buildOutputString(ChatClient chatClient) {
    // Output the username of the sender
    StringBuilder output = new StringBuilder(COLOUR_MAPPER.getColour(sender).wrapText(sender));

    // Specify the recipients (if sent privately)
    if (recipients != null && !recipients.isEmpty()) {
      appendRecipientsSubString(output, chatClient);
    }

    // Output the message
    return output.append(": ")
                 .append(message)
                 .toString();
  }

  /**
   * If the notification has defined recipients, the message was private. This method builds a
   * string to indicate who the message was sent to.
   */
  private void appendRecipientsSubString(StringBuilder output, ChatClient chatClient) {
    output.append(" (to ").append(getPrefix(chatClient));
    int numberOfOtherRecipients = recipients.size() - 1;
    if (numberOfOtherRecipients > 0) {
      output.append(" and ")
            .append(numberOfOtherRecipients)
            .append(numberOfOtherRecipients == 1 ? " other" : " others");
    }
    output.append(')');
  }

  /**
   * Returns "you", except if the sender is the current user. In this case, the name of the first
   * recipient is returned.
   */
  private String getPrefix(ChatClient chatClient) {
    return Objects.equals(chatClient.getUsername(), sender) ? recipients.iterator().next() : "you";
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
