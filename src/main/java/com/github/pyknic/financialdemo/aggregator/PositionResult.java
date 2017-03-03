package com.github.pyknic.financialdemo.aggregator;

import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import static java.util.Objects.requireNonNull;
import java.util.function.Function;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class PositionResult {

    private final Function<RawPositionPojo, String> identifier;
    
    private String id;
    private float initiateTradingMktValue;
    private float liquidateTradingMktValue;
    private float pnl;
    private String instrumentName;
    private int minDate;
    private int maxDate;
    
    private boolean instrumentNameSet = false;
    
    public PositionResult(
            Function<RawPositionPojo, String> identifier) {
        
        this.identifier    = requireNonNull(identifier);
    }

    public String getId() {
        return id;
    }

    public float getInitiateTradingMktValue() {
        return initiateTradingMktValue;
    }

    public float getLiquidateTradingMktValue() {
        return liquidateTradingMktValue;
    }

    public float getPnl() {
        return pnl;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public int getMinDate() {
        return minDate;
    }

    public int getMaxDate() {
        return maxDate;
    }

    public boolean isInstrumentNameSet() {
        return instrumentNameSet;
    }
    
    public PositionResult aggregate(RawPositionPojo rawPosition) {
        if (id == null) {
            id = identifier.apply(rawPosition);
        }
        
        initiateTradingMktValue  += rawPosition.getInitiateTradingMktVal();
        liquidateTradingMktValue += rawPosition.getLiquidateTradingMktVal();
        pnl                      += rawPosition.getPnl();
        
        final int date = rawPosition.getValueDate();
        minDate = Math.min(minDate, date);
        maxDate = Math.max(maxDate, date);
        
        final String name = rawPosition.getInstrumentName();
        if (instrumentNameSet) {
            if (instrumentName == null) {
                instrumentName = name;
            } else {
                if (name == null
                || !name.equals(instrumentName)) {
                    instrumentName = null;
                }
            }
        } else {
            instrumentName = name;
            instrumentNameSet = true;
        }
        
        return this;
    }
    
    public PositionResult aggregate(PositionResult result) {
        if (id == null) id = result.id;

        initiateTradingMktValue  += result.initiateTradingMktValue;
        liquidateTradingMktValue += result.liquidateTradingMktValue;
        pnl                      += result.pnl;
        minDate = Math.min(minDate, result.minDate);
        maxDate = Math.max(maxDate, result.maxDate);
        
        final String name = result.instrumentName;
        if (instrumentNameSet) {
            if (instrumentName == null) {
                instrumentName = name;
            } else {
                if (name == null
                || !name.equals(instrumentName)) {
                    instrumentName = null;
                }
            }
        } else {
            instrumentName = name;
            instrumentNameSet = true;
        }
        
        return this;
    }
}