package com.programacion.distribuida.analytics.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectStatDto {
    private String subject;
    private Long total;
    private Long approved;
    private Long failed;
    private Double approvalRate;
    private Double failRate;
}
