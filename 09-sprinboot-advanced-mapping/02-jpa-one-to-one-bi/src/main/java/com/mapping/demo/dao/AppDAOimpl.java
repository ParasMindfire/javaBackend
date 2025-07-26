package com.mapping.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mapping.demo.entity.Instructor;
import com.mapping.demo.entity.InstructorDetail;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class AppDAOimpl implements AppDAO {

    private EntityManager entityManager;

    @Autowired
    public AppDAOimpl(EntityManager entityManager){
        this.entityManager=entityManager;
    }

    @Override
    @Transactional
    public void save(Instructor theInstructor){
        entityManager.persist(theInstructor);
    }

    @Override
    public Instructor findInstructorById(int theId) {
        return entityManager.find(Instructor.class,theId);
    }

    @Transactional
    @Override
    public void deleteInstructorById(int theId) {
        Instructor tempInstructor=entityManager.find(Instructor.class,theId);
        entityManager.remove(tempInstructor);
    }

    @Override
    public InstructorDetail findInstructorDetailById(int theId) {
        return entityManager.find(InstructorDetail.class,theId);
    }

    @Override
    @Transactional
    public void deleteInstructorDetailById(int theId) {
        InstructorDetail tempInstructionDetail=entityManager.find(InstructorDetail.class,theId);

        tempInstructionDetail.getInstructor().setInstructorDetail(null);

        entityManager.remove(tempInstructionDetail);

    }

}
