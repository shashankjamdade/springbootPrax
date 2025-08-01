package com.mongodbprax.dbprax.service;

import com.mongodbprax.dbprax.model.Employee;
import com.mongodbprax.dbprax.repository.EmployeeRepository;
import com.mongodbprax.dbprax.model.EmployeeSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Employee create(Employee emp) {
        return repository.save(emp);
    }

    public List<Employee> getAll() {
        return repository.findAll();
    }

//    public List<Employee> findByDept(String dept) {
//        return repository.findByDepartment(dept);
//    }

    public List<Employee> findHighEarners(double salary) {
        return repository.findBySalaryGreaterThan(salary);
    }

    public List<Employee> findByCity(String city) {
        return repository.findByAddress_City(city);
    }

    public List<Employee> findBySkill(String skill) {
        return repository.findBySkillsContaining(skill);
    }

    //Paging & sorting
    public Page<Employee> getPagedEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public List<Employee> getSortedEmployees(String field, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(field).descending() : Sort.by(field).ascending();
        return repository.findAll(sort);
    }

    public List<EmployeeSummary> findByDept(String dept) {
        return repository.findByDepartment(dept);
    }


    public void bulkUpdateSalary(String dept, double hikePercent) {
        Query query = Query.query(Criteria.where("department").is(dept));
        Update update = new Update().multiply("salary", 1 + (hikePercent / 100.0));
        mongoTemplate.updateMulti(query, update, Employee.class);
    }
}
