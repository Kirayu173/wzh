package com.wzh.suyuan.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceLogisticsResponse {
    private Long id;
    private LocalDateTime nodeTime;
    private String location;
    private String statusDesc;
}
