package com.github.pyknic.financialdemo.controller;

import com.github.pyknic.financialdemo.controller.har.HarTester;
import com.github.pyknic.financialdemo.repository.h2.PriceStoreRepository;
import com.google.gson.Gson;
import java.net.URISyntaxException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Emil Forslund
 * @since  1.1.8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
public class PricesControllerTest {

    private MockMvc mockMvc;
    private HarTester tester;
    
    private @Autowired Gson gson;
    private @Autowired PriceStoreRepository manager;

    @Before
    public void loadFromDatabase() throws URISyntaxException {
        mockMvc = MockMvcBuilders.standaloneSetup(
            new PricesController(gson, manager)
        ).build();
        
        tester = HarTester.create(getClass().getResourceAsStream("/piq.xh.io.har"));
    }
    
    /**
     * Test of handleGet method, of class OrdersController.
     * 
     * @throws Exception  if something goes wrong
     */
    @Test
    public void testHandleGet() throws Exception {
        System.out.println("Testing /speeder/prices");
        tester.stream()
            .filter(test -> test.getRequest().getPath().startsWith("/speeder/prices"))
            .forEach(test -> {
                System.out.println("Testing " + test.getRequest().getMethod() + ": " + test.getRequest().getPath() + " " + test.getRequest().getParams());
                test.execute(mockMvc);
            });
    }
    
    /**
     * Relates to Speedment Enterprise Issue #39: Equal Predicate Not Applied
     * 
     * @throws Exception  if something goes wrong
     */
    @Test
    public void testIsEqualApplied() throws Exception {
        mockMvc.perform(get("/speeder/prices")
            .param("_dc", "1486072762247")
            .param("limit", "5000")
            .param("filter", "[{\"property\":\"instrumentSymbol\",\"operator\":\"eq\",\"value\":\"SPY\"},{\"property\":\"valueDate\",\"operator\":\"ge\",\"value\":\"20140101\"},{\"property\":\"valueDate\",\"operator\":\"le\",\"value\":\"20160930\"}]")
        ).andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data[*].instrumentSymbol", everyItem(equalTo("SPY"))))
            .andExpect(jsonPath("$.data[*].valueDate", everyItem(greaterThanOrEqualTo(20140101))))
            .andExpect(jsonPath("$.data[*].valueDate", everyItem(lessThanOrEqualTo(20160930))));
    }
}