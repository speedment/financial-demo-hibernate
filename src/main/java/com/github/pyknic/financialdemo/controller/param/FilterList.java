package com.github.pyknic.financialdemo.controller.param;

import static java.util.Collections.emptyList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import static org.springframework.data.jpa.domain.Specifications.where;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class FilterList<T> implements Specification<T> {

    private final List<Specification<T>> filters;
    
    public FilterList() {
        this.filters = emptyList();
    }

    public FilterList(List<Specification<T>> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (filters.isEmpty()) {
            return cb.conjunction();
        }
        
        final Iterator<Specification<T>> it  = filters.iterator();
        Specifications<T> specs = where(it.next());
        while (it.hasNext()) {
            specs = specs.and(it.next());
        }
        
        return specs.toPredicate(root, query, cb);
    }
}