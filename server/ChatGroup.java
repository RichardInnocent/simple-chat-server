import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * This class uses the mediator pattern to communicate with other users in the system. This
 * implementation is <i>not</i> thread-safe. (Thread safety is guaranteed by ensuring the all
 * interactions with this group go through a blocking processor - see
 * {@link BlockingChatRequestProcessor}.)
 */
public class ChatGroup implements ChatMediator {

  private static final Logger LOGGER = Logger.getLogger(ChatGroup.class.getName());

  private final Collection<User> users = new HashSet<>();
  private boolean closed = false;

  /* We can pre-size this list as we know exactly how many action events will be received in this
   * application. In a more extensible program, we could choose a more appropriate initialisation.
   */
  private final Collection<Runnable> onCloseActions = new ArrayList<>(1);

  /**
   * Adds a user to the group.
   * @param user The user to be added to the group.
   * @return {@code true} if the user was added to the group. This could return {@code false} if
   * the user is already a member of the group.
   * @throws IllegalStateException Thrown if the chat group has been closed.
   */
  public boolean addUser(User user) throws IllegalStateException {
    verifyNotClosed();

    // Users within a group must have a unique name so deny entry to the user is the username is
    // already taken
    boolean userAdded = !usernameIsAlreadyTaken(user.getUsername()) && users.add(user);

    if (userAdded) {
      user.onClose(removedUser -> {
        if (!closed) {
          // Ensure that the user is removed from the group on close so that we don't have a memory
          // leak here. This shouldn't cause a concurrent modification of the underlying array, as
          // disconnects of users that form part of a chat group are processed synchronously as a
          // separate request.
          users.remove(removedUser);
          LOGGER.info(removedUser.getUsername() + " has left the chat");
        }
      });
      LOGGER.info(user.getUsername() + " has joined the chat");
    }

    return userAdded;
  }

  private boolean usernameIsAlreadyTaken(String username) {
    return users.stream().map(User::getUsername).anyMatch(username::equals);
  }

  @Override
  public boolean containsUser(User user) throws IllegalStateException {
    verifyNotClosed();
    return users.contains(user);
  }

  @Override
  public boolean containsUserMatching(Predicate<User> userPredicate) {
    return users.stream().anyMatch(userPredicate);
  }

  /**
   * Notifies all users of the given event.
   * @param notification The notification to be sent.
   * @throws IllegalStateException Thrown if the chat group has been closed.
   */
  @Override
  public void notifyUsers(Object notification) throws IllegalStateException {
    verifyNotClosed();
    notifyUsersByPredicate(notification, user -> true);
  }

  /**
   * Notifies only specific users of the given event.
   * @param notification The notification to be sent.
   * @param usernames The usernames of all of the users that the notification should be sent to.
   * This can be set to {@code null} or an empty collection if the notification should be sent to
   * all users.
   * @throws IllegalStateException Thrown if the chat group has been closed.
   */
  @Override
  public void notifyUsersByUsername(Object notification, Collection<String> usernames)
      throws IllegalStateException {
    verifyNotClosed();
    notifyUsersByPredicate(
        notification,
        user -> usernames == null || usernames.isEmpty() || usernames.contains(user.getUsername())
    );
  }

  /**
   * Notifies only the users that match a given predicate of the given event.
   * @param notification The notification to be sent.
   * @param predicate The criterion that a user must match in order to receive a message.
   * @throws IllegalStateException Thrown if the chat group has been closed.
   */
  @Override
  public void notifyUsersByPredicate(Object notification, Predicate<User> predicate)
      throws IllegalStateException {
    verifyNotClosed();
    users.parallelStream()
         .filter(predicate)
         .forEach(user -> user.notifyOfEvent(notification));
  }

  @Override
  public void disconnect(User user) throws IllegalStateException {
    user.close();
  }

  @Override
  public void close() {
    if (!closed) {
      LOGGER.info("Disconnecting all users");
      closed = true;
      users.forEach(this::disconnect);
      onCloseActions.forEach(Runnable::run);
      users.clear();
    }
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  private void verifyNotClosed() throws IllegalStateException {
    if (closed) {
      throw new IllegalStateException("This group is closed");
    }
  }

  @Override
  public void addOnCloseAction(Runnable action) throws NullPointerException, IllegalStateException {
    verifyNotClosed();
    onCloseActions.add(Objects.requireNonNull(action, "Action is null"));
  }

}
