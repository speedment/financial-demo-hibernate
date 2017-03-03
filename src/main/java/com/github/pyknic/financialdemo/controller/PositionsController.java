package com.github.pyknic.financialdemo.controller;

import com.github.pyknic.financialdemo.aggregator.PositionResult;
import com.github.pyknic.financialdemo.aggregator.RawPositionToConcurrentMap;
import com.github.pyknic.financialdemo.common.Stopwatch;
import com.github.pyknic.financialdemo.controller.param.BetweenFilter;
import com.github.pyknic.financialdemo.controller.param.Filter;
import com.github.pyknic.financialdemo.controller.param.FilterList;
import com.github.pyknic.financialdemo.model.h2.RawPositionPojo;
import com.github.pyknic.financialdemo.repository.h2.RawPositionRepository;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.function.Function;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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
public class PositionsController {
    
    private final static String SEPARATOR  = ">>";
    
    private final RawPositionRepository rawPositions;
    
    @Autowired
    PositionsController(RawPositionRepository rawPositions) {
        this.rawPositions = requireNonNull(rawPositions);
    }

    @RequestMapping(
        value    = "/speeder/positions", 
        method   = GET, 
        produces = APPLICATION_JSON_VALUE
    ) public Collection<PositionResult> handleGet(
            @RequestParam(name="callback", required=false) String callback,
            @RequestParam(name="startDate") Integer iFrom, 
            @RequestParam(name="endDate") Integer iTo,
            @RequestParam(name="drillDownPath") String aGroups,
            @RequestParam(name="drillDownKey", required=false) String aKeys,
            HttpServletResponse response
    ) throws ParseException, NumberFormatException {
        
        final Stopwatch sw = Stopwatch.createStarted();
        final String[] groups = aGroups.split(SEPARATOR);
        
        final List<Specification<RawPositionPojo>> filters = new LinkedList<>();
        filters.add(new BetweenFilter<>(iFrom, iTo));

        final Function<RawPositionPojo, Object> classifier;
        final int usedGroups;
        
        if (aKeys == null || "root".equals(aKeys)) {
            classifier = classifier(groups[0]);
            usedGroups = 1;
        } else {
            final String[] keys = aKeys.split(SEPARATOR);
            usedGroups = Math.min(groups.length, keys.length + 1);
            
            for (int i = 0; i < keys.length; i++) {
                filters.add(filter(groups[i], keys[i]));
            }
            
            if (groups.length > keys.length) {
                classifier = classifier(groups[keys.length]);
            } else {
                classifier = null;
            }
        }

        final Function<RawPositionPojo, String> identifier =
            identifier(groups, usedGroups);

        try (final Stream<RawPositionPojo> positions = rawPositions
                .findAll(new FilterList<>(filters)).parallel()) {
            
            if (classifier == null) {
                return positions
                    .map(pos -> new PositionResult(identifier).aggregate(pos))
                    .collect(toList());
                
            } else {
                return positions.collect(new RawPositionToConcurrentMap<>(
                    classifier,
                    identifier
                )).values();
            }
        } finally {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Request-Method", "*");
            response.setHeader(
                "Access-Control-Allow-Headers", 
                "X-Requested-With,Content-Type"
            );
            System.out.println("Finished in: " + sw.stop());
        }
    }
    
    private static Function<RawPositionPojo, String> identifier(
            String[] groups, int limit) {
        
        switch (limit) {
            case 0 : return pos -> "";
            
            case 1 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                return pos -> f0.apply(pos).toString();
            }
            case 2 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                final Function<RawPositionPojo, Object> f1 = classifier(groups[1]);
                return pos -> f0.apply(pos) + SEPARATOR + f1.apply(pos);
            }
            case 3 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                final Function<RawPositionPojo, Object> f1 = classifier(groups[1]);
                final Function<RawPositionPojo, Object> f2 = classifier(groups[2]);
                
