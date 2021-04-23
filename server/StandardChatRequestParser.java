import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A concrete implementation of a {@link ChatRequestParser}, converting inbound XML from clients
 * into usable {@link ChatRequest} objects.
 */
public class StandardChatRequestParser implements ChatRequestParser {

  private static final Logger LOGGER = Logger.getLogger(StandardChatRequestParser.class.getName());

  private static final StandardChatRequestParser INSTANCE = new StandardChatRequestParser();

  // The types of request that we expect to handle. We could generate these dynamically from a
  // class loader but this is manageable for now
  private final Collection<Class<? extends ChatRequest>> registeredTypes = Arrays.asList(
      ChatEntryRequest.class,
      ConnectionRequest.class
  );

  private final XmlReader xmlReader = new ReflectiveXmlReader();

  private final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

  /**
   * Gets the singleton instance.
   * @return The singleton instance.
   */
  public static StandardChatRequestParser getInstance() {
    return INSTANCE;
  }

  private StandardChatRequestParser() {}

  @Override
  public ChatRequest fromXml(String xmlString, User author) throws XmlParseException {
    // Create a new input stream for the given string
    try (
        InputStream xmlInputStream =
            new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))
    ) {
      // Parse the input stream
      return fromXml(xmlInputStream, author);
    } catch (IOException e) {
      throw new XmlParseException("Failed to close XML input stream", e);
    }
  }

  @Override
  public ChatRequest fromXml(InputStream xmlInputStream, User author)
      throws XmlParseException {
    XMLStreamReader xmlStreamReader = null; // Not autocloseable
    try {
      xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlInputStream);
      return fromXmlThrowExceptions(xmlStreamReader, author);
    } catch (XMLStreamException e) {
      throw new XmlParseException("Failed to parse message", e);
    } finally {
      if (xmlStreamReader != null) {
        try {
          xmlStreamReader.close();
        } catch (XMLStreamException e) {
          LOGGER.log(Level.WARNING, "Failed to close XML stream reader", e);
        }
      }
    }
  }

  private ChatRequest fromXmlThrowExceptions(XMLStreamReader xmlStreamReader, User author)
      throws XmlParseException, XMLStreamException {
    while (xmlStreamReader.next() != XMLStreamConstants.START_ELEMENT) {
      // skip to the first element
    }

    String xmlTypeName = xmlStreamReader.getLocalName();

    // Loop through each of the registered types
    for (Class<? extends ChatRequest> type : registeredTypes) {
      // Check if the name of the XML element matches that of the registered type
      if (type.getSimpleName().equals(xmlTypeName)) {
        // If it does, try and load it from XML
        ChatRequest chatRequest = xmlReader.readFromXml(xmlStreamReader, type);
        chatRequest.setAuthor(author);
        return chatRequest;
      }
    }

    // No registered type matches the XML name so we can't parse it
    throw new XmlParseException(
        "Received an XML request of type that is not supported: " + xmlTypeName
    );
  }
}
