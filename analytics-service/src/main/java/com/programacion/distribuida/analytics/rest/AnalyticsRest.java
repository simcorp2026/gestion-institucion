package com.programacion.distribuida.analytics.rest;

import com.programacion.distribuida.analytics.dtos.StudentStatDto;
import com.programacion.distribuida.analytics.dtos.SubjectCareerStatDto;
import com.programacion.distribuida.analytics.dtos.SubjectStatDto;
import com.programacion.distribuida.analytics.dtos.SummaryDto;
import com.programacion.distribuida.analytics.service.IndicatorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AnalyticsRest {

    final IndicatorService indicatorService;

    @Inject
    public AnalyticsRest(IndicatorService indicatorService) {
        this.indicatorService = indicatorService;
    }

    @GET
    @Path("/summary")
    public SummaryDto summary(@QueryParam("period") String period,
                              @QueryParam("career") String career,
                              @QueryParam("subject") String subject) {
        return indicatorService.summary(period, career, subject);
    }

    @GET
    @Path("/average-by-career")
    public Map<String, Double> averageByCareer(@QueryParam("period") String period,
                                               @QueryParam("career") String career,
                                               @QueryParam("subject") String subject) {
        return indicatorService.averageByCareer(period, career, subject);
    }

    @GET
    @Path("/approval-by-subject")
    public List<SubjectStatDto> approvalBySubject(@QueryParam("period") String period,
                                                  @QueryParam("career") String career,
                                                  @QueryParam("subject") String subject) {
        return indicatorService.approvalBySubject(period, career, subject);
    }

    @GET
    @Path("/approval-by-career-subject")
    public List<SubjectCareerStatDto> approvalByCareerSubject(@QueryParam("period") String period,
                                                              @QueryParam("career") String career,
                                                              @QueryParam("subject") String subject) {
        return indicatorService.approvalByCareerAndSubject(period, career, subject);
    }

    @GET
    @Path("/top-students")
    public List<StudentStatDto> topStudents(@QueryParam("period") String period,
                                            @QueryParam("career") String career,
                                            @QueryParam("subject") String subject) {
        return indicatorService.top10(period, career, subject);
    }

    @GET
    @Path("/at-risk")
    public List<StudentStatDto> atRisk(@QueryParam("period") String period,
                                       @QueryParam("career") String career,
                                       @QueryParam("subject") String subject) {
        return indicatorService.atRisk(period, career, subject);
    }
}
