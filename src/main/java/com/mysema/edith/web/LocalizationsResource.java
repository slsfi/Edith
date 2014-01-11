package com.mysema.edith.web;

import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.Maps;

@Path("/localizations.js")
@Produces(MediaType.APPLICATION_JSON)
public class LocalizationsResource {
    
    @GET
    public Map<String, String> get(@Context HttpServletRequest req) {
        ResourceBundle bundle = ResourceBundle.getBundle("app", req.getLocale());
        Map<String, String> result = Maps.newHashMap();
        for (String key : bundle.keySet()) {
            result.put(key, bundle.getString(key));
        }
        return result;
    }
}
