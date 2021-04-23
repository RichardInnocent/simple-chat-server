/**
 * An exception to indicate that a request failed to be sent to the server.
 */
public class RequestSendingException extends Exception {

  /**
   * Creates a new exception to indicate that a request failed to be sent to the server.
   */
  public RequestSendingException() {
  }

  /**
   * Creates a new exception to indicate that a request failed to be sent to the server.
   * @param message The exception message.
   */
  public RequestSendingException(String message) {
    super(message);
  }

  /**
   * Creates a new exception to indicate that a request failed to be sent to the server.
   * @param cause The cause of the exception.
   */
  public RequestSendingException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception to indicate that a request failed to be sent to the server.
   * @param message The exception message.
   * @param cause The cause of the exception.
   */
  public RequestSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}
