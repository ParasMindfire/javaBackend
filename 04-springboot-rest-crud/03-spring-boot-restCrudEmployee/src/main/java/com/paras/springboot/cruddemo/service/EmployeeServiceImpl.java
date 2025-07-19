package com.paras.springboot.cruddemo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paras.springboot.cruddemo.DAO.EmployeeDAO;
import com.paras.springboot.cruddemo.entity.Employee;

import jakarta.transaction.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeDAO employeeDao;

    public EmployeeServiceImpl(EmployeeDAO theEmployeeDao){
        employeeDao=theEmployeeDao;
    }

    @Override
    public List<Employee>findAll(){
        return employeeDao.findAll();
    }

    @Override
    public Employee findById(int theId) {
        return employeeDao.findById(theId);
    }

    @Transactional
    @Override
    public Employee save(Employee theEmployee) {
        return employeeDao.save(theEmployee);
    }
    
    @Transactional
    @Override
    public void deleteById(int theId) {
        employeeDao.deleteById(theId);
    }

}
