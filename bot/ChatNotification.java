/**
 * Represents a notification received from the chat server.
 */
public interface ChatNotification {

  /**
   * Processes the notification, taking whatever action is appropriate according to the type of
   * notification.
   * @param requestSender The instance used to send data to the chat server.
   * @param chatResponseFactory The factory that creates responses based on inputs.
   */
  void process(RequestSender requestSender, ChatResponseFactory chatResponseFactory);

}
