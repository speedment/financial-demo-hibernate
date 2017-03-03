package com.github.pyknic.financialdemo;

import com.github.pyknic.financialdemo.extra.BuySell;
import com.github.pyknic.financialdemo.extra.CohortType;
import com.github.pyknic.financialdemo.extra.Status;
import com.github.pyknic.financialdemo.model.h2.OrderPojo;
import com.github.pyknic.financialdemo.model.h2.PriceStorePojo;
import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import com.github.pyknic.financialdemo.repository.h2.OrderRepository;
import com.github.pyknic.financialdemo.repository.h2.PriceStoreRepository;
import com.github.pyknic.financialdemo.repository.h2.RawPositionRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@SpringBootApplication
public class MainApp {
	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);
	}
    
    @Bean
	public CommandLineRunner loadOrders(
            OrderRepository repository,
            @Qualifier("mysql-datasource") DataSource ds) {
		return (args) -> {
			try (final Connection conn = ds.getConnection();
                final PreparedStatement ps = conn.prepareStatement(ORDERS_SQL)) {
                
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final OrderPojo pojo = new OrderPojo();
                        
                        int i = 1;
                        pojo.setId(rs.getLong(i++));
                        pojo.setDateCreated(rs.getInt(i++));
                        pojo.setDirection(BuySell.valueOf(rs.getString(i++)));
                        pojo.setQuantity(rs.getInt(i++));
                        pojo.setStatus(Status.valueOf(rs.getString(i++)));
                        pojo.setLimitPrice(rs.getFloat(i++));
                        pojo.setInstrumentSymbol(rs.getString(i++));
                        pojo.setInstrumentSector(rs.getString(i++));
                        pojo.setInstrumentIndustry(rs.getString(i++));
                        pojo.setInstrumentName(rs.getString(i++));
                        pojo.setTraderName(rs.getString(i++));
                        pojo.setTraderGroup(rs.getString(i++));
                        pojo.setTraderGroupType(CohortType.fromDatabase(rs.getString(i++)));
                        pojo.setPrice(rs.getFloat(i++));
                        pojo.setDateExecuted(rs.getInt(i++));
                        
                        repository.save(pojo);
                    }
                }
            }
		};
	}
    
    @Bean
	public CommandLineRunner loadPriceStore(
            PriceStoreRepository repository,
            @Qualifier("mysql-datasource") DataSource ds) {
		return (args) -> {
			try (final Connection conn = ds.getConnection();
                final PreparedStatement ps = conn.prepareStatement(PRICE_STORE_SQL)) {
                
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final PriceStorePojo pojo = new PriceStorePojo();
                        
                        int i = 1;
                        pojo.setId(rs.getLong(i++));
                        pojo.setValueDate(rs.getInt(i++));
                        pojo.setOpen(rs.getFloat(i++));
                        pojo.setHigh(rs.getFloat(i++));
                        pojo.setLow(rs.getFloat(i++));
                        pojo.setClose(rs.getFloat(i++));
                        pojo.setInstrumentSymbol(rs.getString(i++));
                        
                        repository.save(pojo);
                    }
                }
            }
		};
	}
    
    @Bean
	public CommandLineRunner loadRawPositions(
            RawPositionRepository repository,
            @Qualifier("mysql-datasource") DataSource ds) {
		return (args) -> {
			try (final Connection conn = ds.getConnection();
                final PreparedStatement ps = conn.prepareStatement(RAW_POSITION_SQL)) {
                
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final RawPositionPojo pojo = new RawPositionPojo();
                        
                        int i = 1;
                        pojo.setId(rs.getLong(i++));
                        pojo.setPnl(rs.getFloat(i++));
                        pojo.setInitiateTradingMktVal(rs.getFloat(i++));
                        pojo.setLiquidateTradingMktVal(rs.getFloat(i++));
                        pojo.setValueDate(rs.getInt(i++));
                        pojo.setTraderName(rs.getString(i++));
                        pojo.setTraderGroup(rs.getString(i++));
                        pojo.setTraderGroupType(rs.getString(i++));
                        pojo.setInstrumentName(rs.getString(i++));
                        pojo.setInstrumentSymbol(rs.getString(i++));
                        pojo.setInstrumentSector(rs.getString(i++));
                        pojo.setInstrumentIndustry(rs.getString(i++));
                        
                        repository.save(pojo);
                    }
                }
            }
		};
	}
    
    private final static String ORDERS_SQL =
        "SELECT `orders`.`id`,\n" +
        "       `orders`.`date_created_int`,\n" +
        "       `orders`.`direction`,\n" +
        "       `orders`.`quantity`,\n" +
        "       `orders`.`status`,\n" +
        "       `orders`.`limit_price`,\n" +
        "       `instrument`.`symbol`,\n" +
        "       `instrument`.`sector`,\n" +
        "       `instrument`.`industry`,\n" +
        "       `instrument`.`name`,\n" +
        "       `trader`.`name`,\n" +
        "       `cohort`.`name`,\n" +
        "       `cohort`.`cohort_type`,\n" +
        "       `execution`.`price`,\n" +
        "       `execution`.`transact_time_int`\n" +
        "FROM `orders`\n" +
        "INNER JOIN `instrument`\n" +
        "    ON `orders`.`instrument_id` = `instrument`.`id`\n" +
        "INNER JOIN `trader`\n" +
        "    ON `orders`.`trader_id` = `trader`.`id`\n" +
        "INNER JOIN `cohort`\n" +
        "    ON `trader`.`cohort_id` = `cohort`.`id`\n" +
        "INNER JOIN `execution`\n" +
        "    ON `orders`.`execution_id` = `execution`.`id`;";
    
    private final static String PRICE_STORE_SQL =
        "SELECT `price_store`.`id`,\n" +
        "       `price_store`.`value_date`,\n" +
        "       `price_store`.`open`,\n" +
        "       `price_store`.`high`,\n" +
        "       `price_store`.`low`,\n" +
        "       `price_store`.`close`,\n" +
        "       `instrument`.`symbol`\n" +
        "FROM `price_store`\n" +
        "INNER JOIN `instrument`\n" +
        "    ON `price_store`.`instrument_id` = `instrument`.`id`;";
    
    private final static String RAW_POSITION_SQL =
        "SELECT `dpp`.`id`,\n" +
        "       `dpp`.`pnl`,\n" +
        "       `dpp`.`total_initiate_mkt_val`,\n" +
        "       `dpp`.`total_liquidate_mkt_val`,\n" +
        "       `dpp`.`value_date_int`,\n" +
        "       `trader`.`name`,\n" +
        "       `cohort`.`name`,\n" +
        "       `cohort`.`cohort_type`,\n" +
        "       `instrument`.`name`,\n" +
        "       `instrument`.`symbol`,\n" +
        "       `instrument`.`sector`,\n" +
        "       `instrument`.`industry`\n" +
        "FROM `daily_position_performance` AS `dpp`\n" +
        "INNER JOIN `position_identifier` \n" +
        "    ON `dpp`.`position_identifier_id` = `position_identifier`.`id`\n" +
        "INNER JOIN `trader`\n" +
        "    ON `position_identifier`.`trader_id` = `trader`.`id`\n" +
        "INNER JOIN `cohort`\n" +
        "    ON `trader`.`cohort_id` = `cohort`.`id`\n" +
        "INNER JOIN `instrument`\n" +
        "    ON `position_identifier`.`instrument_id` = `instrument`.`id`;";
}