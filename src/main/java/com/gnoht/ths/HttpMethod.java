package com.gnoht.ths;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ikumen@gnoht.com
 */
public enum HttpMethod {
  GET("GET"),
  HEAD("HEAD"),
  PATCH("PATCH"),
  POST("POST"),
  PUT("PUT"),
  OPTIONS("OPTIONS"),
  TRACE("TRACE");

  private static Map<String, HttpMethod> methods = new HashMap<>();
  private final String verb;

  static {
    for (HttpMethod method: values())
      methods.put(method.verb, method);
  }

  HttpMethod(String verb) {
    this.verb = verb;
  }

  public static HttpMethod of(String verb) {
    HttpMethod method = methods.get(verb);
    if (method == null)
      throw new IllegalArgumentException("Invalid method verb: " + verb);
    return method;
  }
}
