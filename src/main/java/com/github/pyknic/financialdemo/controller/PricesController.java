package com.github.pyknic.financialdemo.controller;

import com.github.pyknic.financialdemo.common.SizeCache;
import com.github.pyknic.financialdemo.controller.param.Filter;
import com.github.pyknic.financialdemo.controller.param.FilterList;
import com.github.pyknic.financialdemo.model.h2.PriceStorePojo;
import com.github.pyknic.financialdemo.repository.h2.PriceStoreRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
public final class PricesController {

    private final SizeCache sizeCache;
    
    private final Gson gson;
    private final PriceStoreRepository manager;
    
    @Autowired
    PricesController(Gson gson, PriceStoreRepository manager) {
        this.gson      = requireNonNull(gson);
        this.manager   = requireNonNull(manager);
        this.sizeCache = new SizeCache();
    }
    
    @RequestMapping(value = "/speeder/prices", method = GET, produces = APPLICATION_JSON_VALUE)
    public PriceTotalResult handleGet(
            @RequestParam(name="callback", required=false) String callback,
            @RequestParam(name="start", required=false) Long start,
            @RequestParam(name="limit", required=false) Long limit,
            @RequestParam(name="filter", required=false) String sFilters,
            @RequestParam(name="sort", required=false) String sSorts,
            HttpServletResponse response) {
        
        final FilterList<PriceStorePojo> filters;
        if (sFilters == null || "[]".equals(sFilters)) {
            filters = new FilterList<>();
        } else {
            filters = new FilterList<>(
                gson.fromJson(sFilters, 
                    new TypeToken<List<Filter<PriceStorePojo>>>(){}.getType())
            );
        }
        
//        final List<Sort> sorts;
//        if (sSorts == null || "[]".equals(sSorts)) {
//            sorts = Collections.emptyList();
//        } else {
//            sorts = gson.fromJson(sSorts, new TypeToken<List<Sort>>(){}.getType());
//        }
//        if (sorts != null && !sorts.isEmpty()) {
//            final Optional<Comparator<PriceStore>> comparator = sorts.stream()
//                .map(PricesController::sortToComparator)
//                .reduce(Comparator::thenComparing);
//            
//            if (comparator.isPresent()) {
//                stream = stream.sorted(comparator.get());
//            }
//        }
        
        try (Stream<PriceStorePojo> stream = manager.findAll(filters);
             Stream<PriceStorePojo> totalStream = manager.findAll(filters)) {

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
                return PriceTotalResult.from(stream, totalCount);
            } finally {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Request-Method", "*");
                response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type");
            }
        }
    }
    
    public final static class PriceTotalResult {
        
        private final Collection<PriceResult> data;
        private final long total;
        
        static PriceTotalResult from(Stream<PriceStorePojo> stream, long total) {
            return new PriceTotalResult(
                stream.map(PriceResult::from).collect(toList()),
                total
            );
        }

        private PriceTotalResult(Collection<PriceResult> data, long total) {
            this.data  = data;
            this.total = total;
        }

        public Collection<PriceResult> getData() {
            return data;
        }

        public long getTotal() {
            return total;
        }
    }
    
    public final static class PriceResult {
        
        private final long id;
        private final int valueDate;
        private final float open;
        private final Float close;
        private final float high;
        private final float low;
        private final String instrumentSymbol;
        
        static PriceResult from(PriceStorePojo original) {
            return new PriceResult(
                original.getId(),
                original.getValueDate(),
                original.getOpen(),
                original.getClose(),
                original.getHigh(),
                original.getLow(),
                original.getInstrumentSymbol()
            );
        }

        public long getId() {
            return id;
        }

        public long getValueDate() {
            return valueDate;
        }

        public float getOpen() {
            return open;
        }

        public Float getClose() {
            return close;
        }

        public float getHigh() {
            return high;
        }

        public float getLow() {
            return low;
        }

        public String getInstrumentSymbol() {
            return instrumentSymbol;
        }

        private PriceResult(
                long id, 
                int valueDate, 
                float open, 
                Float close, 
                float high, 
                float low, 
                String instrumentSymbol) {
            
            this.id        = id;
            this.valueDate = valueDate;
            this.open      = open;
            this.close     = close;
            this.high      = high;
            this.low       = low;
            this.instrumentSymbol = instrumentSymbol;
        }
    }
}