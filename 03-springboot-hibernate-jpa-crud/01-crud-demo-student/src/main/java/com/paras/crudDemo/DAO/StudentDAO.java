package com.paras.crudDemo.DAO;

import java.util.List;

import com.paras.crudDemo.Entity.Student;

public interface StudentDAO {
    void save(Student theStudent);
    Student findById(Integer id);
    List<Student>findByAll();
    List<Student>findByLastName(String lName);
    // Student updateStudent(String currEmail,String updatedEmail);
    void update(Student theStudent);
    void dlete(Integer Id);
    int dleteAll();
}
