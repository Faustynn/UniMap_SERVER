package org.main.unimapapi.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.main.unimapapi.dtos.News_dto;
import org.main.unimapapi.repository_queries.NewsRepository;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SSE (Server-Sent Events) server component that streams news updates to connected clients.
 *
 * <p>Manages a list of long-lived HTTP connections using text/event-stream, and
 * broadcasts news updates periodically. Also sends keep-alive heartbeats every 15 seconds.</p>
 */
@Component
public class SseServer {
    private final List<SseClient> clients = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final NewsRepository newsRepository;
    private List<News_dto> lastNews;
    private final AtomicInteger eventId = new AtomicInteger(1);


    /**
     * Inner class representing a connected SSE client.
     */
    private static class SseClient {
        final HttpServletResponse response;
        Thread heartbeatThread;
        volatile boolean connected = true;

        SseClient(HttpServletResponse response, Thread heartbeatThread) {
            this.response = response;
            this.heartbeatThread = heartbeatThread;
        }

        void close() {
            connected = false;
            if (heartbeatThread != null && heartbeatThread.isAlive()) {
                heartbeatThread.interrupt();
            }
        }
    }

    /**
     * Constructs the SSE server and starts the background broadcasting task.
     */
    @Autowired
    public SseServer(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
        this.lastNews = newsRepository.findAllNews();
        startBroadcasting();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Handles a new incoming client connection.
     * Registers the client, starts a heartbeat thread, and sends initial data.
     *
     * @param request  the incoming HTTP request
     * @param response the HTTP response to write the SSE stream to
     * @throws IOException if connection cannot be initialized
     */
    public void handleClientConnection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/event-stream"); // headers
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");

        SseClient client = new SseClient(response, null);
        clients.add(client);

        Thread heartbeatThread = new Thread(() -> {
            try {
                while (client.connected) {
                    synchronized (client.response) {
                        try {
                            client.response.getWriter().write(": keepalive\n\n");
                            client.response.getWriter().flush();
                        } catch (IOException e) {
                            client.connected = false;
                            ServerLogger.logServer(ServerLogger.Level.INFO, "Client connection closed: " + e.getMessage());
                            break;
                        }
                    }
                    Thread.sleep(15000); // 15 sec.
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                clients.remove(client);
                ServerLogger.logServer(ServerLogger.Level.INFO, "Heartbeat thread stopped for client");
            }
        });

        heartbeatThread.setDaemon(true);
        client.heartbeatThread = heartbeatThread;
        heartbeatThread.start();


        // Send data to client
        boolean sentSuccessfully = sendAllNewsToClient(client);
        if (!sentSuccessfully) {
            clients.remove(client);
            client.close();
            return;
        }

        // Wait for client disconnecting
        try {
            heartbeatThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            clients.remove(client);
            client.close();
            ServerLogger.logServer(ServerLogger.Level.INFO, "Client disconnected from SSE");
        }
    }


    /**
     * Sends the current full news list to a specific client.
     *
     * @param client the target client
     * @return {@code true} if sent successfully, {@code false} if failed
     */
    private boolean sendAllNewsToClient(SseClient client) {
        try {
            if (!client.connected) {
                return false;
            }

            int currentId = eventId.get();
            String eventId = "id: " + currentId + "\n";
            String eventType = "event: news-list\n";
            String eventData = "data: " + objectMapper.writeValueAsString(lastNews) + "\n\n";

            synchronized (client.response) {
                client.response.getWriter().write(eventId + eventType + eventData);
                client.response.getWriter().flush();
            }
            return true;
        } catch (IOException e) {
            client.connected = false;
            clients.remove(client);
            client.close();
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error sending data to client: " + e.getMessage());
            return false;
        }
    }

    /**
     * Broadcasts a new news list to all connected clients if updated.
     *
     * @param newsList the updated list of news
     */
    private void broadcastNewsList(List<News_dto> newsList) {
        try {
            int currentId = eventId.incrementAndGet();
            String id = "id: " + currentId + "\n";
            String eventType = "event: news-list\n";
            String eventData = "data: " + objectMapper.writeValueAsString(newsList) + "\n\n";

            for (SseClient client : new CopyOnWriteArrayList<>(clients)) {
                if (!client.connected) {
                    clients.remove(client);
                    continue;
                }

                try {
                    client.response.getWriter().write(id + eventType + eventData);
                    client.response.getWriter().flush();
                } catch (IOException e) {
                    client.connected = false;
                    clients.remove(client);
                    client.close();
                    ServerLogger.logServer(ServerLogger.Level.ERROR, "Error broadcasting client: " + e.getMessage());
                }
            }
            ServerLogger.logServer(ServerLogger.Level.INFO, "Broadcasted news update to " + clients.size() + " clients");
        } catch (IOException e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error preparing broadcast data: " + e.getMessage());
        }
    }

    /**
     * Starts periodic polling and broadcasting news every 10 seconds.
     */
    private void startBroadcasting() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<News_dto> currentNews = newsRepository.findAllNews();

                if (!currentNews.equals(lastNews)) {
                    ServerLogger.logServer(ServerLogger.Level.INFO, "News update detected, broadcasting into clients");
                    lastNews = currentNews;
                    broadcastNewsList(currentNews);
                }
            } catch (Exception e) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Error in news broadcasting: " + e.getMessage());
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Clean shutdown hook — terminates threads and clears client list.
     */
    private void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (SseClient client : clients) {client.close();}
        clients.clear();

        ServerLogger.logServer(ServerLogger.Level.INFO, "SSE Server shutdown completed");
    }
}