                return pos -> f0.apply(pos) + SEPARATOR + 
                              f1.apply(pos) + SEPARATOR + 
                              f2.apply(pos);
            }
            case 4 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                final Function<RawPositionPojo, Object> f1 = classifier(groups[1]);
                final Function<RawPositionPojo, Object> f2 = classifier(groups[2]);
                final Function<RawPositionPojo, Object> f3 = classifier(groups[3]);
                
                return pos -> f0.apply(pos) + SEPARATOR + 
                              f1.apply(pos) + SEPARATOR + 
                              f2.apply(pos) + SEPARATOR + 
                              f3.apply(pos);
            }
            case 5 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                final Function<RawPositionPojo, Object> f1 = classifier(groups[1]);
                final Function<RawPositionPojo, Object> f2 = classifier(groups[2]);
                final Function<RawPositionPojo, Object> f3 = classifier(groups[3]);
                final Function<RawPositionPojo, Object> f4 = classifier(groups[4]);
                
                return pos -> f0.apply(pos) + SEPARATOR + 
                              f1.apply(pos) + SEPARATOR + 
                              f2.apply(pos) + SEPARATOR + 
                              f3.apply(pos) + SEPARATOR + 
                              f4.apply(pos);
            }
            case 6 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                final Function<RawPositionPojo, Object> f1 = classifier(groups[1]);
                final Function<RawPositionPojo, Object> f2 = classifier(groups[2]);
                final Function<RawPositionPojo, Object> f3 = classifier(groups[3]);
                final Function<RawPositionPojo, Object> f4 = classifier(groups[4]);
                final Function<RawPositionPojo, Object> f5 = classifier(groups[5]);
                
                return pos -> f0.apply(pos) + SEPARATOR + 
                              f1.apply(pos) + SEPARATOR + 
                              f2.apply(pos) + SEPARATOR + 
                              f3.apply(pos) + SEPARATOR + 
                              f4.apply(pos) + SEPARATOR + 
                              f5.apply(pos);
            }
            case 7 : {
                final Function<RawPositionPojo, Object> f0 = classifier(groups[0]);
                final Function<RawPositionPojo, Object> f1 = classifier(groups[1]);
                final Function<RawPositionPojo, Object> f2 = classifier(groups[2]);
                final Function<RawPositionPojo, Object> f3 = classifier(groups[3]);
                final Function<RawPositionPojo, Object> f4 = classifier(groups[4]);
                final Function<RawPositionPojo, Object> f5 = classifier(groups[5]);
                final Function<RawPositionPojo, Object> f6 = classifier(groups[6]);
                
                return pos -> f0.apply(pos) + SEPARATOR + 
                              f1.apply(pos) + SEPARATOR + 
                              f2.apply(pos) + SEPARATOR + 
                              f3.apply(pos) + SEPARATOR + 
                              f4.apply(pos) + SEPARATOR + 
                              f5.apply(pos) + SEPARATOR + 
                              f6.apply(pos);
            }
            default : {
                final List<Function<RawPositionPojo, Object>> f = Stream.of(groups)
                    .limit(limit)
                    .map(PositionsController::classifier)
                    .collect(toList());
                
                return pos -> f.stream()
                    .map(c -> c.apply(pos))
                    .map(Object::toString)
                    .collect(joining(SEPARATOR));
            }
        }
    }
    
    private static Function<RawPositionPojo, Object> classifier(String group) {
        switch (group) {
            case "valueDate"          : return RawPositionPojo::getValueDate;
            case "traderName"         : return RawPositionPojo::getTraderName;
            case "traderGroup"        : return RawPositionPojo::getTraderGroup;
            case "traderGroupType"    : return RawPositionPojo::getTraderGroupType;
            case "instrumentName"     : return orEmpty(RawPositionPojo::getInstrumentName);
            case "instrumentSymbol"   : return RawPositionPojo::getInstrumentSymbol;
            case "instrumentSector"   : return orEmpty(RawPositionPojo::getInstrumentSector);
            case "instrumentIndustry" : return orEmpty(RawPositionPojo::getInstrumentIndustry);
            default : throw new IllegalArgumentException(
                "Unknown group '" + group + "'."
            );
        }
    }
    
    private static Filter<RawPositionPojo> filter(String group, String key) throws ParseException, NumberFormatException {
        return new Filter<>(group, Filter.Operator.EQUAL, key);
    }

    private static Function<RawPositionPojo, Object> orEmpty(Function<RawPositionPojo, Object> original) {
        return original.andThen(o -> o == null ? "" : o);
    }
}