package com.wzh.suyuan.backend.model;

import java.util.Locale;

public enum ProductStatus {
    ONLINE("online"),
    OFFLINE("offline");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductStatus from(String status) {
        if (status == null) {
            return null;
        }
        String trimmed = status.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        for (ProductStatus item : values()) {
            if (item.value.equals(normalized)) {
                return item;
            }
        }
        return null;
    }
}
