package com.wzh.suyuan.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wzh.suyuan.backend.dto.TraceBatchCreateRequest;
import com.wzh.suyuan.backend.dto.TraceBatchCreateResponse;
import com.wzh.suyuan.backend.dto.TraceBatchListResponse;
import com.wzh.suyuan.backend.dto.TraceBatchResponse;
import com.wzh.suyuan.backend.dto.TraceDetailResponse;
import com.wzh.suyuan.backend.dto.TraceLogisticsCreateResponse;
import com.wzh.suyuan.backend.dto.TraceLogisticsRequest;
import com.wzh.suyuan.backend.dto.TraceLogisticsResponse;
import com.wzh.suyuan.backend.entity.LogisticsNode;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.entity.TraceBatch;
import com.wzh.suyuan.backend.repository.LogisticsNodeRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;
import com.wzh.suyuan.backend.repository.TraceBatchRepository;

@Service
public class TraceService {
    private static final Logger log = LoggerFactory.getLogger(TraceService.class);
    private static final DateTimeFormatter TRACE_CODE_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final int TRACE_CODE_RETRY = 5;

    private final TraceBatchRepository traceBatchRepository;
    private final LogisticsNodeRepository logisticsNodeRepository;
    private final ProductRepository productRepository;

    public TraceService(TraceBatchRepository traceBatchRepository,
                        LogisticsNodeRepository logisticsNodeRepository,
                        ProductRepository productRepository) {
        this.traceBatchRepository = traceBatchRepository;
        this.logisticsNodeRepository = logisticsNodeRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public TraceBatchCreateResponse createBatch(Long adminId, TraceBatchCreateRequest request) {
        long start = System.currentTimeMillis();
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request required");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        TraceBatch batch = TraceBatch.builder()
                .productId(product.getId())
                .traceCode(generateTraceCode())
                .batchNo(trimToNull(request.getBatchNo()))
                .origin(trimToNull(request.getOrigin()))
                .producer(trimToNull(request.getProducer()))
                .harvestDate(request.getHarvestDate())
                .processInfo(trimToNull(request.getProcessInfo()))
                .testOrg(trimToNull(request.getTestOrg()))
                .testDate(request.getTestDate())
                .testResult(trimToNull(request.getTestResult()))
                .reportUrl(trimToNull(request.getReportUrl()))
                .createTime(LocalDateTime.now())
                .build();
        TraceBatch saved = saveWithRetry(batch);
        log.info("trace batch create success: adminId={}, traceCode={}, costMs={}",
                maskUserId(adminId), saved.getTraceCode(), System.currentTimeMillis() - start);
        return TraceBatchCreateResponse.builder()
                .id(saved.getId())
                .traceCode(saved.getTraceCode())
                .build();
    }

    public TraceDetailResponse getTraceDetail(String traceCode) {
        TraceBatch batch = traceBatchRepository.findByTraceCode(normalizeTraceCode(traceCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "trace not found"));
        String productName = productRepository.findById(batch.getProductId())
                .map(Product::getName)
                .orElse(null);
        TraceBatchResponse batchResponse = TraceBatchResponse.builder()
                .id(batch.getId())
                .productId(batch.getProductId())
                .productName(productName)
                .traceCode(batch.getTraceCode())
                .batchNo(batch.getBatchNo())
                .origin(batch.getOrigin())
                .producer(batch.getProducer())
                .harvestDate(batch.getHarvestDate())
                .processInfo(batch.getProcessInfo())
                .testOrg(batch.getTestOrg())
                .testDate(batch.getTestDate())
                .testResult(batch.getTestResult())
                .reportUrl(batch.getReportUrl())
                .createTime(batch.getCreateTime())
                .build();
        List<TraceLogisticsResponse> nodes = logisticsNodeRepository
                .findByTraceCodeOrderByNodeTimeDesc(batch.getTraceCode())
                .stream()
                .map(node -> TraceLogisticsResponse.builder()
                        .id(node.getId())
                        .nodeTime(node.getNodeTime())
                        .location(node.getLocation())
                        .statusDesc(node.getStatusDesc())
                        .build())
                .collect(Collectors.toList());
        return TraceDetailResponse.builder()
                .batch(batchResponse)
                .logistics(nodes)
                .build();
    }

    public TraceBatchListResponse listBatches(int page, int size, String keyword) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "id"));
        Page<TraceBatch> pageData;
        String safeKeyword = trimToNull(keyword);
        if (safeKeyword == null) {
            pageData = traceBatchRepository.findAll(pageable);
        } else {
            pageData = traceBatchRepository.findByTraceCodeContainingIgnoreCase(safeKeyword, pageable);
        }
        List<TraceBatch> batches = pageData.getContent();
        Map<Long, String> productNames = new HashMap<>();
        if (!batches.isEmpty()) {
            List<Long> productIds = batches.stream().map(TraceBatch::getProductId).collect(Collectors.toList());
            productRepository.findAllById(productIds)
                    .forEach(product -> productNames.put(product.getId(), product.getName()));
        }
        List<TraceBatchResponse> items = batches.stream()
                .map(batch -> toBatchResponse(batch, productNames.get(batch.getProductId())))
                .collect(Collectors.toList());
        return TraceBatchListResponse.builder()
                .items(items)
                .page(page)
                .size(size)
                .total(pageData.getTotalElements())
                .build();
    }

    @Transactional
    public TraceBatchResponse updateBatch(Long batchId, TraceBatchCreateRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request required");
        }
        TraceBatch batch = traceBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "trace not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        batch.setProductId(product.getId());
        batch.setBatchNo(trimToNull(request.getBatchNo()));
        batch.setOrigin(trimToNull(request.getOrigin()));
        batch.setProducer(trimToNull(request.getProducer()));
        batch.setHarvestDate(request.getHarvestDate());
        batch.setProcessInfo(trimToNull(request.getProcessInfo()));
        batch.setTestOrg(trimToNull(request.getTestOrg()));
        batch.setTestDate(request.getTestDate());
        batch.setTestResult(trimToNull(request.getTestResult()));
        batch.setReportUrl(trimToNull(request.getReportUrl()));
        TraceBatch saved = traceBatchRepository.save(batch);
        return toBatchResponse(saved, product.getName());
    }

    @Transactional
    public void deleteBatch(Long batchId) {
        TraceBatch batch = traceBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "trace not found"));
        logisticsNodeRepository.deleteByTraceCode(batch.getTraceCode());
        traceBatchRepository.delete(batch);
    }

    @Transactional
    public TraceLogisticsCreateResponse addLogisticsNode(String traceCode, TraceLogisticsRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request required");
        }
        String normalized = normalizeTraceCode(traceCode);
        if (!traceBatchRepository.existsByTraceCode(normalized)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trace not found");
        }
        LogisticsNode node = LogisticsNode.builder()
                .traceCode(normalized)
                .nodeTime(request.getNodeTime())
                .location(trimToNull(request.getLocation()))
                .statusDesc(trimToNull(request.getStatusDesc()))
                .build();
        LogisticsNode saved = logisticsNodeRepository.save(node);
        return TraceLogisticsCreateResponse.builder()
                .id(saved.getId())
                .build();
    }

    public byte[] generateQrCode(String traceCode) {
        String normalized = normalizeTraceCode(traceCode);
        if (!traceBatchRepository.existsByTraceCode(normalized)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trace not found");
        }
        QRCodeWriter writer = new QRCodeWriter();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BitMatrix matrix = writer.encode(normalized, BarcodeFormat.QR_CODE, 320, 320);
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException ex) {
            log.error("qrcode generate failed: traceCode={}", normalized, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "qrcode generate failed");
        }
    }

    private TraceBatch saveWithRetry(TraceBatch batch) {
        TraceBatch saved = null;
        String traceCode = batch.getTraceCode();
        for (int i = 0; i < TRACE_CODE_RETRY; i++) {
            try {
                batch.setTraceCode(traceCode);
                saved = traceBatchRepository.save(batch);
                break;
            } catch (DataIntegrityViolationException ex) {
                traceCode = generateTraceCode();
                saved = null;
            }
        }
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "trace code conflict");
        }
        return saved;
    }

    private String generateTraceCode() {
        String datePart = LocalDate.now().format(TRACE_CODE_DATE);
        int random = ThreadLocalRandom.current().nextInt(0, 10000);
        return "TR" + datePart + String.format("%04d", random);
    }

    private String normalizeTraceCode(String traceCode) {
        if (traceCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "traceCode required");
        }
        String trimmed = traceCode.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "traceCode required");
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private TraceBatchResponse toBatchResponse(TraceBatch batch, String productName) {
        return TraceBatchResponse.builder()
                .id(batch.getId())
                .productId(batch.getProductId())
                .productName(productName)
                .traceCode(batch.getTraceCode())
                .batchNo(batch.getBatchNo())
                .origin(batch.getOrigin())
                .producer(batch.getProducer())
                .harvestDate(batch.getHarvestDate())
                .processInfo(batch.getProcessInfo())
                .testOrg(batch.getTestOrg())
                .testDate(batch.getTestDate())
                .testResult(batch.getTestResult())
                .reportUrl(batch.getReportUrl())
                .createTime(batch.getCreateTime())
                .build();
    }

    private String maskUserId(Long userId) {
        if (userId == null) {
            return "***";
        }
        String value = String.valueOf(userId);
        if (value.length() <= 2) {
            return "***" + value;
        }
        return "***" + value.substring(value.length() - 2);
    }
}
