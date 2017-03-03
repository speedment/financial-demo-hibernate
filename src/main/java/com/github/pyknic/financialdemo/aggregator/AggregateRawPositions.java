package com.github.pyknic.financialdemo.aggregator;

import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;
import java.util.Set;
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
public final class AggregateRawPositions
implements Collector<RawPositionPojo, PositionResult, PositionResult> {
    
    private final Function<RawPositionPojo, String> identifier;
    
    public AggregateRawPositions(
            Function<RawPositionPojo, String> identifier) {
        
        this.identifier = requireNonNull(identifier);
    }
    
    @Override
    public Supplier<PositionResult> supplier() {
        return () -> new PositionResult(identifier);
    }

    @Override
    public BiConsumer<PositionResult, RawPositionPojo> accumulator() {
        return PositionResult::aggregate;
    }

    @Override
    public BinaryOperator<PositionResult> combiner() {
        return PositionResult::aggregate;
    }

    @Override
    public Function<PositionResult, PositionResult> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return singleton(Collector.Characteristics.IDENTITY_FINISH);
    }
}