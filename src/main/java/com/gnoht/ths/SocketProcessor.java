package com.gnoht.ths;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gnoht.ths.handlers.HandlerException;
import com.gnoht.ths.support.IoHelper;

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
      Request request = Request.from(socket.getInputStream());
      Response response = new Response(socket.getOutputStream());

      boolean handled = false; 
      for (RequestHandler handler : requestHandlers) {
        handled = handler.handle(request, response);
        if (handled) {
          logger.log(Level.INFO, "Request handled: " + socket.getRemoteSocketAddress());
          break;
        }
      }
      
      if (!handled) {
        IoHelper.sendError(HttpStatus.INTERNAL_SERVER_ERROR, response);
      }
      
    } catch (IOException | IllegalArgumentException e ) {
      logger.log(Level.WARNING, "Unable to process connection", e);
    } catch (HandlerException e) {
      IoHelper.sendError(HttpStatus.INTERNAL_SERVER_ERROR, socket);
    } finally {
      IoHelper.closeQuietly(socket);
    }
    return null;
  }
}
