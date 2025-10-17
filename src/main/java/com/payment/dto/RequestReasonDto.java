package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestReasonDto {
    private Long id;
    @NotBlank(message = "Reject reason must not be blank")
    private String rejectReason;
}
