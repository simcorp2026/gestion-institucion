package com.programacion.distribuida.academic.rest;

import com.programacion.distribuida.academic.db.GradeReport;
import com.programacion.distribuida.academic.repo.GradeReportRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

// Reporte plano de notas (vista v_grade_report), lo consume analytics-service
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class ReportRest {

    final GradeReportRepository gradeReportRepository;

    @Inject
    public ReportRest(GradeReportRepository gradeReportRepository) {
        this.gradeReportRepository = gradeReportRepository;
    }

    @GET
    @Path("/grades")
    public List<GradeReport> grades() {
        return gradeReportRepository.streamAll(Sort.by("gradeId"))
                .toList();
    }
}
