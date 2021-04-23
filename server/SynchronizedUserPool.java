import java.util.HashSet;
import java.util.Set;

/**
 * <p>A synchronous implementation of a user pool. This ensures that all user resources are closed
 * on shutdown.</p>
 * <p>When users first connect, they are unable to send or receive messages until they send a valid
 * and accepted {@link ConnectionRequest}. At this time, users are <em>not</em> connected to a
 * mediator, so shutting down the mediator will not terminate these users. These users should
 * instead be terminated from the user pool when the mediator closes. This could be simplified by
 * adding all connected users to the mediator, regardless of whether they have provided a username,
 * but I opted against it for two primary reasons:
 * <ol>
 *   <li>In the coursework, there's only one chat room is required, but it's possible to envision
 *   the system supporting multiple chat rooms. In this case, a user should be able to specify the
 *   room that they'd like to join.</li>
 *   <li>Connections can occur at any time, without the synchronization control provided by the
 *   {@link BlockingChatRequestProcessor}, which would mean that the mediator implementation would
 *   need to be changed to be thread-safe.</li>
 * </ol></p>
 */
public class SynchronizedUserPool implements UserPool {

  // No need for this to be a synchronised as all modifications take place synchronously
  private final Set<User> queuedUsers = new HashSet<>();

  /**
   * Creates a new user pool that will close all user connections when the given mediator is
   * instructed to shut down.
   * @param chatMediator The chat mediator.
   */
  public SynchronizedUserPool(ChatMediator chatMediator) {
    // We don't actually need to store a reference to the mediator that we're using, we just need
    // to set up some behaviour to follow after the mediator has been closed.
    chatMediator.addOnCloseAction(this::disconnectAll);
  }

  /**
   * Adds a user to the pool.
   * @param user The user to add.
   */
  public synchronized void addUser(User user) {
    if (queuedUsers.add(user)) {
    // When a user disconnects, remove them from the queuedUsers collection to ensure we don't get a
    // memory leak here.
      user.onClose(queuedUsers::remove);
    }
  }

  /**
   * Disconnects all users in the pool, terminating their threads.
   */
  public synchronized void disconnectAll() {
    // As the queuedUsers entity is updated each time a user disconnects, we need to iterate over a
    // copied collection. Some users may already have been closed, but the User entity should handle
    new HashSet<>(queuedUsers).stream().filter(User::isOpen).forEach(User::close);
  }

}
