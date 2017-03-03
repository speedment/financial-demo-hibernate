package com.github.pyknic.financialdemo.controller.param;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class Sort {
    
    public enum Direction { ASC, DESC }
    
    private final String property;
    private final Direction direction;
   
    public Sort(String property, Direction direction) {
        this.property  = requireNonNull(property);
        this.direction = requireNonNull(direction);
    }

    public String getProperty() {
        return property;
    }

    public Direction getDirection() {
        return direction;
    }
}
