/**
 * An interface to process chat requests.
 */
@FunctionalInterface
public interface ChatRequestProcessor {

  /**
   * Processes a chat request in the given chat mediator (chat group).
   * @param chatRequest The request to process.
   * @param chatMediator The chat group that the request should affect.
   * @throws RequestProcessingException Thrown if there is a problem processing the request.
   */
  void process(ChatRequest chatRequest, ChatMediator chatMediator)
      throws RequestProcessingException;

}
