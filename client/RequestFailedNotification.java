import java.io.PrintWriter;
import java.util.Objects;

/**
 * A notification from the server that a request sent from this client failed to be processed.
 */
public class RequestFailedNotification implements ChatNotification {

  private String failedRequestType;
  private String reason;

  @Override
  public void process(ChatClient chatClient, PrintWriter cmdOutput) {
    if (ConnectionRequest.class.getSimpleName().equals(failedRequestType)) {
      cmdOutput.println(reason);
      cmdOutput.print("Please specify your username: ");
      cmdOutput.flush(); // Needs a flush as a print isn't auto-flushed unlike println
      chatClient.setState(ClientState.DISCONNECTED);
    } else {
      cmdOutput.println("Request failed to process: " + reason);
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
    return Objects.equals(failedRequestType, that.failedRequestType)
        && Objects.equals(reason, that.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(failedRequestType, reason);
  }
}
