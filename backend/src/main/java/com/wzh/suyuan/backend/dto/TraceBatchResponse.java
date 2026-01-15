package com.wzh.suyuan.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceBatchResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String traceCode;
    private String batchNo;
    private String origin;
    private String producer;
    private LocalDate harvestDate;
    private String processInfo;
    private String testOrg;
    private LocalDate testDate;
    private String testResult;
    private String reportUrl;
    private LocalDateTime createTime;
}
