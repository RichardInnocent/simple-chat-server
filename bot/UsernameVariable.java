/**
 * A variable that injects the username of the user that the bot is responding to.
 */
public class UsernameVariable implements Variable {

  @Override
  public String getVariableName() {
    return "username";
  }

  @Override
  public String getValue(ChatEntryNotification notification) {
    return notification.getSender();
  }
}
