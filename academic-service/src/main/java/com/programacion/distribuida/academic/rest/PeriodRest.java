package com.programacion.distribuida.academic.rest;

import com.programacion.distribuida.academic.db.AcademicPeriod;
import com.programacion.distribuida.academic.repo.AcademicPeriodRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.List;

@Path("/periods")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class PeriodRest {

    final AcademicPeriodRepository periodRepository;

    @Inject
    public PeriodRest(AcademicPeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    @GET
    public List<AcademicPeriod> findAll() {
        return periodRepository.streamAll(Sort.by("code"))
                .toList();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Integer id) {
        return periodRepository.findByIdOptional(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response create(AcademicPeriod period) {
        if (isBlank(period.getCode()) || isBlank(period.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        period.setId(null);
        if (period.getActive() == null) {
            period.setActive(Boolean.FALSE);
        }
        periodRepository.persist(period);

        var uri = UriBuilder.fromUri("/periods/{id}")
                .build(period.getId());

        return Response.created(uri).entity(period).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, AcademicPeriod period) {
        return periodRepository.findByIdOptional(id)
                .map(existing -> {
                    existing.setCode(period.getCode());
                    existing.setName(period.getName());
                    existing.setActive(period.getActive() != null ? period.getActive() : existing.getActive());
                    periodRepository.persist(existing);
                    return Response.ok(existing).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = periodRepository.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
