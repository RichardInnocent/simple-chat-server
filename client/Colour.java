/**
 * Represents a colour that can be displayed in the terminal.
 */
public enum Colour {

  RED("\u001B[31m", true),
  GREEN("\u001B[32m", true),
  YELLOW("\u001B[33m", true),
  BLUE("\u001B[34m", true),
  PURPLE("\u001B[35m", true),
  CYAN("\u001B[36m", true),

  /**
   * The reset code reverts the terminal to its original colour.
   */
  RESET("\033[0m", false);

  private final String ansiIdentifier;
  private final boolean assignable;

  Colour(String ansiIdentifier, boolean assignable) {
    this.ansiIdentifier = ansiIdentifier;
    this.assignable = assignable;
  }

  /**
   * Gets the ANSI identifier that should be used to switch the terminal to that specified colour.
   * @return The ANSI identifier for the colour.
   */
  public String getAnsiIdentifier() {
    return ansiIdentifier;
  }

  /**
   * Determines if the colour is assignable to a user. For example, the {@link #RED} is an
   * assignable colour, while the {@link #RESET} identifier is not.
   * @return {@code true} if the colour is assignable.
   */
  public boolean isAssignable() {
    return assignable;
  }

  /**
   * Wraps the text in ANSI identifiers so that it will be printed in the specified colour. A
   * {@link #RESET} code is inserted at the end to return the terminal to its base state.
   * @param text The text to colourise.
   * @return The original text, wrapped by the appropriate ANSI codes to make it display in the
   * given colour.
   */
  public String wrapText(String text) {
    return getAnsiIdentifier() + text + RESET.getAnsiIdentifier();
  }
}
