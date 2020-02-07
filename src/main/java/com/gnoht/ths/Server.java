package com.gnoht.ths;

import com.gnoht.ths.handlers.DirectoryHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ikumen@gnoht.com
 */
public class Server {

  private static final Logger logger = Logger.getLogger("server");

  public static final int DEFAULT_PORT = 8000;
  public static final int DEFAULT_POOL_SIZE = 10;
  public static final String DEFAULT_DOCUMENT_ROOT = "";

  private final int port;
  private final ExecutorService executorService;
  private final Path documentRoot;
  private final List<RequestHandler> requestHandlers = new ArrayList<>();
  private ServerSocket server;

  public Server(int port, int poolSize, Path documentRoot) {
    this.port = port;
    this.executorService = Executors.newFixedThreadPool(poolSize);
    this.documentRoot = documentRoot;

    requestHandlers.add(new DirectoryHandler(new DocumentRoot(documentRoot)));
  }

  public void start() {

    try {
      logger.info("------------------------------------------------");
      logger.info("Starting Tiny HTTP...");
      logger.info("port=" + port);
      logger.info("document-root=" + documentRoot.toString());
      logger.info("------------------------------------------------");

      server = new ServerSocket(port);
      while (true) {
        System.out.println("Waiting for connection");
        Socket socket = server.accept();
        System.out.println("new connection");
        executorService.submit(new SocketProcessor(socket, requestHandlers));
      }
    } catch (IOException e) {
      logger.log(Level.WARNING, "Unable to accept client connection", e);
    }
  }

  public void stop() {
    try {
      System.out.println("Shutting down server");
      if (server != null && !server.isClosed())
        server.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // TODO: move into entry point/app class
  public static void main(String[] args) {

    Settings settings = parseSettings(args);
    if (settings.contains("help")) {
      displayHelp();
      return;
    }

    int port = settings.getInt("port", DEFAULT_PORT);
    int poolSize = settings.getInt("pool-size", DEFAULT_POOL_SIZE);
    Path documentRoot = Paths.get(settings.get("document-root", DEFAULT_DOCUMENT_ROOT)).toAbsolutePath();

    Server server = new Server(port, poolSize, documentRoot);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
    server.start();
  }

  private static void displayHelp() {
    System.out.println(
        "Usage: java [-cp <path>] com.gnoht.ths.Server \n"
            + "                     [--document-root=<PATH>; default=\""+ DEFAULT_DOCUMENT_ROOT +"\"]\n"
            + "                     [--pool-size=<NUMBER>; default=" + DEFAULT_POOL_SIZE + "\n"
            + "                     [--port=<NUMBER>; default=" + DEFAULT_PORT +"]\n");
  }

  private static Settings parseSettings(String[] args) {
    return new Settings(Stream.of(args)
        .filter(s -> s.startsWith("--"))
        .map(s -> s.substring(2)) // removes --
        .map(s -> s.split("="))
        .collect(Collectors.toMap(
            a -> a[0].toLowerCase(),
            a -> a.length == 1 ? "" : a[1].trim())));
  }

  private static class Settings {
    private final Map<String, String> settings;

    Settings(Map<String, String> settings) {
      this.settings = settings;
    }

    boolean contains(String key) {
      return settings.containsKey(key);
    }

    String get(String key, String defaultValue) {
      return settings.getOrDefault(key, defaultValue);
    }

    int getInt(String key, int defaultValue) {
      String value = settings.getOrDefault(key, String.valueOf(defaultValue));
      try {
        return Integer.valueOf(value);
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    }
  }

}
