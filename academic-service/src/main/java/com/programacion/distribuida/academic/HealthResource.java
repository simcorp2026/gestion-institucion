package com.programacion.distribuida.academic;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/health")
public class HealthResource {

    @GET
    public String health() {
        return "academic-service-up";
    }
}
