package com.mk.backend.ordermanagement.mapper;

import com.mk.backend.ordermanagement.model.CleaningService;
import com.mk.backend.ordermanagement.web.dto.response.CleaningServiceResponse;
import org.springframework.stereotype.Component;

@Component
public class CleaningServiceMapper {

  public CleaningServiceResponse toResponse(CleaningService cleaningService) {
    return new CleaningServiceResponse(
        cleaningService.code(), cleaningService.displayName(), cleaningService.priceMinorUnits());
  }
}
