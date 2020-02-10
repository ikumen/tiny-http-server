package com.gnoht.ths;

import com.gnoht.ths.handlers.HandlerException;

/**
 * @author ikumen@gnoht.com
 */
public interface RequestHandler {

  public static final String GENERIC_ERROR_TEMPLATE = "<html><body><h1>%d %s</h1></body></html>";
  
  boolean handle(Request request, Response response);
  
}
