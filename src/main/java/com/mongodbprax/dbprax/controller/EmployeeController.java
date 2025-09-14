package com.mongodbprax.dbprax.controller;

import com.mongodbprax.dbprax.model.DeptAvgSalary;
import com.mongodbprax.dbprax.model.Employee;
import com.mongodbprax.dbprax.repository.EmployeeRepository;
import com.mongodbprax.dbprax.model.EmployeeSummary;
import com.mongodbprax.dbprax.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;
    @Autowired
    private EmployeeRepository repository;

    @PostMapping("/createEmp")
    public ResponseEntity<Employee> create(@RequestBody Employee emp) {
        return ResponseEntity.ok(service.create(emp));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "Greeting from hello world";
    }

//    @GetMapping("/by-dept/{dept}")
//    public ResponseEntity<List<Employee>> getByDept(@PathVariable String dept) {
//        return ResponseEntity.ok(service.findByDept(dept));
//    }

    @GetMapping("/high-earners/{salary}")
    public ResponseEntity<List<Employee>> highEarners(@PathVariable double salary) {
        return ResponseEntity.ok(service.findHighEarners(salary));
    }

    @GetMapping("/by-city/{city}")
    public ResponseEntity<List<Employee>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(service.findByCity(city));
    }

    @GetMapping("/by-skill/{skill}")
    public ResponseEntity<List<Employee>> getBySkill(@PathVariable String skill) {
        return ResponseEntity.ok(service.findBySkill(skill));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Employee>> getPagedEmployees(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(service.getPagedEmployees(page, size));
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Employee>> getSortedEmployees(
            @RequestParam String field,
            @RequestParam(defaultValue = "asc") String dir) {
        return ResponseEntity.ok(service.getSortedEmployees(field, dir));
    }

    //Summary
    @GetMapping("/summary/{dept}")
    public ResponseEntity<List<EmployeeSummary>> getSummaryByDept(@PathVariable String dept) {
        return ResponseEntity.ok(service.findByDept(dept));
    }

    @GetMapping("/by-city-and-skill-or-id")
    public ResponseEntity<List<Employee>> getByCityAndSkillOrId(
            @RequestParam String city,
            @RequestParam String skill,
            @RequestParam String id
            ) {
        return ResponseEntity.ok(repository.findByCityAndSkillOrId(city, skill, id));
    }

    @GetMapping("/avg-salary-by-dept")
    public ResponseEntity<List<DeptAvgSalary>> avgSalaryByDept() {
        return ResponseEntity.ok(repository.getAvgSalaryByDepartment());
    }

    @PutMapping("/bulk-salary-update")
    public ResponseEntity<String> bulkSalaryUpdate(
            @RequestParam String dept, @RequestParam double percent) {
        service.bulkUpdateSalary(dept, percent);
        return ResponseEntity.ok("Bulk salary update done!");
    }



}
