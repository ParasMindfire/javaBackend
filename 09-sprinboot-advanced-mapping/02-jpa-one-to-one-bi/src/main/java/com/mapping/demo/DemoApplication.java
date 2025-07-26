package com.mapping.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mapping.demo.dao.AppDAO;
import com.mapping.demo.entity.Instructor;
import com.mapping.demo.entity.InstructorDetail;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner(AppDAO appDAO){
		return _->{
			// createConstructor(appDAO);
			// findInstructor(appDAO);
			// deleteInstructor(appDAO);
			// findInstructorDetail(appDAO);
			deleteInstructorDetail(appDAO);
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

}
