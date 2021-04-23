/**
 * A request processor that blocks to ensure that all inbound requests are processed one at a time.
 * This improves the transactional accuracy of requests made from clients, as requests cannot
 * interfere with one another during execution. It also alleviates the necessity for various data
 * structures and processes to cater for thread safety - as long as the {@link ChatRequest}
 * processes synchronously, concurrent modifications should never occur.
 */
public class BlockingChatRequestProcessor implements ChatRequestProcessor {

  private static final BlockingChatRequestProcessor INSTANCE = new BlockingChatRequestProcessor();

  /**
   * Gets the singleton instance.
   * @return The singleton instance.
   */
  public static BlockingChatRequestProcessor getInstance() {
    return INSTANCE;
  }

  private BlockingChatRequestProcessor() {}

  @Override
  public synchronized void process(ChatRequest chatRequest, ChatMediator chatMediator)
      throws RequestProcessingException {
    chatRequest.process(chatMediator);
  }

}
