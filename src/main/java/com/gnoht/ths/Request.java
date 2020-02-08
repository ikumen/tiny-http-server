package com.gnoht.ths;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author ikumen@gnoht.com
 */
public class Request {

  public final static Pattern START_LINE_REGEX = Pattern.compile(" ");
  public final static Pattern HEADER_LINE_REGEX = Pattern.compile(": ");

  private final StartLine startLine;
  private final Map<String, String> headers;
  protected final InputStream inputStream;

  public Request(StartLine startLine, Map<String, String> headers, InputStream inputStream) {
    this.startLine = startLine;
    this.headers = headers;
    this.inputStream = inputStream;
  }

  public StartLine getStartLine() {
    return startLine;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getHeader(String key) {
    return headers.get(key);
  }

  public byte[] getBody() throws IOException {
    throw new UnsupportedOperationException("This Request implementation does not support body.");
  }

  public static Request from(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    StartLine startLine = StartLine.parse(reader.readLine());
    Map<String, String> headers = parseHeaders(reader);
    return new Request(startLine, headers, in);
  }

  private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
    Map<String, String> headers = new HashMap<>();
    while (true) {
      String line = reader.readLine();
      if (line == null || line.isEmpty())
        break;
      String[] parts = HEADER_LINE_REGEX.split(line);
      if (parts.length >= 2)
        headers.put(parts[0], parts[1]);
      // ignoring invalid headers
    }
    return headers;
  }


  public static class StartLine {

    private HttpMethod method;
    private String uri;
    private String version;

    public StartLine(HttpMethod method, String uri, String version) {
      this.method = method;
      this.uri = uri;
      this.version = version;
    }

    public HttpMethod getMethod() {
      return method;
    }

    public String getUri() {
      return uri;
    }

    public String getVersion() {
      return version;
    }

    @Override
    public String toString() {
      return method.name() + " " + uri + ' ' + version + "\r\n";
    }

    public static StartLine parse(String line) {
      String[] parts = START_LINE_REGEX.split(line);
      if (parts.length != 3) {
        throw new IllegalArgumentException("Invalid HTTP/1.0 request line: " + line);
      }
      return new StartLine(HttpMethod.of(parts[0].toUpperCase()), parts[1], parts[2]);
    }
  }
}
