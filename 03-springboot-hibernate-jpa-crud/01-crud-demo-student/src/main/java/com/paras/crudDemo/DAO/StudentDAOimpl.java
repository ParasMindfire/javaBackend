package com.paras.crudDemo.DAO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paras.crudDemo.Entity.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class StudentDAOimpl implements StudentDAO {

    private EntityManager entityManager;

    @Autowired
    public StudentDAOimpl(EntityManager entityManager) {
        this.entityManager=entityManager;
    }


    @Override
    @Transactional
    public void save(Student theStudent) {
        entityManager.persist(theStudent);
    }

    @Override
    public Student findById(Integer id) {
        return entityManager.find(Student.class,id);
    }

    @Override
    public List<Student> findByAll() {
        TypedQuery<Student>theQuery=entityManager.createQuery("From Student order by id desc",Student.class);
        return theQuery.getResultList();
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        TypedQuery<Student>theQuery=entityManager.createQuery("From Student where lastName=:lName",Student.class);
        theQuery.setParameter("lName", lastName);
        return theQuery.getResultList();
    }
  
    //rough done by me
    // @Override
    // @Transactional
    // public Student updateStudent(String currEmail, String updatedEmail) {
    //     TypedQuery<Student> currStudQuery = entityManager.createQuery(
    //         "SELECT s FROM Student s WHERE s.email = :currEmail",
    //         Student.class
    //     );
    //     currStudQuery.setParameter("currEmail", currEmail);

    //     Student currStudent = currStudQuery.getSingleResult();

    //     currStudent.setEmail(updatedEmail);

    //     entityManager.merge(currStudent);

    //     return currStudent;
    // }

    @Override
    @Transactional
    public void update(Student theStudent) {
        entityManager.merge(theStudent);
    }

    @Override
    @Transactional
    public void dlete(Integer Id) {
        Student theStudent=entityManager.find(Student.class,Id);
        entityManager.remove(theStudent);
    }

    @Override
    @Transactional
    public int dleteAll() {
        int numberOfDeletes=entityManager.createQuery("DELETE from Student").executeUpdate();
        return numberOfDeletes;
    }


}
