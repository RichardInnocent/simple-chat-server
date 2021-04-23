import java.util.function.Consumer;

/**
 * Represents a user of the chat application.
 */
public interface User extends Closeable {

  /**
   * Notifies the user that an event has occurred.
   * @param notification The notification. This is likely to be transformed into a consistent data
   * structure.
   * @see RemoteUser#notifyOfEvent(Object)
   */
  void notifyOfEvent(Object notification);

  /**
   * Gets the chat group that the user belongs to.
   * @return The chat group that the user belongs to.
   */
  ChatMediator getChatMediator();

  /**
   * Gets the user's username. Usernames should be unique for a given chat group.
   * @return The user's username.
   */
  String getUsername();

  /**
   * Sets the user's username. Usernames should be unique for a given chat group.
   * @param username The user's username.
   */
  void setUsername(String username);

  /**
   * Adds an action that should be taken after the user resources have been closed.
   * @param onCloseFunction The action to take.
   */
  void onClose(Consumer<User> onCloseFunction);

}
