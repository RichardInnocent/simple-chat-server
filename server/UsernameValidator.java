/**
 * Ensures that usernames provided are sensible. In this implementation, a valid username must:
 * <ul>
 *   <li>consist of alphanumeric characters (upper and lowercase accepted), numbers or underscores.
 *   </li>
 *   <li>start with a letter.</li>
 *   <li>have a length between 2 and 32 characters.</li>
 * </ul>
 */
public class UsernameValidator implements StringContentValidator {

  public static UsernameValidator INSTANCE = new UsernameValidator();

  /**
   * Gets the singleton instance.
   * @return The singleton instance.
   */
  public static UsernameValidator getInstance() {
    return INSTANCE;
  }

  private UsernameValidator() {}

  @Override
  public void validate(String value) throws IllegalArgumentException {
    if (!value.matches("^[a-zA-Z][a-zA-Z0-9_]{1,31}$")) {
      throw new IllegalArgumentException(
          "A username must consist of alphanumeric characters and underscores, and be "
              + "between 2 and 32 characters in length. A username must start with a letter."
      );
    }
  }
}
