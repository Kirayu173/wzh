package com.wzh.suyuan.backend.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceLogisticsRequest {
    @NotNull(message = "nodeTime required")
    private LocalDateTime nodeTime;

    @NotBlank(message = "location required")
    private String location;

    @NotBlank(message = "statusDesc required")
    private String statusDesc;
}
