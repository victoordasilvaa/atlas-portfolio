package dev.portfolio.config;

public final class Environment {
  private Environment() {}

  public static String required(String key) {
    String v = System.getProperty(key, System.getenv(key));
    if (v == null || v.trim().isEmpty())
      throw new IllegalStateException("Configuração obrigatória: " + key);
    return v;
  }

  public static String value(String key, String fallback) {
    String v = System.getProperty(key, System.getenv(key));
    return v == null ? fallback : v;
  }
}
