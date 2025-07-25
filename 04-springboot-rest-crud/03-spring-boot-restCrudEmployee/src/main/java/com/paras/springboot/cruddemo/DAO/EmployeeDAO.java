package com.paras.springboot.cruddemo.DAO;

import java.util.List;

import com.paras.springboot.cruddemo.entity.Employee;

public interface EmployeeDAO {
    List<Employee>findAll();

    Employee findById(int theId);

    Employee save(Employee theEmployee);

    void deleteById(int theId);
}
