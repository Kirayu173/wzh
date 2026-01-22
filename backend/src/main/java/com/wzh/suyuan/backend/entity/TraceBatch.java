package com.wzh.suyuan.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_trace_batch_product"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Product product;

    @Column(name = "trace_code", nullable = false, unique = true, length = 64)
    private String traceCode;

    @Column(name = "batch_no", length = 64)
    private String batchNo;

    @Column(length = 128)
    private String origin;

    @Column(length = 128)
    private String producer;

    @Column(name = "harvest_date")
    private LocalDate harvestDate;

    @Column(name = "process_info", length = 2000)
    private String processInfo;

    @Column(name = "test_org", length = 128)
    private String testOrg;

    @Column(name = "test_date")
    private LocalDate testDate;

    @Column(name = "test_result", length = 255)
    private String testResult;

    @Column(name = "report_url", length = 255)
    private String reportUrl;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
