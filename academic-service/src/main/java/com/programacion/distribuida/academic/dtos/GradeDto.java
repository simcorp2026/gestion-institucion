package com.programacion.distribuida.academic.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDto {
    private Integer id;
    private Integer studentId;
    private Integer subjectId;
    private Integer periodId;
    private Double score;
}
