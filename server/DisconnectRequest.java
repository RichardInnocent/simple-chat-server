import java.util.Objects;

/**
 * This is a pseudo-request to disconnect a user. The user never actually sends this request.
 * Instead, it should be created and processed whenever a user disconnects. It is split into a
 * request of its own as part of the disconnection process is to inform other users of the
 * disconnect, and to remove the user from the chart group. Consistent with other updates of this
 * nature, this needs to be processed asynchronously for a given chat group which can be achieved
 * by implementing it as a {@link ChatRequest} and processing it through a
 * {@link BlockingChatRequestProcessor}.
 */
public class DisconnectRequest implements ChatRequest {

  @XmlIgnore
  private User disconnectedUser;

  @Override
  public void setAuthor(User disconnectedUser) {
    this.disconnectedUser = disconnectedUser;
  }

  @Override
  public User getAuthor() {
    return disconnectedUser;
  }

  @Override
  public void process(ChatMediator chatMediator) throws RequestProcessingException {
    // Disconnect the user from the chat group
    chatMediator.disconnect(disconnectedUser);

    // Inform the users in the group that the user has left
    if (disconnectedUser.getUsername() != null) {
      DisconnectNotification disconnectNotification =
          new DisconnectNotification(disconnectedUser.getUsername());
      if (chatMediator.isOpen()) {
        chatMediator.notifyUsers(disconnectNotification);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DisconnectRequest)) {
      return false;
    }
    DisconnectRequest that = (DisconnectRequest) o;
    return Objects.equals(disconnectedUser, that.disconnectedUser);
  }

  @Override
  public int hashCode() {
    return Objects.hash(disconnectedUser);
  }
}
