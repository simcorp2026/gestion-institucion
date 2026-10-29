package com.programacion.distribuida.academic.repo;

import com.programacion.distribuida.academic.db.Subject;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class SubjectRepository implements PanacheRepositoryBase<Subject, Integer> {
}
