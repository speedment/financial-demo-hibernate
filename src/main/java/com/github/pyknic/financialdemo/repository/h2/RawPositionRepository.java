package com.github.pyknic.financialdemo.repository.h2;

import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import com.github.pyknic.financialdemo.repository.StreamSpecificationExecutor;
import org.springframework.data.repository.Repository;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface RawPositionRepository 
extends Repository<RawPositionPojo, Long>, 
    StreamSpecificationExecutor<RawPositionPojo> {

    void save(RawPositionPojo rawPosition);
    
}