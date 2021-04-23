/**
 * An exception to indicate that the input received from a user is invalid.
 */
public class InputFormatException extends IllegalArgumentException {

  /**
   * Creates an exception to indicate that the input received from a user is invalid.
   */
  public InputFormatException() {}

  /**
   * Creates an exception to indicate that the input received from a user is invalid.
   * @param message The exception message.
   */
  public InputFormatException(String message) {
    super(message);
  }

  /**
   * Creates an exception to indicate that the input received from a user is invalid.
   * @param cause The cause of the exception.
   */
  public InputFormatException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates an exception to indicate that the input received from a user is invalid.
   * @param message The exception message.
   * @param cause The cause of the exception.
   */
  public InputFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
