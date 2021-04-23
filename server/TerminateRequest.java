import java.util.Objects;
import java.util.logging.Logger;

/**
 * A request to terminate the chat group.
 */
public class TerminateRequest implements ChatRequest {

  private static final Logger LOGGER = Logger.getLogger(TerminateRequest.class.getName());

  private User author;

  @Override
  public void setAuthor(User author) {
    this.author = author;
  }

  @Override
  public User getAuthor() {
    return author;
  }

  /**
   * Notifies the users of the shut down initialisation and then closes down the group.
   */
  @Override
  public void process(ChatMediator chatMediator) throws RequestProcessingException {
    String shutdownMessage = "System shutdown initiated";
    LOGGER.info(shutdownMessage);
    LOGGER.info("Notifying users of shutdown");
    SystemNotification shutdownNotification = new SystemNotification(shutdownMessage);
    chatMediator.notifyUsers(shutdownNotification);
    chatMediator.close();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TerminateRequest)) {
      return false;
    }
    TerminateRequest that = (TerminateRequest) o;
    return Objects.equals(author, that.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(author);
  }
}
