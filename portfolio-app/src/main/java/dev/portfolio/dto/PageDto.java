package dev.portfolio.dto;

import java.util.List;

public class PageDto<T> {
  public final List<T> items;
  public final long total;
  public final int page, size;

  public PageDto(List<T> items, long total, int page, int size) {
    this.items = items;
    this.total = total;
    this.page = page;
    this.size = size;
  }
}
