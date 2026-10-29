package com.programacion.distribuida.analytics.dtos;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDto {
    private Integer totalStudents;
    private Map<String, Double> averageByCareer;
    private List<StudentStatDto> topStudents;
    private List<StudentStatDto> atRiskStudents;
}
