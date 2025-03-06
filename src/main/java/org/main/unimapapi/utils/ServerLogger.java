package org.main.unimapapi.utils;

import org.main.unimapapi.configs.AppConfig;
import org.main.unimapapi.dtos.LogEntry;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {
    public enum Level {
        INFO, WARNING, ERROR
    }

    private static final String SERVER_LOG_FILE = "src/main/resources/org.main.unimapapi/logs/server_logs.xml";
    private static final String CLIENT_LOG_FILE = "src/main/resources/org.main.unimapapi/logs/client_logs.xml";
    private static final Level CONFIGURED_LEVEL = Level.valueOf(AppConfig.getLogLevel().toUpperCase());

    public static void logServer(Level level, String message) {
        if (level.ordinal() < CONFIGURED_LEVEL.ordinal()) {
            return;
        }
        log(level, message, SERVER_LOG_FILE, -1);
    }

    public static void logClient(LogEntry logEntry) {
        log(Level.valueOf(logEntry.getLevel()), logEntry.getMessage(), CLIENT_LOG_FILE, logEntry.getUserId());
    }


    private static synchronized void log(Level level, String message, String filePath, int userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        saveLogToXml(timestamp, level.toString(), message, filePath, userId);
    }

    private static void removeWhitespaceNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }

    private static synchronized void saveLogToXml(String timestamp, String level, String message, String filePath, int userId) {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (file.exists()) {
                doc = builder.parse(file);
                doc.getDocumentElement().normalize();
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("logs");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();

            removeWhitespaceNodes(root);

            Element log = doc.createElement("log");

            if (userId != -1) {
                Element userIdElem = doc.createElement("userId");
                userIdElem.appendChild(doc.createTextNode(String.valueOf(userId)));
                log.appendChild(userIdElem);
            }

            Element timestampElem = doc.createElement("timestamp");
            timestampElem.appendChild(doc.createTextNode(timestamp));
            log.appendChild(timestampElem);

            Element levelElem = doc.createElement("level");
            levelElem.appendChild(doc.createTextNode(level));
            log.appendChild(levelElem);

            Element messageElem = doc.createElement("message");
            messageElem.appendChild(doc.createTextNode(message));
            log.appendChild(messageElem);

            root.appendChild(log);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (Exception e) {
            System.err.println("SERVER LOGGING ERROR: " + e.getMessage());
        }
    }


}
