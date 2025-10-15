package com.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AdminData {
    private Long totalOrganizations;
    private Long totalActiveOrganizations;
    private Long totalInActiveOrganizations;
    private Long totalPendingRequest;
}
