/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.guice;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.servlet.AbstractFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoCacheFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(NoCacheFilter.class);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.debug("noCacheFilter.doFilter {}", ((HttpServletRequest)request).getRequestURI());
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Cache-Control", "max-age=0");
        chain.doFilter(request, response);
    }

}
