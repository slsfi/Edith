package com.mysema.edith.guice;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CharsetResponseFilter implements ContainerResponseFilter {

    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        MediaType contentType = response.getMediaType();
        if (contentType != null) {
            response.getHttpHeaders().putSingle("Content-Type", contentType.toString() + ";charset=UTF-8");    
        }        
        return response;
    }
}