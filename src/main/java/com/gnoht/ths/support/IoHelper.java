/**
 * 
 */
package com.gnoht.ths.support;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gnoht.ths.HttpStatus;
import com.gnoht.ths.RequestHandler;
import com.gnoht.ths.Response;

/**
 * Just a copy of apache.commons-io.IOUtils.
 * 
 * @author ikumen@gnoht.com
 */
public class IoHelper {
  
  private static final Logger logger = Logger.getLogger(IoHelper.class.getSimpleName());
  
  public static final String GENERIC_ERROR_TEMPLATE = "<html><body><h1>%d %s</h1></body></html>";
  public static final String CRLF = "\r\n";
  
  public static void sendError(HttpStatus status, Socket socket) {
    try {
      sendError(status, new Response(socket.getOutputStream()));
    } catch (IOException e) {
      logger.log(Level.WARNING, "Unable to send error response.", e);
    } finally {
      closeQuietly(socket);
    }
  }
  
  public static void sendError(HttpStatus status, Response response) {
    try {
      String page = String.format(GENERIC_ERROR_TEMPLATE, status.getCode(), status.getReason());
      response
        .setStatus(status)
          .sendStatus()
        .addHeader("Content-Type", "text/html")
        .addHeader("Content-Length", String.valueOf(page))
          .sendHeaders()
        .write(IoHelper.CRLF)
        .write(page)
        .flush();      
    } catch (IOException e) {
      logger.log(Level.WARNING, "Unable to send error response", e);
    } finally {
      closeQuietly(response.getOutputStream());
    }
  }

  public static void closeQuietly(final Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException e) {
        logger.log(Level.WARNING, "Error while closing", e);
      }
    }
  }
}
