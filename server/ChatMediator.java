import java.util.Collection;
import java.util.function.Predicate;

/**
 * This class represents a chat group that a user can be a part of to send/receive messages.
 * Realistically, this should be considered a chat group or room, but this naming convention
 * indicates the use of the mediator pattern.
 */
public interface ChatMediator extends ManagedLifeCycle {

  /**
   * Adds a user to the group.
   * @param user The user to be added to the group.
   * @return {@code true} if the user was added to the group. This could return {@code false} if
   * the user is already a member of the group.
   */
  boolean addUser(User user);

  /**
   * Checks if the group contains the specified user.
   * @param user The user to search for.
   * @return {@code true} if the group contains the specified user.
   */
  boolean containsUser(User user);

  /**
   * Checks if the group contains a user matching the specified predicate.
   * @param userPredicate The predicate to search for.
   * @return {@code true} if the group contains a user matching the specified predicate.
   */
  boolean containsUserMatching(Predicate<User> userPredicate);

  /**
   * Notifies all users of the given event.
   * @param notification The notification to be sent.
   */
  void notifyUsers(Object notification);

  /**
   * Notifies only specific users of the given event.
   * @param notification The notification to be sent.
   * @param usernames The usernames of all of the users that the notification should be sent to.
   * This can be set to {@code null} or an empty collection if the notification should be sent to
   * all users.
   */
  void notifyUsersByUsername(Object notification, Collection<String> usernames);

  /**
   * Notifies only the users that match a given predicate of the given event.
   * @param notification The notification to be sent.
   * @param predicate The criterion that a user must match in order to receive a message.
   */
  void notifyUsersByPredicate(Object notification, Predicate<User> predicate);

  /**
   * Disconnects a given user from the group. This will close the user's connection.
   * @param user The user to be disconnected.
   */
  void disconnect(User user);

}
