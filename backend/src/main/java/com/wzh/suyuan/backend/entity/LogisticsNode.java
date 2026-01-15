package com.wzh.suyuan.backend.entity;

import java.time.LocalDateTime;

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
@Table(name = "logistics_node")
public class LogisticsNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_code", nullable = false, length = 64)
    private String traceCode;

    @Column(name = "node_time", nullable = false)
    private LocalDateTime nodeTime;

    @Column(length = 128)
    private String location;

    @Column(name = "status_desc", length = 255)
    private String statusDesc;
}
