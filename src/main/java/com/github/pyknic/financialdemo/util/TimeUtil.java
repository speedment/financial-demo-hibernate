package com.github.pyknic.financialdemo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class TimeUtil {
    
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    public static int toEpochSecs(String date) throws ParseException {
        return (int) (DATE_FORMAT.parse(date).getTime() / 1_000);
    }
    
    public static String fromEpochSecs(int epochSecs) {
        return DATE_FORMAT.format(Date.from(Instant.ofEpochSecond(epochSecs)));
    }
    
    public static String fromEpochMillis(long epochMillis) {
        return fromEpochSecs((int) (epochMillis / 1_000));
    }
    
    private TimeUtil() {}
}