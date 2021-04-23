/**
 * Implemented a separate version of the {@link java.io.Closeable} interface, primarily to avoid
 * {@code IOException}s being thrown. This also includes some other convenience methods such as
 * {@link #isClosed()}.
 */
public interface Closeable {

  /**
   * Closes the resource.
   */
  void close();

  /**
   * Determines whether the resource is closed.
   * @return {@code true} if the resource is closed.
   */
  boolean isClosed();

  /**
   * Determines whether the resource is still open.
   * @return {@code true} if the resource is open.
   */
  default boolean isOpen() {
    return !isClosed();
  }

}
