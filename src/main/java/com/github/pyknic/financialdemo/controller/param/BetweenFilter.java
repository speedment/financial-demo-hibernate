package com.github.pyknic.financialdemo.controller.param;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @param <T>  the entity type
 * 
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class BetweenFilter<T> implements Specification<T> {

    private final Integer from, to;

    public BetweenFilter(Integer from, Integer to) {
        this.from = from;
        this.to   = to;
    }
    
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.between(root.get("valueDate"), from, to);
    }

}