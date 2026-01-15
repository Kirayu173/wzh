package com.wzh.suyuan.kit;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class FormatUtils {
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");

    private FormatUtils() {
    }

    public static String formatPrice(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return PRICE_FORMAT.format(value);
    }
}
