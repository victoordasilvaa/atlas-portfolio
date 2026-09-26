package dev.portfolio.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.portfolio.domain.BusinessException;
import java.util.*;
import java.util.logging.*;
import javax.persistence.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
  private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

  public Response toResponse(Exception e) {
    int status = 500;
    String code = "INTERNAL_ERROR", message = "Não foi possível concluir a operação.";
    if (e instanceof BusinessException) {
      BusinessException b = (BusinessException) e;
      status = b.getStatus();
      code = b.getCode();
      message = b.getMessage();
    } else if (e instanceof OptimisticLockException) {
      status = 409;
      code = "STALE_VERSION";
      message = "O projeto foi alterado por outra requisição.";
    } else if (e instanceof JsonProcessingException || e instanceof IllegalArgumentException) {
      status = 400;
      code = "INVALID_REQUEST";
      message = "Requisição inválida. Verifique os tipos e os campos enviados.";
    } else if (e instanceof WebApplicationException) {
      status = ((WebApplicationException) e).getResponse().getStatus();
      code = "HTTP_" + status;
      message = "Requisição não aceita.";
    }
    String trace = UUID.randomUUID().toString();
    if (status >= 500) LOG.log(Level.SEVERE, "Falha " + trace, e);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", status);
    body.put("code", code);
    body.put("message", message);
    body.put("traceId", trace);
    return Response.status(status)
        .type("application/problem+json")
        .entity(body)
        .header("X-Request-Id", trace)
        .build();
  }
}
