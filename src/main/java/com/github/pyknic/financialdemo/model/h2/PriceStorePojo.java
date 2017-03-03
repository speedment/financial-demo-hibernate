package com.github.pyknic.financialdemo.model.h2;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@Entity @Data
public class PriceStorePojo {
    private @Id Long id;
    private Integer valueDate;
    private Float open;
    private Float high;
    private Float low;
    private Float close;
    private String instrumentSymbol;
}