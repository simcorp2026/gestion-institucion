package com.programacion.distribuida.academic.repo;

import com.programacion.distribuida.academic.db.Career;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class CareerRepository implements PanacheRepositoryBase<Career, Integer> {
}
