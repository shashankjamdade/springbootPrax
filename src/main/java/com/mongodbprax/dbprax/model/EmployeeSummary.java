package com.mongodbprax.dbprax.model;

import java.util.List;

public interface EmployeeSummary {
    String getName();
    String getDepartment();
    List<String> getSkills();
    List<EmployeeSummary> findByDepartment(String department);
}
