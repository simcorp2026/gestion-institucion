package com.programacion.distribuida.academic.db;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grade")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "enrollment_id", nullable = false, unique = true)
    private Integer enrollmentId;

    @Column(nullable = false)
    private Double score;
}
