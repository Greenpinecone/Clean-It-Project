package com.mk.backend.ordermanagement.application.support;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class ItemCodeGenerator {

  private final AtomicInteger itemSequence = new AtomicInteger(8);

  public String nextItemCode(int year) {
    return "TAG-%d-%08d".formatted(year, itemSequence.incrementAndGet());
  }
}
