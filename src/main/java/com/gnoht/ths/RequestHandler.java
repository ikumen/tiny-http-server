package com.gnoht.ths;

/**
 * @author ikumen@gnoht.com
 */
public interface RequestHandler {

  boolean handle(Request request, Response response);
}
