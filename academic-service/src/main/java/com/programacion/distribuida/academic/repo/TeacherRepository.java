package com.programacion.distribuida.academic.repo;

import com.programacion.distribuida.academic.db.Teacher;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class TeacherRepository implements PanacheRepositoryBase<Teacher, Integer> {
}
