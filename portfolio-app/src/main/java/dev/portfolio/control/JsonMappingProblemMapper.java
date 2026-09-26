package dev.portfolio.control;

import com.fasterxml.jackson.databind.JsonMappingException;
import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1000)
public final class JsonMappingProblemMapper implements ExceptionMapper<JsonMappingException> {
  public Response toResponse(JsonMappingException exception) {
    return new GlobalExceptionMapper().toResponse(exception);
  }
}
