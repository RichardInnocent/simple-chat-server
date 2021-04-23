import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A variable that can be used to display the current time.
 */
public class CurrentTimeVariable implements Variable {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  @Override
  public String getVariableName() {
    return "currentTime";
  }

  @Override
  public String getValue(ChatEntryNotification notification) {
    return LocalDateTime.now().format(DATE_TIME_FORMATTER);
  }
}
