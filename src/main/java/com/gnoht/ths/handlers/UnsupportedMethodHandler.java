/**
 * 
 */
package com.gnoht.ths.handlers;

import static com.gnoht.ths.HttpMethod.GET;

import com.gnoht.ths.HttpStatus;
import com.gnoht.ths.Request;
import com.gnoht.ths.RequestHandler;
import com.gnoht.ths.Response;
import com.gnoht.ths.Request.StartLine;
import com.gnoht.ths.support.IoHelper;

/**
 * @author ikumen@gnoht.com
 */
public class UnsupportedMethodHandler implements RequestHandler {

  @Override
  public boolean handle(Request request, Response response) {
    StartLine startLine = request.getStartLine();
    if (!startLine.getMethod().equals(GET)) {
      IoHelper.sendError(HttpStatus.METHOD_NOT_ALLOWED, response);
      return true;
    }
    return false;
  }

}
