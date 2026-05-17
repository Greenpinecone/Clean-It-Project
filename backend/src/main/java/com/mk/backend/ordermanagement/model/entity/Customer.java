package com.mk.backend.ordermanagement.model.entity;

import java.util.UUID;

public record Customer(UUID id, String displayName, String email, String phoneNumber) {}
