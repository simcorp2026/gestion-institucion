package com.programacion.distribuida.academic.rest;

import com.programacion.distribuida.academic.db.Career;
import com.programacion.distribuida.academic.db.Student;
import com.programacion.distribuida.academic.dtos.StudentDto;
import com.programacion.distribuida.academic.repo.CareerRepository;
import com.programacion.distribuida.academic.repo.GradeReportRepository;
import com.programacion.distribuida.academic.repo.StudentRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.List;

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class StudentRest {

    final StudentRepository studentRepository;
    final CareerRepository careerRepository;
    final GradeReportRepository gradeReportRepository;

    @Inject
    public StudentRest(StudentRepository studentRepository,
                       CareerRepository careerRepository,
                       GradeReportRepository gradeReportRepository) {
        this.studentRepository = studentRepository;
        this.careerRepository = careerRepository;
        this.gradeReportRepository = gradeReportRepository;
    }

    @GET
    public List<StudentDto> findAll() {
        return studentRepository.streamAll(Sort.by("lastName", "firstName"))
                .map(this::toDto)
                .toList();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Integer id) {
        return studentRepository.findByIdOptional(id)
                .map(this::toDto)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    // Historial academico: todas las notas de un estudiante
    @GET
    @Path("/{id}/history")
    public Response history(@PathParam("id") Integer id) {
        return studentRepository.findByIdOptional(id)
                .map(student -> gradeReportRepository
                        .stream("studentId", Sort.by("periodCode", "subjectName"), id)
                        .toList())
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response create(Student student) {
        if (isBlank(student.getIdentification()) || isBlank(student.getFirstName())
                || isBlank(student.getLastName()) || student.getCareerId() == null
                || careerRepository.findByIdOptional(student.getCareerId()).isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        student.setId(null);
        studentRepository.persist(student);

        var uri = UriBuilder.fromUri("/students/{id}")
                .build(student.getId());

        return Response.created(uri).entity(toDto(student)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, Student student) {
        return studentRepository.findByIdOptional(id)
                .map(existing -> {
                    existing.setIdentification(student.getIdentification());
                    existing.setFirstName(student.getFirstName());
                    existing.setLastName(student.getLastName());
                    existing.setEmail(student.getEmail());
                    existing.setCareerId(student.getCareerId() != null ? student.getCareerId() : existing.getCareerId());
                    studentRepository.persist(existing);
                    return Response.ok(toDto(existing)).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = studentRepository.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private StudentDto toDto(Student student) {
        String careerName = careerRepository.findByIdOptional(student.getCareerId())
                .map(Career::getName)
                .orElse("unknown");

        return StudentDto.builder()
                .id(student.getId())
                .identification(student.getIdentification())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .careerId(student.getCareerId())
                .careerName(careerName)
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
