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
    return uri.startsWith("/")
        ? resolve(uri.substring(1)) // recursively handle multiple slashes e.g, ////
        : root.resolve(uri);
  }
}
