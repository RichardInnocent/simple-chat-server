import java.util.Objects;
import java.util.Optional;

/**
 * Parses command line parameters. Command line parameters are expected to start with a hyphen,
 * followed by their name. The next argument would be its value. For example in:
 * <pre><code>["-abc", "123"]</code></pre>
 * The parameter name is {@code abc}, and the parameter value is {@code 123}.
 */
public class CommandLineArgumentParser {

  private final String[] args;

  /**
   * Creates a new command line argument parser.
   * @param args The arguments that the parser should analyse.
   * @throws NullPointerException Thrown if {@code args == null}.
   */
  public CommandLineArgumentParser(String[] args) throws NullPointerException {
    this.args = Objects.requireNonNull(args, "Args are null");
  }

  /**
   * Gets the value of the parameter with the given name.
   * @param parameterName The parameter name. Do not include the leading hyphen.
   * @return The value of the parameter, or an empty optional if it is not specified.
   */
  public Optional<String> getParameter(String parameterName) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-" + parameterName)) {
        return i+1 < args.length ? Optional.of(args[i+1]) : Optional.empty();
      }
    }
    return Optional.empty();
  }

}
