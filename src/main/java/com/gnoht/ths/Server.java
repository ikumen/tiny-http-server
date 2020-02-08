package com.gnoht.ths;

import com.gnoht.ths.handlers.ResourceHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ikumen@gnoht.com
 */
public class Server {

  private static final Logger logger = Logger.getLogger("server");

  private final int port;
  private final ExecutorService executor;
  private final DocumentRoot documentRoot;
  private final List<RequestHandler> requestHandlers;

  private ServerSocket server;

  public Server(int port, int threadPoolSize, Path documentRootPath) {
    this.port = port;
    this.executor = Executors.newFixedThreadPool(threadPoolSize);
    this.documentRoot = new DocumentRoot(documentRootPath);
    this.requestHandlers = Arrays.asList(
      new ResourceHandler(documentRoot)
    );

  }

  public void start() {
    logger.info("-----------------------------");
    logger.info("    Starting Tiny Http");
    logger.info("");
    logger.info("port=" + port);
    logger.info("documents=" + documentRoot);
    logger.info("-----------------------------");

    try {
      server = new ServerSocket(port);
      while (!server.isClosed()) {
        Socket socket = server.accept();
        executor.submit(new SocketProcessor(socket, requestHandlers));
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Server could not start", e);
    } finally {
      stop();
    }
  }

  public void stop() {
    logger.fine("Shutting down server");
    try {
      if (server != null) server.close();
      if (executor != null) executor.shutdown();
    } catch (IOException e) {
      logger.log(Level.WARNING, "Error shutting down server", e);
      // ignoring
    }
  }

  public static void main(String[] args) {
    new Server(8080, 10, Paths.get(""))
        .start();
  }
}
