import java.util.Objects;

/**
 * Represents a request to join a chat group.
 */
public class ConnectionRequest implements ChatRequest {

  private String username;

  @XmlIgnore
  private User author;

  /**
   * Gets the desired username of the connecting user.
   * @return The desired username of the connecting user.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the desired username of the connecting user.
   * @param username The desired username of the connecting user.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public User getAuthor() {
    return author;
  }

  @Override
  public void setAuthor(User author) {
    this.author = author;
  }

  @Override
  public void process(ChatMediator chatMediator) {
    // Validate the username
    try {
      UsernameValidator.getInstance().validate(username);
    } catch (IllegalArgumentException e) {
      author.notifyOfEvent(new RequestFailedNotification(ConnectionRequest.class, e.getMessage()));
      return;
    }

    if (chatMediator.containsUserMatching(user -> Objects.equals(user.getUsername(), username))) {
      author.notifyOfEvent(
          new RequestFailedNotification(
              ConnectionRequest.class,
              "A user with that name already exists. Please choose a different name"
          )
      );
      return;
    }

    // Username is valid, so set it
    author.setUsername(username);

    // Notify the other users in the group
    notifyUsersOfJoinIfAppropriate(chatMediator);
  }

  private void notifyUsersOfJoinIfAppropriate(ChatMediator chatMediator) {
    chatMediator.addUser(author);
    chatMediator.notifyUsers(new ConnectionNotification(author.getUsername()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConnectionRequest)) {
      return false;
    }
    ConnectionRequest that = (ConnectionRequest) o;
    return Objects.equals(username, that.username) && Objects
        .equals(author, that.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, author);
  }
}
