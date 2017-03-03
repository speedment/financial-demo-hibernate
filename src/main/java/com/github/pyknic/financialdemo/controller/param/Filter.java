package com.github.pyknic.financialdemo.controller.param;

import com.google.gson.annotations.SerializedName;
import static java.util.Objects.requireNonNull;
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
public final class Filter<T> implements Specification<T>{

    public enum Operator {
        @SerializedName("eq") EQUAL,
        @SerializedName("ne") NOT_EQUAL,
        @SerializedName("lt") LESS_THAN,
        @SerializedName("le") LESS_OR_EQUAL,
        @SerializedName("gt") GREATER_THAN,
        @SerializedName("ge") GREATER_OR_EQUAL,
        @SerializedName("like") LIKE;
    }
    
    private final String property;
    private final Operator operator;
    private final String value;
    
    public Filter(
            String property, 
            Operator operator, 
            String value) {
        
        this.property = requireNonNull(property);
        this.operator = requireNonNull(operator);
        this.value    = requireNonNull(value);
    }
    
    public final String getProperty() {
        return property;
    }

    public final Operator getOperator() {
        return operator;
    }

    public final String getValue() {
        return value;
    }
    
    @Override
    public final Predicate toPredicate(
            Root<T> root, 
            CriteriaQuery<?> query, 
            CriteriaBuilder cb) {

        switch (getOperator()) {
            case EQUAL : 
                return cb.equal(root.get(getProperty()), getValue());
            case NOT_EQUAL : 
                return cb.notEqual(root.get(getProperty()), getValue());
            case GREATER_THAN : 
                return cb.greaterThan(root.get(getProperty()), getValue());
            case GREATER_OR_EQUAL : 
                return cb.equal(root.get(getProperty()), getValue());
            case LESS_THAN : 
                return cb.lessThan(root.get(getProperty()), getValue());
            case LESS_OR_EQUAL : 
                return cb.lessThanOrEqualTo(root.get(getProperty()), getValue());
            case LIKE :
                return cb.like(root.get(getProperty()), "%" + getValue() + "%");
            default :
                throw new UnsupportedOperationException(
                    "Operator " + getOperator() + " not supported."
                );
        }
    }

    @Override
    public final String toString() {
        return String.format(
            "%s{property='%s',operator='%s',value='%s'}",
            getClass(), property, operator, value
        );
    }
}