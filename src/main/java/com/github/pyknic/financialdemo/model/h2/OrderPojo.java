package com.github.pyknic.financialdemo.model.h2;

import com.github.pyknic.financialdemo.extra.BuySell;
import com.github.pyknic.financialdemo.extra.CohortType;
import com.github.pyknic.financialdemo.extra.OrderType;
import com.github.pyknic.financialdemo.extra.Status;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@Entity @Data
public class OrderPojo {

    private @Id Long id;
    private Integer dateCreated;
    private @Enumerated(EnumType.STRING) BuySell direction;
    private @Enumerated(EnumType.STRING) OrderType orderType;
    private Integer quantity;
    private @Enumerated(EnumType.STRING) Status status;
    private Float limitPrice;
    private String instrumentSymbol;
    private String instrumentSector;
    private String instrumentIndustry;
    private String traderName;
    private String traderGroup;
    private @Enumerated(EnumType.STRING) CohortType traderGroupType;
    private Float price;
    private Integer dateExecuted;
    private String instrumentName;
    
}