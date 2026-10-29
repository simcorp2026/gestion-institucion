package com.programacion.distribuida.analytics.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeReportDto {
    private Integer gradeId;
    private Integer studentId;
    private String studentName;
    private Integer careerId;
    private String careerCode;
    private String careerName;
    private Integer subjectId;
    private String subjectCode;
    private String subjectName;
    private Integer periodId;
    private String periodCode;
    private Double score;
}
