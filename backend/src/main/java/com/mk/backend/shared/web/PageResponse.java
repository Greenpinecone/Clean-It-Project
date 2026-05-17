package com.mk.backend.shared.web;

import java.util.List;

public record PageResponse<T>(List<T> items, int page, int size, int totalItems, int totalPages) {

  public PageResponse {
    items = List.copyOf(items);
  }
}
