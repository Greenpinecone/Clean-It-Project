package com.mk.backend.ordermanagement.application.support;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class OrderNumberGenerator {

  // Would be inferred from a database / dedicated number-range-table / centralized service
  private final AtomicInteger orderSequence = new AtomicInteger(5);

  public String nextOrderNumber(int year) {
    return "CI-%d-%04d".formatted(year, orderSequence.incrementAndGet());
  }
}
