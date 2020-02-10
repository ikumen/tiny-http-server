package com.gnoht.ths;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gnoht.ths.handlers.NotFoundHandler;
import com.gnoht.ths.handlers.ResourceHandler;
import com.gnoht.ths.handlers.UnsupportedMethodHandler;

/**
 * @author ikumen@gnoht.com
 */
public class Server {

  private static final Logger logger = Logger.getLogger("server");

  public static final int DEFAULT_PORT = 8000;
  public static final int DEFAULT_POOL_SIZE = 10;
  public static final String DEFAULT_DOCUMENT_ROOT = "";
  
  private final int port;
  private final ExecutorService executor;
  private final DocumentRoot documentRoot;
  private final List<RequestHandler> requestHandlers;

  private ServerSocket server;

  public Server(int port, Path documentRootPath, int threadPoolSize) {
    this.port = port;
    this.executor = Executors.newFixedThreadPool(threadPoolSize);
    this.documentRoot = new DocumentRoot(documentRootPath);
    this.requestHandlers = Arrays.asList(
      new ResourceHandler(documentRoot),
      new NotFoundHandler(documentRoot),
      new UnsupportedMethodHandler()
    );

  }

  public void start() {

    try {
      server = new ServerSocket(port);
      
      System.out.println(String.format("Server started on %s:%d", 
          server.getInetAddress().getHostName(), port));
      
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
    try {
      if (server != null && !server.isClosed()) {
        System.out.println("Shutting down server.");
        server.close();
      }
      if (executor != null) executor.shutdown();
    } catch (IOException e) {
      System.out.println("Error shutting down server");
      e.printStackTrace();
      // ignoring
    }
  }

  public static void main(String[] args) {
    Options options = Options.from(args);
    
    if (options.contains("help")) {
      // show usage
      System.out.println("Usage: "
          + "\njava -cp <path> com.gnoht.ths.Server \\"
          + "\n   --port=<number>          port to bind to, defaults to 8000"
          + "\n   --document-root=<path>   path to document root, defaults to current directory"
          + "\n   --pool-size=<number>     number of threads, defaults to 10"
          + "\n   --help                   display this message\n");
      return;
    }
    
    int port = options.getInt("port", DEFAULT_PORT);
    Path rootPath = Paths.get(options.get("document-root", DEFAULT_DOCUMENT_ROOT));
    int poolSize = options.getInt("pool-size", DEFAULT_POOL_SIZE);    
    
    Server server = new Server(port, rootPath, poolSize);
    //https://stackoverflow.com/questions/48510441/closing-socket-on-program-termination
    Runtime.getRuntime()
      .addShutdownHook(
        new Thread(() -> server.stop()));
    server.start();
  }
  
  static class Options {
    private final Map<String, String> options;
    
    public Options(Map<String, String> options) {
      this.options = options;
    }
    
    public static Options from(String[] args) {
      return new Options(Stream.of(args)
          .filter(s -> s.startsWith("--")) // found server option
          .map(s -> s.substring(2)) // remove --
          .map(s -> s.split("="))
          .collect(Collectors.toMap(
              a -> a[0].toLowerCase(), // store all options in lowercase 
              a -> a.length == 1 ? "" : a[1])));
    }
    
    public boolean contains(String key) {
      return options.containsKey(key);
    }
    
    public String get(String key) {
      return options.get(key);
    }
    
    public String get(String key, String defaultValue) {
      return options.getOrDefault(key, defaultValue);
    }
    
    public int getInt(String key) {
      return Integer.parseInt(options.get(key));
    }
    
    public int getInt(String key, int defaultValue) {
      return options.containsKey(key) 
        ? Integer.parseInt(options.get(key))
        : defaultValue;
    }
  }
}
