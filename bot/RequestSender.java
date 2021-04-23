/**
 * Responsible for sending requests to the server.
 */
public interface RequestSender {

  /**
   * Transforms the given {@code request} into a form that is acceptable for consumption by the
   * server, and then sends the data.
   * @param request The data to send.
   * @throws RequestSendingException Thrown if there is a problem sending the request.
   */
  void send(Object request) throws RequestSendingException;
}
