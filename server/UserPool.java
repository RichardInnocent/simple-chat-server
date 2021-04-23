/**
 * A pool of users. Users can be added to the pool with {@link #addUser(User)}. When
 * {@link #disconnectAll()} is called, all users in the pool will be disconnected. If users
 * disconnected through another means, they will be removed from the pool.
 */
public interface UserPool {

  /**
   * Adds a user to the pool.
   * @param user The user to add.
   */
  void addUser(User user);

  /**
   * Disconnects all users in the pool, terminating their threads.
   */
  void disconnectAll();

}
