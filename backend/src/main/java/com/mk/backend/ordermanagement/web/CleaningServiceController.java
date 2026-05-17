package com.mk.backend.ordermanagement.web;

import com.mk.backend.ordermanagement.application.CleaningServiceService;
import com.mk.backend.ordermanagement.web.dto.response.CleaningServiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cleaning-services")
@Tag(name = "Cleaning services")
public class CleaningServiceController {

  private final CleaningServiceService cleaningServiceService;

  CleaningServiceController(CleaningServiceService cleaningServiceService) {
    this.cleaningServiceService = cleaningServiceService;
  }

  @GetMapping
  @Operation(summary = "Get available cleaning services")
  public ResponseEntity<List<CleaningServiceResponse>> findAll() {
    return ResponseEntity.ok(cleaningServiceService.findAll());
  }
}
