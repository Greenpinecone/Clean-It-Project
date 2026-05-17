package com.mk.backend.ordermanagement.web.dto.response;

import java.util.UUID;

public record CustomerSearchResponse(
    UUID id, String displayName, String email, String phoneNumber) {}
