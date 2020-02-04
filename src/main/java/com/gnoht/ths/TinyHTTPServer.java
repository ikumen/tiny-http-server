package com.gnoht.ths;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ikumen@gnoht.com
 */
public class TinyHTTPServer {

  private static final Logger logger = Logger.getLogger("server");

  public void start(Options options) {
    ExecutorService connPool = Executors.newFixedThreadPool(50);
    try (ServerSocket server = new ServerSocket(options.getPort())) {
      logger.info("TinyHTTP started and listening on port " + options.getPort());

      while (true) {
        Socket conn = server.accept();

      }

    } catch (IOException ex) {

    }
  }


  public static void main(String[] args) {

    Options options = Options.fromStartUpArguments(args).build();

    if (options.requiresHelp()) {
      printHelpMessage();
      return;
    }

    new TinyHTTPServer().start(options);
  }

  private static void printHelpMessage() {
    System.out.println(
        "Usage: java [-cp <path>] com.gnoht.ths.TinyHTTPServer \n"
            + "                        [--doc-root=<PATH>; default=\"public\"]\n"
            + "                        [--port=<NUMBER>; default=8000]");
  }

}
