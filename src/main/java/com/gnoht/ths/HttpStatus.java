package com.gnoht.ths;

/**
 * A simplified copy of the Spring HttpStatus enum,
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpStatus.html
 *
 * @author ikumen@gnoht.com
 */
public enum HttpStatus {

  BAD_REQUEST(400, "Bad Request"),
  FORBIDDEN(403, "Forbidden"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  NOT_FOUND(404, "Not Found"),
  OK(200, "OK");

  private final int code;
  private final String reason;

  private HttpStatus(int code, String reason) {
    this.code = code;
    this.reason = reason;
  }

  public int getCode() {
    return code;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public String toString() {
    return code + " " + reason;
  }
}
