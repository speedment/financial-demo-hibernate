package com.github.pyknic.financialdemo.common;

import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 *
 * @author  Emil Forslund
 * @since   1.0.0
 */
public class SizeCache {

    private final Map<String, Long> maps;

    public SizeCache() {
        this.maps = new ConcurrentHashMap<>();
    }

    public Long computeIfAbsent(String filter, LongSupplier supplier) {
        requireNonNull(supplier);
        return maps.computeIfAbsent(
            filter == null ? "[]" : filter, 
            $ -> supplier.getAsLong()
        );
    }

    public void clear() {
        maps.clear();
    }
}