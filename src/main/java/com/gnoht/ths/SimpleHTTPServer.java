package com.gnoht.ths;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ikumen@gnoht.com
 */
public class SimpleHTTPServer {

  private static final Logger logger = Logger.getLogger(SimpleHTTPServer.class.getSimpleName());

  private final int port;
  private final String headerTemplate = "HTTP/1.0 %d %s\r\n"
      + "Server: tiny 0.1\r\n"
      + "Content-length: %d\r\n"
      + "Content-type: %s; charset=UTF-8\r\n\r\n";

  public SimpleHTTPServer(int port) {
    this.port = port;
  }

  public void start() {
    ExecutorService pool = Executors.newFixedThreadPool(50);
    try (ServerSocket server = new ServerSocket(port)) {
      logger.info("Accepting connections on port: " + server.getLocalPort());

      while (true) {
        try {
          Socket conn = server.accept();
          pool.submit(new HTTPHandler(conn));
        } catch (IOException ex) {
          logger.log(Level.WARNING, "Exception accepting connection", ex);
        }
      }

    } catch (IOException ex) {
      logger.log(Level.SEVERE, "Could not start server", ex);
    }
  }

  public static void main(String[] args) {
    SimpleHTTPServer server = new SimpleHTTPServer(8090);
    server.start();
  }

  private class HTTPHandler implements Callable<Void> {

    private final Socket conn;

    public HTTPHandler(Socket conn) {
      this.conn = conn;
    }

    @Override
    public Void call() throws Exception {
      try {
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        InputStream in = new BufferedInputStream(conn.getInputStream());

        StringBuilder request = new StringBuilder();
        while (true) {
          int c = in.read();
          if (c == '\r' || c == '\n' || c == -1)
            break;
          request.append((char) c);
        }

        if (request.toString().indexOf("HTTP/") != -1) {
          logger.info("Client request: " + request.toString());
        }

        String response = "hello";
        String header = String.format(headerTemplate, 200, "OK", response.length(), "text/plain");
        out.write(header.getBytes(Charset.forName("US-ASCII")));
        out.write(response.getBytes());
        out.flush();
      } catch (IOException ex) {
        logger.log(Level.WARNING, "Unable to write to client", ex);
      } finally {
        conn.close();
      }
      return null;
    }
  }
}
