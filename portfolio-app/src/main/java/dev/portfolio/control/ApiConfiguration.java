package dev.portfolio.control;

import org.glassfish.jersey.server.ResourceConfig;

public class ApiConfiguration extends ResourceConfig {
  public ApiConfiguration() {
    register(ProjectResource.class);
    register(GlobalExceptionMapper.class);
    register(JsonParseProblemMapper.class);
    register(JsonMappingProblemMapper.class);
    register(JsonConfiguration.class);
    register(org.glassfish.jersey.jackson.JacksonFeature.class);
  }
}
