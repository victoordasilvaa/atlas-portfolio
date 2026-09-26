package dev.portfolio.control;

import com.fasterxml.jackson.databind.*;
import javax.ws.rs.ext.*;

@Provider
public class JsonConfiguration implements ContextResolver<ObjectMapper> {
  private final ObjectMapper mapper =
      new ObjectMapper()
          .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
          .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
          .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);

  public ObjectMapper getContext(Class<?> type) {
    return mapper;
  }
}
