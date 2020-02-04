package com.gnoht.ths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ikumen@gnoht.com
 */
public class Options {

  public static final int DEFAULT_PORT = 8000;
  private static final String DEFAULT_ROOT_DIRECTORY = "public";

  private int port;
  private Path documentRootPath;
  private boolean help;

  public int getPort() {
    return port;
  }

  public Path getDocumentRootPath() {
    return documentRootPath;
  }

  public boolean requiresHelp() {
    return help;
  }

  public static Builder fromDefaults() {
    return new Builder()
        .requiresHelp(false)
        .documentRootPath(Paths.get(DEFAULT_ROOT_DIRECTORY).toAbsolutePath())
        .port(DEFAULT_PORT);
  }

  public static Builder fromStartUpArguments(String[] args) {
    Map<String, String> unparsedOpts = Stream.of(args)
        .filter(s -> s.startsWith("--"))
        .map(s -> s.substring(2))
        .map(s -> s.toLowerCase())
        .map(s -> s.split("="))
        .collect(Collectors.toMap(a -> a[0], a -> a.length == 1 ? "" : a[1].trim()));

    Builder builder = new Builder()
        .port(DEFAULT_PORT);

    String givenPath = unparsedOpts.getOrDefault("doc-root", "");
    if (!givenPath.isEmpty()) {
      builder.documentRootPath(givenPath.startsWith("/")
          ? Paths.get(givenPath)
          : Paths.get(getWorkingDirectoryPath().toString(), givenPath));
    } else {
      builder.documentRootPath(Paths.get(DEFAULT_ROOT_DIRECTORY).toAbsolutePath());
    }

    String givenPort = unparsedOpts.getOrDefault("port", "");
    if (!givenPort.isEmpty()) {
      try {
        builder.port(Integer.parseInt(givenPort));
      } catch (NumberFormatException e) {
        // ignore
      }
    }

    builder.requiresHelp(unparsedOpts.containsKey("help"));

    return builder;
  }


  private Options(int port, Path docPath, boolean help) {
    this.port = port;
    this.documentRootPath = docPath;
    this.help = help;
  }

  private static Path getWorkingDirectoryPath() {
    return Paths.get("").toAbsolutePath();
  }

  public static class Builder {
    private int port;
    private Path documentRootPath;
    private boolean help;

    private Builder() {}

    Builder port(int port) {
      if (port < 1 || port > 65535)
        throw new IllegalArgumentException("Port not in supported range: 1 to 65535!");
      this.port = port; return this;
    }

    Builder documentRootPath(Path path) {
      if (!Files.isDirectory(path))
        throw new IllegalArgumentException("Document root path does not exists or not a directory: " + path.toString());
      this.documentRootPath = path; return this;
    }

    Builder requiresHelp(boolean help) {
      this.help = help; return this;
    }

    public Options build() {
      return new Options(port, documentRootPath, help);
    }
  }
}
