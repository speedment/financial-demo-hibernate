package com.github.pyknic.financialdemo.extra;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public enum CohortType {
    BOSTON,
    WASHINGTON,
    NEW_YORK,
    QUANT_MODELS,
    SAN_FRANSISCO;
    
    public static CohortType fromDatabase(String value) {
        if (value == null) return null; 
        else switch (value) {
            case "Boston"        : return BOSTON;
            case "Washington"    : return WASHINGTON;
            case "New York"      : return NEW_YORK;
            case "Quant Models"  : return QUANT_MODELS;
            case "San Francisco" : return SAN_FRANSISCO;
            default : throw new IllegalArgumentException(
                "Unknown CohortType constant '" + value + "'."
            );
        }
    }
}