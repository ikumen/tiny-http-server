package com.gnoht.ths;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ikumen@gnoht.com
 */
public class SocketProcessor implements Callable<Void> {

  private static final Logger logger = Logger.getLogger(SocketProcessor.class.getSimpleName());

  private final Socket socket;
  private final List<RequestHandler> requestHandlers;

  public SocketProcessor(Socket socket, List<RequestHandler> requestHandlers) {
    this.socket = socket;
    this.requestHandlers = requestHandlers;
  }

  @Override
  public Void call() {
    try {
      logger.info("building request");
      Request request = Request.from(socket.getInputStream());
      logger.info("building response");
      Response response = new Response(socket.getOutputStream());

      for (RequestHandler handler : requestHandlers) {
        logger.info("handling request");
        if (handler.handle(request, response)) {
          logger.log(Level.INFO, "request handled: " + socket.getRemoteSocketAddress());
          break;
        }
      }
    } catch (IOException e) {
      logger.log(Level.WARNING, "Unable to process", e);
      // basically ignore
    }
    return null;
  }
}
