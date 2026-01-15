package com.wzh.suyuan.backend.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trace_batch")
public class TraceBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "trace_code", length = 64)
    private String traceCode;

    @Column(name = "batch_no", length = 64)
    private String batchNo;

    @Column(length = 128)
    private String origin;

    @Column(length = 128)
    private String producer;

    @Column(name = "harvest_date")
    private LocalDate harvestDate;

    @Column(name = "process_info")
    private String processInfo;

    @Column(name = "test_org", length = 128)
    private String testOrg;

    @Column(name = "test_date")
    private LocalDate testDate;

    @Column(name = "test_result")
    private String testResult;

    @Column(name = "report_url", length = 255)
    private String reportUrl;
}
