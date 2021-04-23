import java.util.Arrays;
import java.util.Collection;

/**
 * A concrete implementation of the {@link VariableInjector} that is capable of injecting the
 * following variables:
 * <ul>
 *   <li>{@link UsernameVariable}</li>
 *   <li>{@link CurrentTimeVariable}</li>
 * </ul>
 */
public class StandardVariableInjector implements VariableInjector {

  private final Collection<Variable> variables = Arrays.asList(
      new UsernameVariable(),
      new CurrentTimeVariable()
  );

  @Override
  public String injectVariables(String response, ChatEntryNotification notification) {
    if (notification.getMessage() == null) {
      return null;
    }

    // Try to inject each supported variable
    for (Variable variable : variables) {
      response = injectVariable(response, notification, variable);
    }

    return response;
  }

  private String injectVariable(
      String response, ChatEntryNotification notification, Variable variable
  ) {
    // If the response contains the variable name, then resolve it and insert it and replace with
    // the new value
    return response.contains(variable.getWrappedVariableName()) ?
        response.replaceAll(variable.getWrappedVariableName(), variable.getValue(notification)) :
        response;
  }
}
