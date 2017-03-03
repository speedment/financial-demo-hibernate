package com.github.pyknic.financialdemo.controller.har;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class HarRequest {

    private final String path;
    private final RequestMethod method;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final long time;

    public HarRequest(String path, RequestMethod method, Map<String, String> headers, Map<String, String> params, long time) {
        this.path    = path;
        this.method  = method;
        this.headers = headers;
        this.params  = params;
        this.time    = time;
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public long getTime() {
        return time;
    }
    
}