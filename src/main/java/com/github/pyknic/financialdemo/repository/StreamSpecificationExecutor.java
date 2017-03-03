package com.github.pyknic.financialdemo.repository;

import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @param <T> the entity type
 * 
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface StreamSpecificationExecutor<T> {
    
    long count(Specification<T> spec);
    
    Stream<T> findAll(Specification<T> spec);
    
}