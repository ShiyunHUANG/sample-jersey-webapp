package com.sample.jersey.app;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.resource.ResourceException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import com.stormpath.sdk.client.*;
import com.stormpath.sdk.application.Application;
import sun.java2d.pipe.hw.ContextCapabilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.*;
import java.io.*;

import java.util.Base64;

@Path("/api/weather/{city}")
public class WeatherApi {

    String applicationHref = "https://api.stormpath.com/v1/applications/5HRNSljrcYQax0oCQQovT9";
    @Context
    private HttpServletRequest servletRequest;
    @Context
    private HttpServletResponse servletResponse;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getWeatherApi(@PathParam("city") String myCity) throws Exception {
        StormpathClient stormpathClient = new StormpathClient();
        Client myClient = stormpathClient.getClient();

        Application application = myClient.getResource(applicationHref, Application.class);

        System.out.println(servletRequest.getHeader("Authorization"));

        try {
            ApiAuthenticationResult authenticationResult = application.authenticateApiRequest(servletRequest);
        } catch (ResourceException e) {
            System.out.println(e.getMessage());
            servletResponse.sendError(403);
            return "Cannot authenticate user.";
        }

        URL weatherURL = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + myCity);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        City city = null;

        try {
            InputStream in = weatherURL.openStream();
            city = mapper.readValue(in, City.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = city.toString();

        return result;
    }

}