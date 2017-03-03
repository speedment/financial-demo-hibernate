package com.github.pyknic.financialdemo.controller.har;

import com.speedment.common.json.Json;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class HarTester {

    private final List<HarTest> tests;
    
    private HarTester(List<HarTest> tests) {
        this.tests = requireNonNull(tests);
    }
    
    public Stream<HarTest> stream() {
        return tests.stream();
    }
    
    @SuppressWarnings("unchecked")
    public static HarTester create(InputStream in) {
        requireNonNull(in, ".har-file not found.");
        
        final List<HarTest> tests = new LinkedList<>();
        
        try {
            final Map<String, Object> obj = (Map<String, Object>) Json.fromJson(in);
            final Map<String, Object> log = (Map<String, Object>) obj.get("log");
            final List<Map<String, Object>> entries = (List<Map<String, Object>>) log.get("entries");
            
            for (final Map<String, Object> entry : entries) {
                try {
                    final long time = (long) (double) (Double) entry.get("time");
                    final Map<String, Object> req = (Map<String, Object>) entry.get("request");

                    // Parse request
                    final RequestMethod method = RequestMethod.valueOf((String) req.get("method"));
                    final String fullPath = (String) req.get("url");
                    
                    if (fullPath.startsWith("https://piq.xh.io/assets/")
                    ||  fullPath.startsWith("https://piq.xh.io/hoistImpl/")) {
                        continue;
                    }
                    
                    final String path = fullPath.substring(0, fullPath.indexOf("?"))
                        .replace("https://piq.xh.io/proxy/speeder/", "/speeder/")
                        .replace("https://piq.xh.io/", "/speeder/");

                    final Map<String, String> headers = new HashMap<>();
                    final List<Map<String, String>> headerString = (List<Map<String, String>>) req.get("headers");
                    for (final Map<String, String> header : headerString) {
                        headers.put(header.get("name"), URLDecoder.decode(header.get("value"), "UTF-8"));
                    }

                    final Map<String, String> params = new HashMap<>();
                    final List<Map<String, String>> queryString = (List<Map<String, String>>) req.get("queryString");
                    for (final Map<String, String> param : queryString) {
                        params.put(param.get("name"), URLDecoder.decode(param.get("value"), "UTF-8"));
                    }

                    final HarRequest harReq = new HarRequest(path, method, new HashMap<>(), params, time);

                    // Parse Response
                    final Map<String, Object> res = (Map<String, Object>) entry.get("response");
                    final int status = (int) (long) (Long) res.get("status");

                    final Map<String, Object> content = (Map<String, Object>) res.get("content");
                    final MediaType mediaType = MediaType.valueOf((String) content.get("mimeType"));
                    final String text = (String) content.get("text");

                    final HarResponse harRes = new HarResponse(status, mediaType, text);

                    tests.add(new HarTest(harReq, harRes));
                } catch (final Exception ex) {
                    System.err.println("Could not parse test " + Json.toJson(entry));
                    throw new RuntimeException(ex);
                }
            }
            
            in.close();
            
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }

        return new HarTester(tests);
    }
}