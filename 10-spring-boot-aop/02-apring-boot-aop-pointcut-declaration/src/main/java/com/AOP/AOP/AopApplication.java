package com.AOP.AOP;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.AOP.AOP.dao.AccoutDAO;
import com.AOP.AOP.dao.MembershipDAO;

@SpringBootApplication
public class AopApplication {

	public static void main(String[] args) {
		SpringApplication.run(AopApplication.class, args);
	}

	@Bean 
	public CommandLineRunner commandLineRunner(AccoutDAO accountDAO,MembershipDAO membershipDAO){
		return _->{
			demoTheBeforeAdvice(accountDAO,membershipDAO);
		};
	}

	private void demoTheBeforeAdvice(AccoutDAO accountDAO,MembershipDAO membershipDAO) {
		System.out.println("Call for Account add");
		accountDAO.addAccount(new Account(),true);
		System.out.println("Call for Account soemthing");
		accountDAO.addSomething();
		System.out.println("Call for Membership ");
		membershipDAO.addAccount();
	}


}
