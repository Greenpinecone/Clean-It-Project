package com.mk.backend.ordermanagement.application;

import com.mk.backend.ordermanagement.web.dto.request.CustomerSearchRequest;
import com.mk.backend.ordermanagement.web.dto.response.CustomerSearchResponse;
import java.util.List;

public interface CustomerService {

  List<CustomerSearchResponse> searchCustomers(CustomerSearchRequest request);
}
