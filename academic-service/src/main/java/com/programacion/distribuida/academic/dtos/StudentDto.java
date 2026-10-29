package com.programacion.distribuida.academic.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private Integer id;
    private String identification;
    private String firstName;
    private String lastName;
    private String email;
    private Integer careerId;
    private String careerName;
}
