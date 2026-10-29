package com.programacion.distribuida.academic.repo;

import com.programacion.distribuida.academic.db.Course;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class CourseRepository implements PanacheRepositoryBase<Course, Integer> {
}
