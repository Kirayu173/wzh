package com.wzh.suyuan.backend.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceBatchCreateRequest {
    @NotNull(message = "productId required")
    private Long productId;

    private String batchNo;

    @NotBlank(message = "origin required")
    private String origin;

    @NotBlank(message = "producer required")
    private String producer;

    private LocalDate harvestDate;

    private String processInfo;

    private String testOrg;

    private LocalDate testDate;

    private String testResult;

    private String reportUrl;
}
