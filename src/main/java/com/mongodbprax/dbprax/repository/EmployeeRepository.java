package com.mongodbprax.dbprax.repository;

import com.mongodbprax.dbprax.model.DeptAvgSalary;
import com.mongodbprax.dbprax.model.Employee;
import com.mongodbprax.dbprax.model.EmployeeSummary;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
//    List<Employee> findByDepartment(String department);
    List<Employee> findBySalaryGreaterThan(Double salary);

    // New method to filter by embedded address field
    List<Employee> findByAddress_City(String city);

    // New method to filter by array field
    List<Employee> findBySkillsContaining(String skill);

    List<EmployeeSummary> findByDepartment(String department); // ✅

    @Query("{ '$or': [ { 'address.city': ?0, 'skills': ?1 }, { '_id': ?2 } ] }")
    List<Employee> findByCityAndSkillOrId(String city, String skill, String id);


    @Aggregation(pipeline = {
            "{ $group: { _id: '$department', avgSalary: { $avg: '$salary' } } }",
            "{ $project: { department: '$_id', avgSalary: 1, _id: 0 } }"
    })
    List<DeptAvgSalary> getAvgSalaryByDepartment();

}
