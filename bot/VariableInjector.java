/**
 * Responsible for injecting variables into a response. Variables can be used in response files if
 * they are included in the following form: <code>%{variableName}%</code> such as
 * {@code %username%}. This injector will attempt to resolve the values of these variables and
 * insert them into the template.
 */
@FunctionalInterface
public interface VariableInjector {

  String injectVariables(String response, ChatEntryNotification notification);

}
