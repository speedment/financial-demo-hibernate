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
public class RawPositionPojo {
    private @Id Long id;
    private Float pnl;
    private Float initiateTradingMktVal;
    private Float liquidateTradingMktVal;
    private Integer valueDate;
    private String traderName;
    private String traderGroup;
    private String traderGroupType;
    private String instrumentName;
    private String instrumentSymbol;
    private String instrumentSector;
    private String instrumentIndustry;
}