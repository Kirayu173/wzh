package com.wzh.suyuan.data.db;

import java.math.BigDecimal;

import androidx.room.TypeConverter;

public class DbConverters {
    @TypeConverter
    public static BigDecimal toBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @TypeConverter
    public static String fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }
}
