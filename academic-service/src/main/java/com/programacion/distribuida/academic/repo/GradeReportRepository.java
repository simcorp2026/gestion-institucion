package com.programacion.distribuida.academic.repo;

import com.programacion.distribuida.academic.db.GradeReport;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class GradeReportRepository implements PanacheRepositoryBase<GradeReport, Integer> {
}
