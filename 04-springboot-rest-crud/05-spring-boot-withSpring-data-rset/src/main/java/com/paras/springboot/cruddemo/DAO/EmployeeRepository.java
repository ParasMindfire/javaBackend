package com.paras.springboot.cruddemo.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paras.springboot.cruddemo.entity.Employee;

// @RepositoryRestResource(path="members")
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    
}
