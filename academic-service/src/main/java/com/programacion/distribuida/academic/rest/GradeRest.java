package com.programacion.distribuida.academic.rest;

import com.programacion.distribuida.academic.db.Enrollment;
import com.programacion.distribuida.academic.db.Grade;
import com.programacion.distribuida.academic.dtos.GradeDto;
import com.programacion.distribuida.academic.repo.CourseRepository;
import com.programacion.distribuida.academic.repo.EnrollmentRepository;
import com.programacion.distribuida.academic.repo.GradeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.Optional;

@Path("/grades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class GradeRest {

    final GradeRepository gradeRepository;
    final EnrollmentRepository enrollmentRepository;
    final CourseRepository courseRepository;

    @Inject
    public GradeRest(GradeRepository gradeRepository,
                     EnrollmentRepository enrollmentRepository,
                     CourseRepository courseRepository) {
        this.gradeRepository = gradeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Integer id) {
        return gradeRepository.findByIdOptional(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    // Registra (o actualiza) la nota de un estudiante en una asignatura y periodo
    @POST
    public Response create(GradeDto dto) {
        if (dto.getStudentId() == null || dto.getSubjectId() == null
                || dto.getPeriodId() == null || dto.getScore() == null
                || dto.getScore() < 0 || dto.getScore() > 10) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return findEnrollment(dto.getStudentId(), dto.getSubjectId(), dto.getPeriodId())
                .map(enrollment -> {
                    Grade grade = gradeRepository.find("enrollmentId", enrollment.getId())
                            .firstResultOptional()
                            .orElseGet(() -> Grade.builder()
                                    .enrollmentId(enrollment.getId())
                                    .build());
                    grade.setScore(dto.getScore());
                    gradeRepository.persist(grade);

                    dto.setId(grade.getId());
                    var uri = UriBuilder.fromUri("/grades/{id}")
                            .build(grade.getId());

                    return Response.created(uri).entity(dto).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = gradeRepository.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Matricula del estudiante en el curso de esa asignatura y periodo
    private Optional<Enrollment> findEnrollment(Integer studentId, Integer subjectId, Integer periodId) {
        return courseRepository.stream("subjectId = ?1 and periodId = ?2", subjectId, periodId)
                .map(course -> enrollmentRepository
                        .find("studentId = ?1 and courseId = ?2", studentId, course.getId())
                        .firstResultOptional())
                .flatMap(Optional::stream)
                .findFirst();
    }
}
