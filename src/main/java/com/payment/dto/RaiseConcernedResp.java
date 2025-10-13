package com.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RaiseConcernedResp {
    private Long concernId;
    private String organizationName;
    private String raiseAt;
    private boolean isSolved;
}
