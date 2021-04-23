/**
 * Variables can be injected into the responses file by citing it in the following form:
 * <pre><code>%{variableName}%</code></pre>
 * For example, this will inject a variable called username:
 * <pre><code>Hello %username%</code></pre>
 */
public interface Variable {

  /**
   * Gets the name of the variable.
   * @return The name of the variable.
   */
  String getVariableName();

  /**
   * Gets the wrapped name of the variable (i.e. surrounded by percentage signs), as it should
   * appear in the template.
   * @return The wrapped name of the variable, as it should appear in the template.
   */
  default String getWrappedVariableName() {
    return "%" + getVariableName() + "%";
  }

  /**
   * Gets the value of the variable.
   * @param notification The notification that triggered the response that is being injected. This
   * allows for user-specific information to be gathered, such as the user's username.
   * @return The value of the variable.
   */
  String getValue(ChatEntryNotification notification);

}
