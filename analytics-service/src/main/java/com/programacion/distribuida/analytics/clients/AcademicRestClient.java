package com.programacion.distribuida.analytics.clients;

import com.programacion.distribuida.analytics.dtos.GradeReportDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "academic-api")
public interface AcademicRestClient {

    @GET
    @Path("/grades")
    List<GradeReportDto> findGrades();

    default List<GradeReportDto> findGradesFallback() {
        return List.of();
    }
}
