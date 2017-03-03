package com.github.pyknic.financialdemo.controller;

import com.github.pyknic.financialdemo.common.SizeCache;
import com.github.pyknic.financialdemo.controller.param.Filter;
import com.github.pyknic.financialdemo.controller.param.FilterList;
import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import com.github.pyknic.financialdemo.repository.h2.RawPositionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
public class RawPositionsController {

    private final Gson gson;
    private final RawPositionRepository manager;
    private final SizeCache sizeCache;
    
    @Autowired
    RawPositionsController(Gson gson, RawPositionRepository manager) {
        this.gson      = requireNonNull(gson);
        this.manager   = requireNonNull(manager);
        this.sizeCache = new SizeCache();
    }
    
    @RequestMapping(value = "/speeder/rawpositions", method = GET, produces = APPLICATION_JSON_VALUE)
    public RawPositionTotalResult handleGet(
            @RequestParam(name="callback", required=false) String callback,
            @RequestParam(name="start", required=false) Long start,
            @RequestParam(name="limit", required=false) Long limit,
            @RequestParam(name="filter", required=false) String sFilters,
            @RequestParam(name="sort", required=false) String sSorts,
            HttpServletResponse response
    ) throws ParseException {
        
        final FilterList<RawPositionPojo> filters;
        if (sFilters == null || "[]".equals(sFilters)) {
            filters = new FilterList<>();
        } else {
            filters = new FilterList<>(
                gson.fromJson(sFilters, 
                    new TypeToken<List<Filter<RawPositionPojo>>>(){}.getType())
            );
        }
        
//        final List<Sort> sorts;
//        if (sSorts == null || "[]".equals(sSorts)) {
//            sorts = Collections.emptyList();
//        } else {
//            sorts = gson.fromJson(sSorts, new TypeToken<List<Sort>>(){}.getType());
//        }
//        
//        if (sorts != null && !sorts.isEmpty()) {
//            final Optional<Comparator<RawPosition>> comparator = sorts.stream()
//                .map(RawPositionsController::sortToComparator)
//                .reduce(Comparator::thenComparing);
//            
//            if (comparator.isPresent()) {
//                stream = stream.sorted(comparator.get());
//            }
//        }
        
        try (Stream<RawPositionPojo> stream = manager.findAll(filters);
             Stream<RawPositionPojo> totalStream = manager.findAll(filters)) {

            if (start != null && start > 0) {
                stream.skip(start);
            }

            if (limit != null) {
                stream.limit(limit);
            } else {
                stream.limit(100);
            }

            final long totalCount = sizeCache.computeIfAbsent(
                sFilters, totalStream::count
            );

            try {
                return RawPositionTotalResult.from(stream, totalCount);
            } finally {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Request-Method", "*");
                response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type");
            }
        }
    }
    
    public final static class RawPositionTotalResult {
        
        private final Collection<RawPositionResult> data;
        private final long total;
        
        static RawPositionTotalResult from(Stream<RawPositionPojo> stream, long total) {
            return new RawPositionTotalResult(
                stream.map(RawPositionResult::from).collect(toList()),
                total
            );
        }
        
        private RawPositionTotalResult(Collection<RawPositionResult> data, long total) {
            this.data  = data;
            this.total = total;
        }

        public Collection<RawPositionResult> getData() {
            return data;
        }

        public long getTotal() {
            return total;
        }
    }
    
    public final static class RawPositionResult {
        
        private final long id;
        private final double pnl;
        private final double initiateTradingMktVal;
        private final double liquidateTradingMktVal;
        private final int valueDate;
        private final String traderName;
        private final String traderGroup;
        private final String traderGroupType;
        private final String instrumentName;
        private final String instrumentSymbol;
        private final String instrumentSector;
        private final String instrumentIndustry;
        
        public static RawPositionResult from(RawPositionPojo pos) {
            return new RawPositionResult(
                pos.getId(),
                pos.getPnl(),
                pos.getInitiateTradingMktVal(),
                pos.getLiquidateTradingMktVal(),
                pos.getValueDate(),
                pos.getTraderName(),
                pos.getTraderGroup(),
                pos.getTraderGroupType(),
                pos.getInstrumentName(),
                pos.getInstrumentSymbol(),
                pos.getInstrumentSector(),
                pos.getInstrumentIndustry()
            );
        }

        public RawPositionResult(
                long id, 
                double pnl, 
                double initiateTradingMktVal, 
                double liquidateTradingMktVal, 
                int valueDate, 
                String traderName, 
                String traderGroup, 
                String traderGroupType, 
                String instrumentName, 
                String instrumentSymbol, 
                String instrumentSector, 
                String instrumentIndustry) {
            
            this.id                     = id;
            this.pnl                    = pnl;
            this.initiateTradingMktVal  = initiateTradingMktVal;
            this.liquidateTradingMktVal = liquidateTradingMktVal;
            this.valueDate              = valueDate;
            this.traderName             = traderName;
            this.traderGroup            = traderGroup;
            this.traderGroupType        = traderGroupType;
            this.instrumentName         = instrumentName;
            this.instrumentSymbol       = instrumentSymbol;
            this.instrumentSector       = instrumentSector;
            this.instrumentIndustry     = instrumentIndustry;
        }

        public long getId() {
            return id;
        }

        public double getPnl() {
            return pnl;
        }

        public double getInitiateTradingMktVal() {
            return initiateTradingMktVal;
        }

        public double getLiquidateTradingMktVal() {
            return liquidateTradingMktVal;
        }

        public int getValueDate() {
            return valueDate;
        }

        public String getTraderName() {
            return traderName;
        }

        public String getTraderGroup() {
            return traderGroup;
        }

        public String getTraderGroupType() {
            return traderGroupType;
        }

        public String getInstrumentName() {
            return instrumentName;
        }

        public String getInstrumentSymbol() {
            return instrumentSymbol;
        }

        public String getInstrumentSector() {
            return instrumentSector;
        }

        public String getInstrumentIndustry() {
            return instrumentIndustry;
        }
    }
}