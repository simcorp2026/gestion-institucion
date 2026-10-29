package com.programacion.distribuida.analytics;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/health")
public class HealthResource {

    @GET
    public String health() {
        return "analytics-service-up";
    }
}
