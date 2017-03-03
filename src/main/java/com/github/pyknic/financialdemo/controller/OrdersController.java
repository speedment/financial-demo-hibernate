package com.github.pyknic.financialdemo.controller;

import com.github.pyknic.financialdemo.common.DateIntToShortMapper;
import com.github.pyknic.financialdemo.common.SizeCache;
import com.github.pyknic.financialdemo.controller.param.Filter;
import com.github.pyknic.financialdemo.controller.param.FilterList;
import com.github.pyknic.financialdemo.extra.BuySell;
import com.github.pyknic.financialdemo.extra.OrderType;
import com.github.pyknic.financialdemo.extra.Status;
import com.github.pyknic.financialdemo.model.h2.OrderPojo;
import com.github.pyknic.financialdemo.repository.h2.OrderRepository;
import com.github.pyknic.financialdemo.util.TimeUtil;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
public final class OrdersController {
    
    private final static DateIntToShortMapper DATE_INT_TO_SHORT =
        new DateIntToShortMapper();
    
    private final Gson gson;
    private final OrderRepository manager;
    private final SizeCache sizeCache;
    
    @Autowired
    OrdersController(Gson gson, OrderRepository manager) {
        this.gson      = requireNonNull(gson);
        this.manager   = requireNonNull(manager);
        this.sizeCache = new SizeCache();
    }
    
    @GetMapping(path = "/speeder/orders", produces = APPLICATION_JSON_VALUE)
    public OrderTotalResult handleGet(
            @RequestParam(name="callback", required=false) String callback,
            @RequestParam(name="start", required=false) Long start,
            @RequestParam(name="limit", required=false) Long limit,
            @RequestParam(name="filter", required=false) String sFilters,
            @RequestParam(name="sort", required=false) String sSorts,
            HttpServletResponse response
    ) throws ParseException, NumberFormatException {
        
        final FilterList<OrderPojo> filters;
        if (sFilters == null || "[]".equals(sFilters)) {
            filters = new FilterList<>();
        } else {
            filters = new FilterList<>(
                gson.fromJson(sFilters, 
                    new TypeToken<List<Filter<OrderPojo>>>(){}.getType())
            );
        }
        
//        final List<Sort> sorts;
//        if (sSorts == null || "[]".equals(sSorts)) {
//            sorts = Collections.emptyList();
//        } else {
//            sorts = gson.fromJson(sSorts, 
//                new TypeToken<List<Sort>>(){}.getType());
//        }
//        if (sorts != null && !sorts.isEmpty()) {
//            final Optional<Comparator<Order>> comparator = sorts.stream()
//                .map(OrdersController::sortToComparator)
//                .reduce(Comparator::thenComparing);
//            
//            if (comparator.isPresent()) {
//                stream = stream.sorted(comparator.get());
//            }
//        }
        
        try (Stream<OrderPojo> stream      = manager.findAll(filters);
             Stream<OrderPojo> totalStream = manager.findAll(filters)) {
        
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
                return OrderTotalResult.from(stream, totalCount);
            } finally {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Request-Method", "*");
                response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type");
            }
        }
    }

    public final static class OrderTotalResult {
        
        private final Collection<OrderResult> data;
        private final long total;
        
        static OrderTotalResult from(Stream<OrderPojo> stream, long total) {
            return new OrderTotalResult(
                stream.map(OrderResult::from).collect(toList()),
                total
            );
        }

        private OrderTotalResult(Collection<OrderResult> data, long total) {
            this.data  = data;
            this.total = total;
        }

        public Collection<OrderResult> getData() {
            return data;
        }

        public long getTotal() {
            return total;
        }
    }
    
    public final static class OrderResult {
        
        private final long id;
        private final String valueDate;
        private final int dateCreated;
        private final Integer dateExecuted;
        private final BuySell direction; 
        private final String instrumentName;
        private final String instrumentSymbol;
        private final String instrumentIndustry;
        private final String instrumentSector;
        private final OrderType orderType;
        private final Status status;
        private final String traderName;
        private final float execPrice;
        private final Float limitPrice;
        private final int quantity;
        
        static OrderResult from(OrderPojo original) {
            String valueDate;
            try {
                valueDate = TimeUtil.fromEpochSecs(original.getDateCreated());
            } catch (final Throwable thrw) {
                System.err.println("Error in /orders! Could not parse date " + 
                    "created '" + original.getDateCreated() + "' into string."
                );
                valueDate = null;
            }
            
            return new OrderResult(
                original.getId(),
                valueDate,
                original.getDateCreated(),
                original.getDateExecuted(),
                original.getDirection(),
                original.getInstrumentName(),
                original.getInstrumentSymbol(),
                original.getInstrumentIndustry(),
                original.getInstrumentSector(),
                original.getOrderType(),
                original.getStatus(),
                original.getTraderName(),
                original.getPrice(),
                original.getLimitPrice(),
                original.getQuantity()
            );
        }

        public OrderResult(
                long id, 
                String valueDate, 
                int dateCreated, 
                Integer dateExecuted, 
                BuySell direction, 
                String instrumentName,
                String instrumentSymbol,
                String instrumentIndustry,
                String instrumentSector,
                OrderType orderType, 
                Status status, 
                String traderName,
                float price,
                Float limitPrice,
                int quantity) {
            
            this.id                 = id;
            this.valueDate          = valueDate;
            this.dateCreated        = dateCreated;
            this.dateExecuted       = dateExecuted;
            this.direction          = direction;
            this.instrumentName     = instrumentName;
            this.instrumentSymbol   = instrumentSymbol;
            this.instrumentIndustry = instrumentIndustry;
            this.instrumentSector   = instrumentSector;
            this.orderType          = orderType;
            this.status             = status;
            this.traderName         = traderName;
            this.execPrice          = price;
            this.limitPrice         = limitPrice;
            this.quantity           = quantity;
        }

        public long getId() {
            return id;
        }

        public String getValueDate() {
            return valueDate;
        }

        public int getDateCreated() {
            return dateCreated;
        }

        public Integer getDateExecuted() {
            return dateExecuted;
        }

        public BuySell getDirection() {
            return direction;
        }

        public String getInstrumentName() {
            return instrumentName;
        }

        public String getInstrumentSymbol() {
            return instrumentSymbol;
        }

        public String getInstrumentIndustry() {
            return instrumentIndustry;
        }

        public String getInstrumentSector() {
            return instrumentSector;
        }

        public OrderType getOrderType() {
            return orderType;
        }

        public Status getStatus() {
            return status;
        }

        public String getTraderName() {
            return traderName;
        }

        public float getExecPrice() {
            return execPrice;
        }

        public Float getLimitPrice() {
            return limitPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}