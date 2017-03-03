package com.github.pyknic.financialdemo.common;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class DateIntToShortMapper {

    private final static int DAY_MASK   = 0b0000_0000_0001_1111;
    private final static int MONTH_MASK = 0b0000_0001_1110_0000;
    private final static int YEAR_MASK  = 0b1111_1110_0000_0000;

    public Short toJavaType(Integer value) {
        if (value == null) return null;
        final int date = value;

        return (short) (
            ((date - (date / 100) * 100) & DAY_MASK) |
            ((((date / 100) - ((date / 10_000) * 100)) << 5) & MONTH_MASK) |
            (((date / 10_000 - 1970) << 9) & YEAR_MASK)
        );
    }

    public Integer toDatabaseType(Short value) {
        if (value == null) return null;
        final short encoded = value;
        
        final int day   =   encoded & DAY_MASK;
        final int month =  (encoded & MONTH_MASK) >>> 5;
        final int year  = ((encoded & YEAR_MASK ) >>> 9) + 1970;
        
        return 10_000 * year + 100 * month + day;
    }
}