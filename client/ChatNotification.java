import java.io.PrintWriter;

/**
 * Represents a notification received from the chat server.
 */
public interface ChatNotification {

  /**
   * Processes the notification, taking whatever action is appropriate according to the type of
   * notification.
   * @param chatClient The client instance.
   * @param cmdOutput The mechanism to display output.
   */
  void process(ChatClient chatClient, PrintWriter cmdOutput);

}
