package com.mk.backend.ordermanagement.web.dto.response;

import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;

public record CleaningServiceResponse(
    CleaningServiceCode code, String displayName, int priceMinorUnits) {}
