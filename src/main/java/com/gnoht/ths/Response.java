package com.gnoht.ths;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ikumen@gnoht.com
 */
public class Response {

  public static final String DEFAULT_VERSION = "HTTP/1.0";
  private static final String STATUS_LINE_FORMAT = "%s %d %s \r\n";
  private static final String HEADER_FORMAT = "%s: %s\r\n";

  private final OutputStream outputStream;

  private Map<String, String> headers;
  private String version = DEFAULT_VERSION;
  private HttpStatus status;

  public Response(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public Response addHeader(String name, String value) {
    getHeaders().put(name, value);
    return this;
  }

  public Response setHeaders(Map<String, String> headers) {
    this.headers = headers; return this; // for chaining
  }

  public Response setVersion(String version) {
    this.version = version; return this;
  }

  public Response setStatus(HttpStatus status) {
    this.status = status; return this;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public Response write(String s) throws IOException {
    outputStream.write(s.getBytes()); return this;
  }
  
  public Response write(byte[] bytes) throws IOException {
    getOutputStream().write(bytes); return this;
  }

  public Response sendHeaders() throws IOException {
    for (String name: getHeaders().keySet())
      write(String.format(HEADER_FORMAT, name, getHeader(name)));
    return this;
  }

  public Response sendStatus() throws IOException {
    if (status == null)
      throw new IllegalArgumentException("HTTP status not set");
    write(String.format(STATUS_LINE_FORMAT, getVersion(), status.getCode(), status.getReason()));
    return this;
  }

  public Response flush() throws IOException {
    outputStream.flush(); return this;
  }

  public Map<String, String> getHeaders() {
    if (headers == null)
      headers = new HashMap<>();
    return headers;
  }

  public String getHeader(String name) {
    return getHeaders().get(name);
  }

  public String getVersion() {
    return version;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
