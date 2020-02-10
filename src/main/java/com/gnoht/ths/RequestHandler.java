package com.gnoht.ths;

import com.gnoht.ths.handlers.HandlerException;

/**
 * @author ikumen@gnoht.com
 */
public interface RequestHandler {

  boolean handle(Request request, Response response);
  
}
