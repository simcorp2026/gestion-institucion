package com.programacion.distribuida.academic.db;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subject_id", nullable = false)
    private Integer subjectId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "period_id", nullable = false)
    private Integer periodId;

    @Column(nullable = false, length = 5)
    private String parallel;
}
