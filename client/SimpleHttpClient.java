import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic client implementation that attempts to connect to a server at a given hostname on a
 * provided port, and then runs.
 */
public abstract class SimpleHttpClient implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(SimpleHttpClient.class.getName());

  private final String address;
  private final int port;

  /**
   * Creates a new HTTP client.
   * @param hostname The hostname of the server.
   * @param port The port that the server will accept connections on.
   * @throws NullPointerException Thrown if {@code hostname == null}.
   * @throws IllegalArgumentException Thrown if {@code port} is not a positive integer.
   */
  public SimpleHttpClient(String hostname, int port)
      throws NullPointerException, IllegalArgumentException {
    this.address = Objects.requireNonNull(hostname, "Address must not be null");
    if (hostname.isEmpty()) {
      throw new IllegalArgumentException("Address cannot be empty");
    }
    this.port = port;
  }

  /**
   * Attempt to start the server. If starting the server is successful, create appropriate I/O
   * mechanisms and then hand off to the child implementation. If starting the server is not
   * successful, exit and log the exception.
   */
  @Override
  public void run() {
    try (Socket socket = new Socket(address, port)) {
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
      run(input, output);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not connect to server", e);
    }
  }

  /**
   * Runs the server. This implementation can vary depending on purpose.
   * @param input The mechanism for retrieving data from the server.
   * @param output The mechanism for sending data to the server.
   * @throws IOException Thrown if there is a problem reading from/writing to the input/output
   * mechanisms.
   */
  protected abstract void run(BufferedReader input, PrintWriter output) throws IOException;

}
