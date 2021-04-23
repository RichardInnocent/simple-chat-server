import java.util.Optional;

/**
 * Creates appropriate responses based on an input.
 */
public interface ChatResponseFactory {

  /**
   * Generates a response based on some input. It may be appropriate that no response is generated,
   * in which case the result will be an empty {@link Optional}.
   * @param notification The notification received.
   * @return The response that should be sent to the chat.
   */
  Optional<String> getResponse(ChatEntryNotification notification);

}
