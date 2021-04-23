/**
 * Ensures that a string meets is in a specified format.
 */
@FunctionalInterface
public interface StringContentValidator {

  /**
   * Ensures that the string meets a given format.
   * @param value The string to verify.
   * @throws IllegalArgumentException Thrown if the string is not in the desired format.
   */
  void validate(String value) throws IllegalArgumentException;

}
