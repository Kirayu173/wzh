package com.wzh.suyuan.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceDetailResponse {
    private TraceBatchResponse batch;
    private List<TraceLogisticsResponse> logistics;
}
