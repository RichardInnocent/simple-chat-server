import java.util.Objects;
import java.util.logging.Logger;

/**
 * A notification from the server that a request sent from this client failed to be processed.
 */
public class RequestFailedNotification implements ChatNotification {

  private static final Logger LOGGER = Logger.getLogger(RequestFailedNotification.class.getName());

  private String failedRequestType;
  private String reason;

  @Override
  public void process(RequestSender requestSender, ChatResponseFactory chatResponseFactory) {
    // Log the error
    LOGGER.warning("Request of type " + failedRequestType + " failed. Reason: " + reason);

    // If the bot can't connect as the name is incorrect, terminate and force the user to enter a
    // different name.
    if (ConnectionRequest.class.getSimpleName().equals(failedRequestType)) {
      LOGGER.info("Restart the bot with a different username, using flag -n");
      System.exit(1);
    }
  }

  /**
   * Gets the type of request that failed.
   * @return The type of request that failed.
   */
  public String getFailedRequestType() {
    return failedRequestType;
  }

  /**
   * Sets the type of request that failed.
   * @param failedRequestType The type of request that failed.
   */
  public void setFailedRequestType(String failedRequestType) {
    this.failedRequestType = failedRequestType;
  }

  /**
   * Gets the reason that the request failed.
   * @return The reason that the request failed.
   */
  public String getReason() {
    return reason;
  }

  /**
   * Sets the reason that the request failed.
   * @param reason The reason that the request failed.
   */
  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RequestFailedNotification)) {
      return false;
    }
    RequestFailedNotification that = (RequestFailedNotification) o;
    return Objects.equals(failedRequestType, that.failedRequestType) && Objects
        .equals(reason, that.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(failedRequestType, reason);
  }
}
