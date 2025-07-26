package com.mapping.demo.dao;

import java.util.List;

import com.mapping.demo.entity.Course;
import com.mapping.demo.entity.Instructor;
import com.mapping.demo.entity.InstructorDetail;
import com.mapping.demo.entity.Student;

public interface AppDAO {
    void save(Instructor theInstructor);
    Instructor findInstructorById(int theId);
    void deleteInstructorById(int theId);
    InstructorDetail findInstructorDetailById(int theId);
    void deleteInstructorDetailById(int theId);
    List<Course> findCoursesByInstructorId(int theId);
    Instructor findInstrcutorByIdjoinFetch(int theId);
    void update(Instructor tempInstructor);
    void update(Course course);
    Course findCourseById(int theId);
    void deleteInstructor(int theId);
    void deleteCourse(int theId);
    void save(Course theCourse);
    Course findCourseAndReviewsByCourseId(int theId);
    Course findCourseAndStudentByCourseId(int theId);
    Student findStudentAndCourseByStudentId(int theId);
    void update(Student tempStudent);
    void deleteStudent(int theId);
}
