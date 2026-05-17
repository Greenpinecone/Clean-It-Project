package com.mk.backend.ordermanagement.application;

import com.mk.backend.ordermanagement.web.dto.response.CleaningServiceResponse;
import java.util.List;

public interface CleaningServiceService {

  List<CleaningServiceResponse> findAll();
}
