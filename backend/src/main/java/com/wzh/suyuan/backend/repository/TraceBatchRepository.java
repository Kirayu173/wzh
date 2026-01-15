package com.wzh.suyuan.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.TraceBatch;

public interface TraceBatchRepository extends JpaRepository<TraceBatch, Long> {
    Optional<TraceBatch> findByTraceCode(String traceCode);

    boolean existsByTraceCode(String traceCode);

    Page<TraceBatch> findByTraceCodeContainingIgnoreCase(String traceCode, Pageable pageable);
}
