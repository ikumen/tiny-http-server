/**
 * 
 */
package com.gnoht.ths.handlers;

import static com.gnoht.ths.HttpMethod.GET;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.gnoht.ths.DocumentRoot;
import com.gnoht.ths.HttpStatus;
import com.gnoht.ths.Request;
import com.gnoht.ths.RequestHandler;
import com.gnoht.ths.Response;
import com.gnoht.ths.Request.StartLine;
import com.gnoht.ths.support.IoHelper;

/**
 * @author ikumen@gnoht.com
 */
public class NotFoundHandler implements RequestHandler {

  private DocumentRoot documentRoot;

  public NotFoundHandler(DocumentRoot documentRoot) {
    this.documentRoot = documentRoot;
  }

  @Override
  public boolean handle(Request request, Response response) {
    StartLine startLine = request.getStartLine();
    if (!startLine.getMethod().equals(GET))
      return false;

    Path path = documentRoot.resolve(startLine.getUri());
    if (!Files.exists(path)) {
      IoHelper.sendError(HttpStatus.NOT_FOUND, response);
      return true; 
    }
    return false;
  }

}
