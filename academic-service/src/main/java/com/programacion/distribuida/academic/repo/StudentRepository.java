package com.programacion.distribuida.academic.repo;

import com.programacion.distribuida.academic.db.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class StudentRepository implements PanacheRepositoryBase<Student, Integer> {
}
