package com.payment.service;

import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;

public interface SalaryStructureService {

    SalaryStructureResponse createSalaryStructure(SalaryStructureRequest request);

    SalaryStructureResponse updateSalaryStructure(Long slipId);
}
