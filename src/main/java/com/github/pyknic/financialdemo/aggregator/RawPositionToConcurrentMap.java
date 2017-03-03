package com.github.pyknic.financialdemo.aggregator;

import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import static java.util.Collections.singleton;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class RawPositionToConcurrentMap<K>
implements Collector<RawPositionPojo, Map<K, PositionResult>, Map<K, PositionResult>> {
    
    @FunctionalInterface
    public interface ObjLongFunction<T, R> {
        R apply(T obj, long ref);
    }

    private final Function<RawPositionPojo, K> classifier;
    private final Function<RawPositionPojo, String> identifier;

    public RawPositionToConcurrentMap(
            Function<RawPositionPojo, K> classifier,
            Function<RawPositionPojo, String> identifier) {
        
        this.classifier = requireNonNull(classifier);
        this.identifier = requireNonNull(identifier);
    }
    
    @Override
    public Supplier<Map<K, PositionResult>> supplier() {
        return ConcurrentHashMap::new;
    }

    @Override
    public BiConsumer<Map<K, PositionResult>, RawPositionPojo> accumulator() {
        return (result, pojo) -> result.computeIfAbsent(
            classifier.apply(pojo), 
            k -> new PositionResult(identifier)
        ).aggregate(pojo);
    }

    @Override
    public BinaryOperator<Map<K, PositionResult>> combiner() {
        return (first, second) -> {
            second.forEach((key, value) ->
                first.merge(key, value, PositionResult::aggregate)
            );
            return first;
        };
    }

    @Override
    public Function<Map<K, PositionResult>, Map<K, PositionResult>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return singleton(Collector.Characteristics.IDENTITY_FINISH);
    }
}