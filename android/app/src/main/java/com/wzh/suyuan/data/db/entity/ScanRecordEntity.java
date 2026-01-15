package com.wzh.suyuan.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_record")
public class ScanRecordEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "trace_code")
    private String traceCode;

    @ColumnInfo(name = "scan_time")
    private long scanTime;

    @ColumnInfo(name = "product_name")
    private String productName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTraceCode() {
        return traceCode;
    }

    public void setTraceCode(String traceCode) {
        this.traceCode = traceCode;
    }

    public long getScanTime() {
        return scanTime;
    }

    public void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
