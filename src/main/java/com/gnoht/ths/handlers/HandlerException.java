package com.gnoht.ths.handlers;

/**
 * @author ikumen@gnoht.com
 */
public class HandlerException extends RuntimeException {
  public HandlerException(Throwable cause, String msg, Object ...args) {
    super(String.format(msg, args), cause);
  }

  public HandlerException(String msg, Object ...args) {
    super(String.format(msg, args));
  }

}
