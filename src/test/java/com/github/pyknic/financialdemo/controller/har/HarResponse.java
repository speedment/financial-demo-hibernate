package com.github.pyknic.financialdemo.controller.har;

import org.springframework.http.MediaType;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class HarResponse {
    
    private final int status;
    private final MediaType mediaType;
    private final String content;

    public HarResponse(int status, MediaType mediaType, String content) {
        this.status    = status;
        this.mediaType = mediaType;
        this.content   = content;
    }

    public int getStatus() {
        return status;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getContent() {
        return content;
    }
    
}