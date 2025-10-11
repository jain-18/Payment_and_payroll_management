package com.payment.service;

import com.payment.dto.SalaryStructureRequest;
import com.payment.dto.SalaryStructureResponse;

public interface SalaryStructureService {

    SalaryStructureResponse createSalaryStructure(SalaryStructureRequest request,Long orgId);

    SalaryStructureResponse updateSalaryStructure(Long slipId,Long orgId);
}
