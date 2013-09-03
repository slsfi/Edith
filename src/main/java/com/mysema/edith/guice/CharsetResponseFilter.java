package com.mysema.edith.guice;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CharsetResponseFilter implements ContainerResponseFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(CharsetResponseFilter.class);

    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        logger.debug("charsetResponseFilter.doFilter {}", request.getAbsolutePath().toString());
        MediaType contentType = response.getMediaType();
        if (contentType != null) {
            response.getHttpHeaders().putSingle("Content-Type", contentType.toString() + ";charset=UTF-8");    
        }        
        return response;
    }
}