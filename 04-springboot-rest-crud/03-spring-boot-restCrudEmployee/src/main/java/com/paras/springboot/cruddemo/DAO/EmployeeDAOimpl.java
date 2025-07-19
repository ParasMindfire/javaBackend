package com.paras.springboot.cruddemo.DAO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paras.springboot.cruddemo.entity.Employee;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Repository
public class EmployeeDAOimpl implements EmployeeDAO {
    private EntityManager entityManager;

    @Autowired
    public EmployeeDAOimpl(EntityManager theEntityManager){
        entityManager=theEntityManager;
    }

    @Override
    public List<Employee> findAll() {
        TypedQuery<Employee>theQuery=entityManager.createQuery("from Employee",Employee.class);
        List<Employee>employees=theQuery.getResultList();
        return employees;
    }

    @Override
    public Employee findById(int theId) {
        Employee theEmployee=entityManager.find(Employee.class,theId);
        return theEmployee;
    }

    @Override
    public Employee save(Employee theEmployee) {
        Employee dbEmployee=entityManager.merge(theEmployee);
        return dbEmployee;
    }

    @Override
    public void deleteById(int theId) {
        Employee theEmp=entityManager.find(Employee.class,theId);
        entityManager.remove(theEmp);
    }

}
