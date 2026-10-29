package com.programacion.distribuida.analytics.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatDto {
    private Integer studentId;
    private String name;
    private String career;
    private Double average;
    private Long failedSubjects;
}
