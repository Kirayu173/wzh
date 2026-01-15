package com.wzh.suyuan.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.LogisticsNode;

public interface LogisticsNodeRepository extends JpaRepository<LogisticsNode, Long> {
    List<LogisticsNode> findByTraceCodeOrderByNodeTimeDesc(String traceCode);

    void deleteByTraceCode(String traceCode);
}
