package com.mongodbprax.dbprax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    private String name;
    private String department;
    private Double salary;
    private LocalDate joinedDate;

    private Address address;          // 🏠 Embedded object
    private List<String> skills;      // 🛠️ Array of skills
}
