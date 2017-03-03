package com.github.pyknic.financialdemo.controller.har;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class HarTest {

    private final HarRequest request;
    private final HarResponse response;

    public HarTest(HarRequest request, HarResponse response) {
        this.request  = request;
        this.response = response;
    }

    public HarRequest getRequest() {
        return request;
    }

    public HarResponse getResponse() {
        return response;
    }
    
    public void execute(MockMvc mvc) {
        try {
            final MockHttpServletRequestBuilder builder;
            switch (request.getMethod()) {
                case POST    : builder = MockMvcRequestBuilders.post(request.getPath());    break;
                case GET     : builder = MockMvcRequestBuilders.get(request.getPath());     break;
                case PUT     : builder = MockMvcRequestBuilders.put(request.getPath());     break;
                case DELETE  : builder = MockMvcRequestBuilders.delete(request.getPath());  break;
                case OPTIONS : builder = MockMvcRequestBuilders.options(request.getPath()); break;
                default : throw new UnsupportedOperationException(
                    "Unknown request method '" + request.getMethod() + "'."
                );
            }

            request.getParams().forEach((param, value) -> {
                builder.param(param, value);
            });

            request.getHeaders().forEach((header, value) -> {
                builder.header(header, value);
            });

            builder.accept(response.getMediaType());

            mvc.perform(builder)
                .andExpect(status().is(response.getStatus()))
                .andExpect(content().contentTypeCompatibleWith(response.getMediaType()));
//                .andExpect(content().json(response.getContent()));   // Requires databases to be identical
        } catch (final Exception ex) {
            System.err.println(
                "Error in request '" + request.getMethod() + 
                ": " + request.getPath() + "'."
            );
            
            throw new RuntimeException(ex);
        }
    }
}