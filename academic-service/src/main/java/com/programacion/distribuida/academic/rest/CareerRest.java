package com.programacion.distribuida.academic.rest;

import com.programacion.distribuida.academic.db.Career;
import com.programacion.distribuida.academic.repo.CareerRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.List;

@Path("/careers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class CareerRest {

    final CareerRepository careerRepository;

    @Inject
    public CareerRest(CareerRepository careerRepository) {
        this.careerRepository = careerRepository;
    }

    @GET
    public List<Career> findAll() {
        return careerRepository.streamAll(Sort.by("code"))
                .toList();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Integer id) {
        return careerRepository.findByIdOptional(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response create(Career career) {
        if (isBlank(career.getCode()) || isBlank(career.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        career.setId(null);
        careerRepository.persist(career);

        var uri = UriBuilder.fromUri("/careers/{id}")
                .build(career.getId());

        return Response.created(uri).entity(career).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, Career career) {
        return careerRepository.findByIdOptional(id)
                .map(existing -> {
                    existing.setCode(career.getCode());
                    existing.setName(career.getName());
                    careerRepository.persist(existing);
                    return Response.ok(existing).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = careerRepository.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
