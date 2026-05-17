package com.mk.backend.shared.config;

import java.time.Clock;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ValidationConfig {

  @Bean
  ValidationConfigurationCustomizer validationClockCustomizer(Clock clock) {
    return configuration -> configuration.clockProvider(() -> clock);
  }
}
