package com.mk.backend.ordermanagement.model;

import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;

public record SelectedCleaningService(
    CleaningServiceCode code, String displayName, int priceMinorUnits) {}
