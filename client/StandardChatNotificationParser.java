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
 * A concrete implementation of a {@link ChatNotificationParser}, converting inbound XML from the
 * server into usable {@link ChatNotification} objects.
 */
public class StandardChatNotificationParser implements ChatNotificationParser {

  private static final Logger LOGGER =
      Logger.getLogger(StandardChatNotificationParser.class.getName());

  private static final StandardChatNotificationParser INSTANCE = new StandardChatNotificationParser();

  // The types of notification that we expect to handle. We could generate these dynamically from a
  // class loader but this is manageable for now
  private final Collection<Class<? extends ChatNotification>> registeredTypes = Arrays.asList(
      ChatEntryNotification.class,
      ConnectionNotification.class,
      DisconnectNotification.class,
      RequestFailedNotification.class,
      SystemNotification.class
  );

  private final XmlReader xmlReader = new ReflectiveXmlReader();

  private final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

  /**
   * Gets the singleton instance.
   * @return The singleton instance.
   */
  public static StandardChatNotificationParser getInstance() {
    return INSTANCE;
  }

  private StandardChatNotificationParser() {}

  @Override
  public ChatNotification fromXml(String xmlString) throws XmlParseException {
    // Create a new input stream for the given string
    try (
        InputStream xmlInputStream =
            new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))
    ) {
      // Parse the input stream
      return fromXml(xmlInputStream);
    } catch (IOException e) {
      throw new XmlParseException("Failed to close XML input stream", e);
    }
  }

  @Override
  public ChatNotification fromXml(InputStream xmlInputStream) throws XmlParseException {
    XMLStreamReader xmlStreamReader = null; // Not autocloseable
    try {
      xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlInputStream);
      return fromXmlThrowExceptions(xmlStreamReader);
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

  private ChatNotification fromXmlThrowExceptions(XMLStreamReader xmlStreamReader)
      throws XmlParseException, XMLStreamException {
    while (xmlStreamReader.next() != XMLStreamConstants.START_ELEMENT) {
      // skip to the first element
    }

    String xmlTypeName = xmlStreamReader.getLocalName();

    // Loop through each of the registered types
    for (Class<? extends ChatNotification> type : registeredTypes) {
      // Check if the name of the XML element matches that of the registered type
      if (type.getSimpleName().equals(xmlTypeName)) {
        // If it does, try and load it from XML
        return xmlReader.readFromXml(xmlStreamReader, type);
      }
    }

    // No registered type matches the XML name so we can't parse it
    throw new XmlParseException(
        "Received an XML request of type that is not supported: " + xmlTypeName
    );
  }
}
