/**
 * Represents the current state of the client.
 */
public enum ClientState {

  /**
   * The client is assumed to be connected to a chat group.
   */
  CONNECTED,

  /**
   * The client is connected to a server, but has not joined a chat group. As such, the user cannot
   * send or receive messages until they have connected to a group.
   */
  DISCONNECTED
}
