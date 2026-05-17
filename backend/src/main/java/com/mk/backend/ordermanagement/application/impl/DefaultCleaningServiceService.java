package com.mk.backend.ordermanagement.application.impl;

import com.mk.backend.ordermanagement.application.CleaningServiceService;
import com.mk.backend.ordermanagement.application.support.CleaningServiceCatalog;
import com.mk.backend.ordermanagement.mapper.CleaningServiceMapper;
import com.mk.backend.ordermanagement.web.dto.response.CleaningServiceResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class DefaultCleaningServiceService implements CleaningServiceService {

  private final CleaningServiceCatalog cleaningServiceCatalog;
  private final CleaningServiceMapper cleaningServiceMapper;

  DefaultCleaningServiceService(
      CleaningServiceCatalog cleaningServiceCatalog, CleaningServiceMapper cleaningServiceMapper) {
    this.cleaningServiceCatalog = cleaningServiceCatalog;
    this.cleaningServiceMapper = cleaningServiceMapper;
  }

  @Override
  public List<CleaningServiceResponse> findAll() {
    return cleaningServiceCatalog.findAll().stream()
        .map(cleaningServiceMapper::toResponse)
        .toList();
  }
}
