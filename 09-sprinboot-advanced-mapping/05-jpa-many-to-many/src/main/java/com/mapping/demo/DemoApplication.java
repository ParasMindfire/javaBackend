package com.mapping.demo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mapping.demo.dao.AppDAO;
import com.mapping.demo.entity.Course;
import com.mapping.demo.entity.Instructor;
import com.mapping.demo.entity.InstructorDetail;
import com.mapping.demo.entity.Review;
import com.mapping.demo.entity.Student;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner(AppDAO appDAO){
		return _->{
			// createCourseAndStudent(appDAO);
			// findCourseAndStudent(appDAO);
			// findStudentsAndCourses(appDAO);
			// addMoreCoursesForStudent(appDAO);
			// deleteCourseById(appDAO);
			deleteStudentByid(appDAO);
		};
	}

	private void createConstructor(AppDAO appDAO){
		Instructor tempInstructor1=new Instructor("paras","bhatia","parasbhatia@gmail.com");
		InstructorDetail tempInstructorDetail1=new InstructorDetail("ok.com","coding");
		tempInstructor1.setInstructorDetail(tempInstructorDetail1);
		Instructor tempInstructor=new Instructor("pallavi","sahoo","pallavisahoo@gmail.com");
		InstructorDetail tempInstructorDetail=new InstructorDetail("ko.com","dancing");
		tempInstructor.setInstructorDetail(tempInstructorDetail);

		System.err.println("Saving Instructor"+tempInstructor);
		appDAO.save(tempInstructor);
		appDAO.save(tempInstructor1);
		System.err.println("Done");
	}

	private void findInstructor(AppDAO appDAO){
		int theId=2;
		System.out.println("Finding instructor id: "+theId);
		Instructor tempInstructor=appDAO.findInstructorById(theId);
		System.out.println("tempinstructor: "+tempInstructor);
		System.out.println("the associated instructorDetial only "+tempInstructor.getInstructionDetail());
	}

	private void deleteInstructor(AppDAO appDAO){
		int theId=1;
		System.out.println("Delete the User with id "+theId);
		appDAO.deleteInstructorById(theId);
		System.err.println("Done");
	}

	private void findInstructorDetail(AppDAO appDAO){
		int theId=2;
		InstructorDetail tempInstructorDetail=appDAO.findInstructorDetailById(theId);
		System.out.println("tempInstructorDetail "+tempInstructorDetail);
		System.out.println("the associated instructor: "+tempInstructorDetail.getInstructor());
		System.out.println("Done");
	}

	private void deleteInstructorDetail(AppDAO appDAO){
		int theId=3;
		System.out.println("Deleting Instructor Detail with id "+theId);
		appDAO.deleteInstructorDetailById(theId);
		System.out.println("Done");
	}

	private void createInstructorWithCourses(AppDAO appDAO){
		Instructor tempInstructor=new Instructor("pallavi","sahoo","pallavisahoo@gmail.com");
		InstructorDetail tempInstructorDetail=new InstructorDetail("ko.com","dancing");
		tempInstructor.setInstructorDetail(tempInstructorDetail);

		System.err.println("Saving Instructor"+tempInstructor);

		Course tempCourse1=new Course("Math");
		Course tempCourse2=new Course("Science");

		tempInstructor.add(tempCourse1);
		tempInstructor.add(tempCourse2);

		System.out.println("The Courses are "+tempInstructor.getCourses());

		appDAO.save(tempInstructor);
		
		System.out.println("Done");
	}

	private void findInstructorWithCourses(AppDAO appDAO){
		int theId=1;
		System.out.println("Finding instructor id: "+theId);
		Instructor tempInstructor=appDAO.findInstructorById(theId);
		System.out.println("tempInstrcutor: "+tempInstructor);
		System.out.println("Associate courses are + "+tempInstructor.getCourses());
		System.out.println("Done");
	}

	private void findCousesForInstructor(AppDAO appDAO){
		int theId=1;
		System.out.println("Finding instructor id: "+theId);
		Instructor tempInstructor=appDAO.findInstructorById(theId);
		System.out.println("tempInstrcutor: "+tempInstructor);

		List<Course>courses=appDAO.findCoursesByInstructorId(theId);

		tempInstructor.setCourses(courses);
		
		System.out.println("Associate courses are + "+tempInstructor.getCourses());
		System.out.println("Done");
	}

	private void findInstructorWithCoursesJoinFetch(AppDAO appDAO){
		int theId=1;
		System.out.println("Finding instructor id: "+theId);
		Instructor tempInstructor=appDAO.findInstrcutorByIdjoinFetch(theId);
		System.out.println("tempInstrcutor: "+tempInstructor);
		System.out.println("Associate courses are + "+tempInstructor.getCourses());
		System.out.println("Done");
	}

	private void updateInstructor(AppDAO appDAO){
		int theId=1;
		System.out.println("Finding instructor id: "+theId);
		Instructor tempInstructor=appDAO.findInstrcutorByIdjoinFetch(theId);
		tempInstructor.setFirstName("firstName");

		appDAO.update(tempInstructor);

		System.out.println("Done");
	}

	private void updateCourse(AppDAO appDAO){
		int id=10;
		Course tempC=appDAO.findCourseById(id);
		tempC.setTitle("Updated Title");
		appDAO.update(tempC);
	}

	private void deleteInstructorByObject(AppDAO appDAO){
		int id=1;
		appDAO.deleteInstructor(id);
	}

	private void deleteCourseById(AppDAO appDAO){
		int id=10;
		appDAO.deleteCourse(id);
	}

	private void createCourseAndReview(AppDAO appDAO){
		Course tempCourse=new Course("How to be a rich guy");
		tempCourse.addReview(new Review("Book was okeish"));
		tempCourse.addReview(new Review("Book was Good"));
		tempCourse.addReview(new Review("Book was BAD"));

		System.out.println("Saving the course");
		System.out.println(tempCourse);
		System.out.println(tempCourse.getReviews());
		appDAO.save(tempCourse);
	}

	private void retrieveCourseAndReviews(AppDAO appDAO){
		int theId=10;
		Course tempCourse=appDAO.findCourseAndReviewsByCourseId(theId);
		System.out.println(tempCourse);
		System.out.println(tempCourse.getReviews());
	}

	private void deleteCoursesAndReviews(AppDAO appDAO){
		int theId=10;
		System.out.println("Deleteing the course id: "+theId);
		appDAO.deleteCourse(theId);
		System.out.println("Done");
	}

	private void createCourseAndStudent(AppDAO appDAO){
		Course tempCourse1=new Course("Paras is a good boy");

		Student tempStud1=new Student("paras","singh","parascet@gmail.com");
		Student tempStud2=new Student("abhijeet","sahu","abhijeet@gmail.com");
		Student tempStud3=new Student("subham","parida","subham@gmail.com");

		tempCourse1.addStudent(tempStud1);
		tempCourse1.addStudent(tempStud2);
		tempCourse1.addStudent(tempStud3);

		System.out.println("Saving the course "+tempCourse1);
		System.out.println("Associated course "+tempCourse1.getStudents());

		appDAO.save(tempCourse1);

		System.out.println("Done");
	}

	private void findCourseAndStudent(AppDAO appDAO){
		int theId=10;
		Course tempCourse=appDAO.findCourseAndStudentByCourseId(theId);
		System.out.println("loaded courses "+tempCourse);
		System.out.println("Students "+tempCourse.getStudents());
	}

	private void findStudentsAndCourses(AppDAO appDAO){
		int theId=1;
		Student tempCourse=appDAO.findStudentAndCourseByStudentId(theId);
		System.out.println("loaded Students "+tempCourse);
		System.out.println("associated courses "+tempCourse.getCourse());
	}

	private void addMoreCoursesForStudent(AppDAO appDAO){
		int theId=2;
		Student tempStud=appDAO.findStudentAndCourseByStudentId(theId);

		tempStud.addCourse(new Course("paras is a bad boyyyy"));
		tempStud.addCourse(new Course("paras is a okieshhh boyyyy"));

		System.out.println("Saving the student "+tempStud);
		System.out.println("Associated Student "+tempStud.getCourse());

		appDAO.update(tempStud);

		System.out.println("Done");
	}

	private void deleteStudentByid(AppDAO appDAO){
		int theId=1;
		appDAO.deleteStudent(theId);
	}
}
