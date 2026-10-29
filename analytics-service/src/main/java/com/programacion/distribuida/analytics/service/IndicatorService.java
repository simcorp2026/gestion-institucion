package com.programacion.distribuida.analytics.service;

import com.programacion.distribuida.analytics.clients.AcademicRestClient;
import com.programacion.distribuida.analytics.dtos.*;
import com.programacion.distribuida.analytics.dtos.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class IndicatorService {

    final AcademicRestClient academicClient;

    @Inject
    @ConfigProperty(name = "analytics.risk-threshold", defaultValue = "7.0")
    Double threshold;

    @Inject
    @ConfigProperty(name = "analytics.risk-max-failed", defaultValue = "2")
    Long maxFailed;

    @Inject
    public IndicatorService(@RestClient AcademicRestClient academicClient) {
        this.academicClient = academicClient;
    }

    // Resumen para el dashboard
    public SummaryDto summary(String period, String career, String subject) {
        return SummaryDto.builder()
                .totalStudents(totalStudents(period, career, subject))
                .averageByCareer(averageByCareer(period, career, subject))
                .topStudents(top10(period, career, subject))
                .atRiskStudents(atRisk(period, career, subject))
                .build();
    }

    // Indicador 1: promedio por carrera (groupingBy + averagingDouble)
    public Map<String, Double> averageByCareer(String period, String career, String subject) {
        return load(period, career, subject).stream()
                .collect(Collectors.groupingBy(
                        GradeReportDto::getCareerName,
                        TreeMap::new,
                        Collectors.averagingDouble(GradeReportDto::getScore)));
    }

    // Indicador 2: aprobacion y reprobacion por asignatura
    public List<SubjectStatDto> approvalBySubject(String period, String career, String subject) {
        return load(period, career, subject).stream()
                .collect(Collectors.groupingBy(GradeReportDto::getSubjectName))
                .entrySet().stream()
                .map(entry -> {
                    var grades = entry.getValue();
                    long total = grades.size();
                    long approved = countApproved(grades);
                    long failed = total - approved;
                    return SubjectStatDto.builder()
                            .subject(entry.getKey())
                            .total(total)
                            .approved(approved)
                            .failed(failed)
                            .approvalRate(round(100.0 * approved / total))
                            .failRate(round(100.0 * failed / total))
                            .build();
                })
                .sorted(Comparator.comparing(SubjectStatDto::getSubject))
                .toList();
    }

    // Reporte por carrera y asignatura
    public List<SubjectCareerStatDto> approvalByCareerAndSubject(String period, String career, String subject) {
        return load(period, career, subject).stream()
                .collect(Collectors.groupingBy(g -> g.getCareerName() + "|" + g.getSubjectName()))
                .values().stream()
                .map(grades -> {
                    var first = grades.getFirst();
                    long total = grades.size();
                    long approved = countApproved(grades);
                    return SubjectCareerStatDto.builder()
                            .career(first.getCareerName())
                            .subject(first.getSubjectName())
                            .total(total)
                            .approved(approved)
                            .approvalRate(round(100.0 * approved / total))
                            .build();
                })
                .sorted(Comparator.comparing(SubjectCareerStatDto::getCareer)
                        .thenComparing(SubjectCareerStatDto::getSubject))
                .toList();
    }

    // Indicador 3: top 10 (sorted + limit)
    public List<StudentStatDto> top10(String period, String career, String subject) {
        return studentStats(period, career, subject).stream()
                .sorted(Comparator.comparing(StudentStatDto::getAverage).reversed())
                .limit(10)
                .toList();
    }

    // Indicador 4: estudiantes en riesgo (filter)
    public List<StudentStatDto> atRisk(String period, String career, String subject) {
        return studentStats(period, career, subject).stream()
                .filter(s -> s.getAverage() < threshold || s.getFailedSubjects() >= maxFailed)
                .sorted(Comparator.comparing(StudentStatDto::getAverage))
                .toList();
    }

    public Integer totalStudents(String period, String career, String subject) {
        return studentStats(period, career, subject).size();
    }

    // filter: deja pasar solo las notas del periodo, carrera y asignatura pedidos
    private List<GradeReportDto> load(String period, String career, String subject) {
        return academicClient.findGrades().stream()
                .filter(g -> isBlank(period) || g.getPeriodCode().equals(period))
                .filter(g -> isBlank(career) || g.getCareerCode().equals(career))
                .filter(g -> isBlank(subject) || g.getSubjectCode().equals(subject))
                .toList();
    }

    // Base comun: promedio y materias reprobadas de cada estudiante
    private List<StudentStatDto> studentStats(String period, String career, String subject) {
        return load(period, career, subject).stream()
                .collect(Collectors.groupingBy(GradeReportDto::getStudentId))
                .values().stream()
                .map(grades -> {
                    var first = grades.getFirst();
                    double average = grades.stream()
                            .mapToDouble(GradeReportDto::getScore)
                            .average()
                            .orElse(0);
                    return StudentStatDto.builder()
                            .studentId(first.getStudentId())
                            .name(first.getStudentName())
                            .career(first.getCareerName())
                            .average(round(average))
                            .failedSubjects(grades.size() - countApproved(grades))
                            .build();
                })
                .toList();
    }

    private long countApproved(List<GradeReportDto> grades) {
        return grades.stream()
                .filter(g -> g.getScore() >= threshold)
                .count();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
