package com.github.pyknic.financialdemo.repository.h2;

import com.github.pyknic.financialdemo.model.h2.OrderPojo;
import com.github.pyknic.financialdemo.repository.StreamSpecificationExecutor;
import org.springframework.data.repository.Repository;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface OrderRepository
extends Repository<OrderPojo, Long>, 
    StreamSpecificationExecutor<OrderPojo> {

    void save(OrderPojo order);
    
}