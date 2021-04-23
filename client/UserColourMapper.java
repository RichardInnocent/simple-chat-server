/**
 * Maps a username to a specific colour.
 */
public interface UserColourMapper {

  /**
   * Gets the colour associated with the specified username. If the username does not have an
   * associated colour, assign it and return this.
   * @param username The username to retrieve a colour for.
   * @return The colour associated with the username.
   */
  Colour getColour(String username);

  /**
   * Removes the colour mapping associated with a username.
   * @param username The colour mapping associated with a username.
   */
  void removeMapping(String username);

}
