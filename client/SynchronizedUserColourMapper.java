import java.util.HashMap;

/**
 * Maps a username to a specific colour. This implementation is thread-safe.
 */
public class SynchronizedUserColourMapper implements UserColourMapper {

  private static final int NUMBER_OF_COLOURS = Colour.values().length;
  private static final SynchronizedUserColourMapper INSTANCE = new SynchronizedUserColourMapper();

  private final HashMap<String, Colour> usernameToColourMap = new HashMap<>();
  private int currentColourIndex = 0;

  /**
   * Gets the singleton instance.
   * @return The singleton instance.
   */
  public static SynchronizedUserColourMapper getInstance() {
    return INSTANCE;
  }

  private SynchronizedUserColourMapper() {}

  /**
   * Gets the colour associated with the specified username. If the username does not have an
   * associated colour, assign it and return this.
   * @param username The username to retrieve a colour for.
   * @return The colour associated with the username.
   */
  @Override
  public synchronized Colour getColour(String username) {
    Colour colour = usernameToColourMap.get(username);
    if (colour == null) {
      // This username does not have an associated colour - let's assign one
      colour = getNextAssignableColour();
      usernameToColourMap.put(username, colour);
    }
    return colour;
  }

  /**
   * Gets the next colour that can be assigned. Colours are not intended to be solely unique, but
   * should provide enough of a separator to allow users to be able to quickly differentiate
   * themselves in most cases.
   * @return The next colour that can be assigned to a username.
   */
  private Colour getNextAssignableColour() {
    Colour colour;
    // Keep iterating over the colours until we get one that is assignable
    while (!(colour = Colour.values()[currentColourIndex]).isAssignable()) {
      incrementColourIndex();
    }
    incrementColourIndex();
    return colour;
  }

  private void incrementColourIndex() {
    if (++currentColourIndex >= NUMBER_OF_COLOURS) {
      currentColourIndex = 0;
    }
  }

  @Override
  public synchronized void removeMapping(String username) {
    usernameToColourMap.remove(username);
  }
}
