/**
 * Thrown if there is a problem encountered when parsing or writing XML data.
 */
public class XmlParseException extends RuntimeException {

  /**
   * Creates a new exception to indicate that a problem was encountered when parsing or writing XML
   * data.
   * @param message The exception message.
   */
  public XmlParseException(String message) {
    super(message);
  }

  /**
   * Creates a new exception to indicate that a problem was encountered when parsing or writing XML
   * data.
   * @param cause The cause of the exception.
   */
  public XmlParseException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception to indicate that a problem was encountered when parsing or writing XML
   * data.
   * @param message The exception message.
   * @param cause The cause of the exception.
   */
  public XmlParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
