package com.gnoht.ths.handlers;

import static com.gnoht.ths.HttpMethod.GET;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.gnoht.ths.DocumentRoot;
import com.gnoht.ths.HttpStatus;
import com.gnoht.ths.Request;
import com.gnoht.ths.Request.StartLine;
import com.gnoht.ths.RequestHandler;
import com.gnoht.ths.Response;
import com.gnoht.ths.support.IoHelper;

/**
 * Generic {@link com.gnoht.ths.RequestHandler} that returns a file or directory
 * listing for all valid "GET" request.
 *
 * @author ikumen@gnoht.com
 */
public class ResourceHandler implements RequestHandler {

  private static final Logger logger = Logger.getLogger(ResourceHandler.class.getSimpleName());
  
  private static final String[] INDEX_FILE_NAMES = { "index.html", "index.htm" };
  private final DocumentRoot documentRoot;

  public ResourceHandler(DocumentRoot documentRoot) {
    this.documentRoot = documentRoot;
  }

  @Override
  public boolean handle(Request request, Response response) {
    StartLine startLine = request.getStartLine();
    if (!startLine.getMethod().equals(GET))
      return false;

    try {
      Path path = documentRoot.resolve(startLine.getUri());
      if (!Files.exists(path)) 
        return false;

      if (!Files.isDirectory(path)) {
        serveFile(path, response);
      } else {
        Path indexPath = getIndexFile(path);
        if (indexPath != null) {
          serveFile(indexPath, response);
        } else {
          serveDirectoryListing(startLine.getUri(), path, response);
        }
      }
      return true;
    } catch (IOException e) {
      throw new HandlerException(e, "Unable to handle request: " + startLine.getUri());
    }
  }
  
  private void serveFile(Path path, Response response) throws IOException {
    byte[] bytes = Files.readAllBytes(path);
    response
        .setStatus(HttpStatus.OK)
          .sendStatus()
        .addHeader("Content-Type", Files.probeContentType(path))
        .addHeader("Content-Length", String.valueOf(bytes.length))
          .sendHeaders()
          .flush()
        .write(IoHelper.CRLF)
        .write(bytes)
          .flush();    
  }
  
  private void serveDirectoryListing(String uri, Path path, Response response) throws IOException {
    String listing = buildDirectoryListing(uri, path);
    response
      .setStatus(HttpStatus.OK)
        .sendStatus()
      .addHeader("Content-Type", "text/html")
      .addHeader("Content-Length", String.valueOf(listing.length()))
        .sendHeaders()
        .flush()
      .write(IoHelper.CRLF)
      .write(listing)
        .flush();
  }
  
  private Path getIndexFile(Path path) {
    return Stream.of(INDEX_FILE_NAMES)
      .filter(f -> Files.exists(path.resolve(f)))
      .map(path::resolve)
      .findFirst()
      .orElse(null);
  }
    
  private String createListingLink(String uri, Path path) {
    String fname = path.getFileName().toString();
    String suffix = Files.isDirectory(path) ? "/" : "";
    return String.format("<li><a href='%s/%s'>%s%s</a></li>", uri, fname, fname, suffix);
  }

  private String getParentUri(String uri) {
    int lastSlash = uri.lastIndexOf('/');
    return lastSlash == 0 ? "/" : uri.substring(0, lastSlash);
  }

  private String buildDirectoryListing(String uri, Path path) throws IOException {

    boolean isRoot = documentRoot.equals(path);
    final StringBuilder sb = new StringBuilder("<html><head><title>" + uri 
        + "</title></head><body><h1>Index of " + uri + "</h1>");

    if (!isRoot) {
      sb.append("<a href='" + getParentUri(uri) 
        + "'><-- parent directory</a><br/>");
    }

    sb.append("<hr/><ul>");
    Files.list(path).forEach(p -> sb.append(createListingLink(isRoot ? "" : uri, p)));
    sb.append("</ul></body></html>");

    return sb.toString();
  }

}
