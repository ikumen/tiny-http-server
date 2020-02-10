package com.gnoht.ths;

import java.nio.file.Path;

/**
 * @author ikumen@gnoht.com
 */
public class DocumentRoot {

  private final Path root;

  public DocumentRoot(Path root) {
    this.root = root;
  }

  public boolean equals(Path path) {
    return root.equals(path);
  }

  public Path getSubPath(Path path) {
    return root.subpath(root.getNameCount(), path.getNameCount());
  }

  /**
   * Makes all requested uri relative to document root
   * @param uri
   * @return
   */
  public Path resolve(String uri) {
    // normalize the given uri (i.e, don't trust it)
    // - remove any query strings, we just want the path to the file
    int queryStart = uri.indexOf('?');
    if (queryStart != -1)
      uri = uri.substring(0, queryStart);
    // - remove any starting slash, this keep the requested paths confined to our document root    
    return uri.startsWith("/")
        ? resolve(uri.substring(1)) // recursively handle multiple slashes e.g, ////
        : root.resolve(uri);
  }
  
  
}
