package com.mapping.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mapping.demo.entity.Course;
import com.mapping.demo.entity.Instructor;
import com.mapping.demo.entity.InstructorDetail;
import com.mapping.demo.entity.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

    @Override
    public List<Course> findCoursesByInstructorId(int theId) {
        TypedQuery<Course>query=entityManager.createQuery("from Course where instructor.id=:data",Course.class);
        query.setParameter("data", theId);
        List<Course>courses=query.getResultList();
        return courses;
    }

    @Override
    public Instructor findInstrcutorByIdjoinFetch(int theId) {
        TypedQuery<Instructor>query=entityManager.createQuery("select i from Instructor i "
                            +" JOIN FETCH i.courses "
                            +" JOIN FETCH i.instructorDetail "
                            +" where i.id=:data"
                            ,Instructor.class);

        query.setParameter("data", theId);
        Instructor instructor = query.getSingleResult();
        return instructor;
    }

    @Override
    @Transactional
    public void update(Instructor tempInstructor) {
        entityManager.merge(tempInstructor);
    }

    @Override
    @Transactional
    public void update(Course course) {
        entityManager.merge(course);
    }

    @Override
    public Course findCourseById(int theId) {
        Course tempCourse=entityManager.find(Course.class,theId);
        return tempCourse;
    }

    @Override
    @Transactional
    public void deleteCourse(int theId) {
        Course tempCourse=entityManager.find(Course.class,theId);
        entityManager.remove(tempCourse);
    }

    @Override
    @Transactional
    public void deleteInstructor(int theId) {
        Instructor tempInstructor=entityManager.find(Instructor.class,theId);
        List<Course>tempCourses=tempInstructor.getCourses();
        for(Course c:tempCourses){
            c.setInstructor(null);
        }
        entityManager.remove(tempInstructor);
    }

    @Override
    @Transactional
    public void save(Course theCourse) {
        entityManager.persist(theCourse);
    }

    @Override
    public Course findCourseAndReviewsByCourseId(int theId) {
        TypedQuery<Course>query=entityManager.createQuery("select c from Course c "
                            +"JOIN FETCH c.reviews "
                            +"where c.id=:data",Course.class);
        query.setParameter("data", theId);

        Course course=query.getSingleResult();

        return course;
    }

    @Override
    public Course findCourseAndStudentByCourseId(int theId) {
        TypedQuery<Course>query=entityManager.createQuery("select c from Course c "
                            +"JOIN FETCH c.students "
                            +"where c.id=:data",Course.class);
        query.setParameter("data", theId);

        Course course=query.getSingleResult();

        return course;
    }

    @Override
    public Student findStudentAndCourseByStudentId(int theId) {
        TypedQuery<Student>query=entityManager.createQuery("select s from Student s "
                            +"JOIN FETCH s.courses "
                            +"where s.id=:data",Student.class);
        query.setParameter("data", theId);

        Student Students=query.getSingleResult();

        return Students;
    }

    @Override
    @Transactional
    public void update(Student tempStudent) {
        entityManager.merge(tempStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(int theId) {
        Student tempStudent=entityManager.find(Student.class,theId);
        if(tempStudent!=null){
            List<Course>courses=tempStudent.getCourse();
            for(Course c:courses){
                c.getStudents().remove(tempStudent);
            }
            entityManager.remove(tempStudent);
        }
    }


}
