package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryStructureRequest {

	@NotBlank(message = "Employee Id cannot be blank")
	private Long employeeId;
}
