import java.util.Objects;

/**
 * A notification that should be sent to a client when their request fails.
 */
public class RequestFailedNotification {

  private final String failedRequestType;
  private final String reason;

  /**
   * Creates a notification to imply that a client's request failed.
   * @param failedRequestType The type of request that failed to be processed.
   * @param reason The reason that the request failed to be processed.
   */
  public RequestFailedNotification(Class<? extends ChatRequest> failedRequestType, String reason) {
    this(failedRequestType.getSimpleName(), reason);
  }

  /**
   * Creates a notification to imply that a client's request failed.
   * @param failedRequestType The type of request that failed to be processed.
   * @param reason The reason that the request failed to be processed.
   */
  public RequestFailedNotification(String failedRequestType, String reason) {
    this.failedRequestType =
        Objects.requireNonNull(failedRequestType, "Failed request type is null");
    this.reason = Objects.requireNonNull(reason, "Reason is null");
  }

  /**
   * Gets the type of request that failed.
   * @return The type of request that failed.
   */
  public String getFailedRequestType() {
    return failedRequestType;
  }

  /**
   * Gets the reason that the request failed.
   * @return The reason that the request failed.
   */
  public String getReason() {
    return reason;
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
