package com.programacion.distribuida.analytics.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCareerStatDto {
    private String career;
    private String subject;
    private Long total;
    private Long approved;
    private Double approvalRate;
}
