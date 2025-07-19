package com.paras.springboot.cruddemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paras.springboot.cruddemo.DAO.EmployeeRepository;
import com.paras.springboot.cruddemo.entity.Employee;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepo;

    public EmployeeServiceImpl(EmployeeRepository theEmployeeRepo){
        employeeRepo=theEmployeeRepo;
    }

    @Override
    public List<Employee>findAll(){
        return employeeRepo.findAll();
    }

    @Override
    public Employee findById(int theId) {
        Optional<Employee>result=employeeRepo.findById(theId);

        Employee employee=null;

        if(result.isPresent()){
            employee=result.get();
        }else{
            throw new RuntimeException("We couldnt find employee of id "+theId);
        }

        return employee;
    }

    @Override
    public Employee save(Employee theEmployee) {
        return employeeRepo.save(theEmployee);
    }
    
    @Override
    public void deleteById(int theId) {
        employeeRepo.deleteById(theId);
    }

}
