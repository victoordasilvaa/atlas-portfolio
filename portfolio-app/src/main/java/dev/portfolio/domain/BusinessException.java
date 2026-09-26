package dev.portfolio.domain;

public final class BusinessException extends RuntimeException {
  private final String code;
  private final int status;

  public BusinessException(String code, String message, int status) {
    super(message);
    this.code = code;
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public int getStatus() {
    return status;
  }

  public static BusinessException invalid(String message) {
    return new BusinessException("VALIDATION", message, 422);
  }

  public static BusinessException conflict(String message) {
    return new BusinessException("CONFLICT", message, 409);
  }
}
