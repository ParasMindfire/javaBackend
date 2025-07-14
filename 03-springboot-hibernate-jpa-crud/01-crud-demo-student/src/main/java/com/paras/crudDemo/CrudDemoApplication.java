package com.paras.crudDemo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.paras.crudDemo.DAO.StudentDAO;
import com.paras.crudDemo.Entity.Student;

@SpringBootApplication
public class CrudDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(StudentDAO studentDAO){
		return _ -> {
			createMultipleStudent(studentDAO);
			// readStudent(studentDAO);
			// queryStudent(studentDAO);
			// queryByLname(studentDAO);
			// updateStudent(studentDAO);
			// update(studentDAO);
			// dleteStudent(studentDAO);
			// dleteAllStudent(studentDAO);
		};
	}

	private void createStudent(StudentDAO studentDAO) {
		System.out.println("Creating new Student object...");
		Student tempStudent=new Student("Paras","singh","parascet@gmail.com");

		System.out.println("Saving the student DAO..");
		studentDAO.save(tempStudent);

		System.out.println("Saved Student , generated id "+tempStudent.getId());

	}


	private void createMultipleStudent(StudentDAO studentDAO) {
		System.out.println("Creating new Student object...");
		Student tempStudent=new Student("Paras","singh","parascet@gmail.com");

		System.out.println("Creating new Student object...");
		Student tempStudent2=new Student("Paras2","singh2","parasce2t@gmail.com");

		System.out.println("Creating new Student object...");
		Student tempStudent3=new Student("Paras3","singh3","parascet3@gmail.com");

		System.out.println("Creating new Student object...");
		Student tempStudent4=new Student("Paras4","singh4","parascet4@gmail.com");

		System.out.println("Saving the student DAO..");
		studentDAO.save(tempStudent);
		studentDAO.save(tempStudent2);
		studentDAO.save(tempStudent3);
		studentDAO.save(tempStudent4);

		System.out.println("Saved Student , generated id "+tempStudent.getId());
		System.out.println("Saved Student , generated id "+tempStudent2.getId());
		System.out.println("Saved Student , generated id "+tempStudent3.getId());
		System.out.println("Saved Student , generated id "+tempStudent4.getId());

	}

	private void readStudent(StudentDAO studentDAO){
		System.out.println("Creating read Student object...");
		Student tempStudent=new Student("readStudent","read","read@gmail.com");

		System.out.println("saving read Student object...");
		studentDAO.save(tempStudent);

		Integer id=tempStudent.getId();
		System.out.println("Read Student , generated id "+id);

		System.out.println("Retriving read Student object...");
		Student currStudent=studentDAO.findById(id);

		System.err.println("currentStudent read is"+currStudent);

	}


	private void queryStudent(StudentDAO studentDAO){
		System.out.println("Retiving all the students ");

		List<Student>studs=studentDAO.findByAll();

		System.out.println("Listing all the students ");

		for(Student s: studs){
			System.out.println(s);
		}
	}

	private void queryByLname(StudentDAO studentDAO){
		System.out.println("Retiving all the students by last name ");
		List<Student>studs=studentDAO.findByLastName("singh");

		System.out.println("Listing all the students last name");

		for(Student s: studs){
			System.out.println(s);
		}
	}

	// private void updateStudent(StudentDAO studentDAO){
	// 	System.out.println("Updateing student");
	// 	Student updatedStud=studentDAO.updateStudent("read@gmail.com", "updatedmail@gmai.com");
	// 	System.out.println(updatedStud);
	// }

	private void update(StudentDAO studentDAO){
		int studentID=1;
		System.out.println("Getting student with id: "+studentID);
		Student myStudent=studentDAO.findById(studentID);
		System.out.println("Updating student with id: "+studentID);
		myStudent.setFirstName("Sobbyy");
		studentDAO.update(myStudent);
		System.out.println("Updated student "+myStudent);
	}

	private void dleteStudent(StudentDAO studentDAO){
		int studentID=2;
		System.out.println("Deleting the student with id "+studentID);

		studentDAO.dlete(studentID);
	}	

    private void dleteAllStudent(StudentDAO studentDAO) {
        System.out.println("Deleteing all the sudents");
		int numb=studentDAO.dleteAll();
		System.out.println("Deleted students count are "+numb);
    }

}
