package com.programacion.distribuida.academic.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;

// Entidad de solo lectura sobre la vista v_grade_report
@Entity
@Immutable
@Table(name = "v_grade_report")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeReport {

    @Id
    @Column(name = "grade_id")
    private Integer gradeId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "career_id")
    private Integer careerId;

    @Column(name = "career_code")
    private String careerCode;

    @Column(name = "career_name")
    private String careerName;

    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "period_id")
    private Integer periodId;

    @Column(name = "period_code")
    private String periodCode;

    @Column(name = "score")
    private Double score;
}
