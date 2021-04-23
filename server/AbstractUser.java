import java.util.Objects;

/**
 * An abstract implementation of a {@link User} that contains details applicable to all users, such
 * as their username and chat group.
 */
public abstract class AbstractUser implements User {

  private String username;
  private final ChatMediator chatMediator;

  /**
   * Creates a new user.
   * @param chatMediator The chat group that the user belongs to.
   */
  public AbstractUser(ChatMediator chatMediator) {
    this.chatMediator = Objects.requireNonNull(chatMediator, "Chat mediator is null");
  }

  /**
   * <p>Gets the user's username.</p>
   * <p>The username can be modified while it is being read, hence the synchronisation. For example,
   * if a user request that sets the user's username is being processed at the same time as a failed
   * request is being processed, this could cause issues.</p>
   * @return The user's username.
   */
  @Override
  public synchronized String getUsername() {
    return username;
  }

  /**
   * <p>Sets the user's username.</p>
   * <p>The username can be modified while it is being read, hence the synchronisation. For example,
   * if a user request that sets the user's username is being processed at the same time as the
   * user's connection drops (triggering logs and the creation of a notification to be sent to other
   * users), this could cause issues.</p>
   * @param username The user's username.
   */
  @Override
  public synchronized void setUsername(String username) {
    this.username = username;
  }

  @Override
  public ChatMediator getChatMediator() {
    return chatMediator;
  }

}
