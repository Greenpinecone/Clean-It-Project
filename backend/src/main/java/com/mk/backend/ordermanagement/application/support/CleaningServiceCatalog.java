package com.mk.backend.ordermanagement.application.support;

import com.mk.backend.ordermanagement.model.CleaningService;
import com.mk.backend.ordermanagement.model.SelectedCleaningService;
import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CleaningServiceCatalog {

  private static final CleaningService STANDARD_CLEANING =
      new CleaningService(CleaningServiceCode.STANDARD_CLEANING, "Standard cleaning", 990);

  private static final CleaningService DRY_CLEANING =
      new CleaningService(CleaningServiceCode.DRY_CLEANING, "Dry cleaning", 1990);

  private static final CleaningService IRONING =
      new CleaningService(CleaningServiceCode.IRONING, "Ironing", 490);

  private static final CleaningService STAIN_REMOVAL =
      new CleaningService(CleaningServiceCode.STAIN_REMOVAL, "Stain removal", 790);

  private static final CleaningService EXPRESS_SERVICE =
      new CleaningService(CleaningServiceCode.EXPRESS_SERVICE, "Express service", 990);

  private static final List<CleaningService> SERVICES =
      List.of(STANDARD_CLEANING, DRY_CLEANING, IRONING, STAIN_REMOVAL, EXPRESS_SERVICE);

  private static final Map<CleaningServiceCode, CleaningService> SERVICES_BY_CODE =
      Map.of(
          STANDARD_CLEANING.code(), STANDARD_CLEANING,
          DRY_CLEANING.code(), DRY_CLEANING,
          IRONING.code(), IRONING,
          STAIN_REMOVAL.code(), STAIN_REMOVAL,
          EXPRESS_SERVICE.code(), EXPRESS_SERVICE);

  public List<CleaningService> findAll() {
    return SERVICES;
  }

  public CleaningService findByCode(CleaningServiceCode code) {
    CleaningService cleaningService = SERVICES_BY_CODE.get(code);

    if (cleaningService == null) {
      throw new IllegalArgumentException("Unknown cleaning service: %s.".formatted(code));
    }

    return cleaningService;
  }

  public SelectedCleaningService selectedServiceByCode(CleaningServiceCode code) {
    CleaningService service = findByCode(code);

    return new SelectedCleaningService(
        service.code(), service.displayName(), service.priceMinorUnits());
  }
}
