package dev.portfolio.control;

import com.fasterxml.jackson.core.JsonParseException;
import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1000)
public final class JsonParseProblemMapper implements ExceptionMapper<JsonParseException> {
  public Response toResponse(JsonParseException exception) {
    return new GlobalExceptionMapper().toResponse(exception);
  }
}
