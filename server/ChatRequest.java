/**
 * Represents a request sent to the server by a client.
 */
public interface ChatRequest {

  /**
   * Sets the user that made the request.
   * @param author The author of the request.
   */
  void setAuthor(User author);

  /**
   * Gets the user that made the request.
   * @return The author of the request.
   */
  User getAuthor();

  /**
   * Processes the request.
   * @param chatMediator The chat group that the request is associated with.
   * @throws RequestProcessingException Thrown if there is a problem processing the request.
   */
  void process(ChatMediator chatMediator) throws RequestProcessingException;

}
