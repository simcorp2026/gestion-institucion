package com.programacion.distribuida.academic.rest;

import com.programacion.distribuida.academic.db.Subject;
import com.programacion.distribuida.academic.repo.SubjectRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.List;

@Path("/subjects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class SubjectRest {

    final SubjectRepository subjectRepository;

    @Inject
    public SubjectRest(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @GET
    public List<Subject> findAll() {
        return subjectRepository.streamAll(Sort.by("code"))
                .toList();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Integer id) {
        return subjectRepository.findByIdOptional(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response create(Subject subject) {
        if (isBlank(subject.getCode()) || isBlank(subject.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        subject.setId(null);
        if (subject.getCredits() == null || subject.getCredits() < 0) {
            subject.setCredits(0);
        }
        subjectRepository.persist(subject);

        var uri = UriBuilder.fromUri("/subjects/{id}")
                .build(subject.getId());

        return Response.created(uri).entity(subject).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, Subject subject) {
        return subjectRepository.findByIdOptional(id)
                .map(existing -> {
                    existing.setCode(subject.getCode());
                    existing.setName(subject.getName());
                    existing.setCredits(subject.getCredits() != null ? subject.getCredits() : existing.getCredits());
                    subjectRepository.persist(existing);
                    return Response.ok(existing).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = subjectRepository.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
