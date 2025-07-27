package com.AOP.AOP;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.AOP.AOP.dao.AccoutDAO;
import com.AOP.AOP.dao.MembershipDAO;
import com.AOP.AOP.service.TrafficFortuneService;

@SpringBootApplication
public class AopApplication {

	public static void main(String[] args) {
		SpringApplication.run(AopApplication.class, args);
	}

	@Bean 
	public CommandLineRunner commandLineRunner(AccoutDAO accountDAO,
											   MembershipDAO membershipDAO,
											   TrafficFortuneService trafficFortuneService){
		return _->{
			// demoTheBeforeAdvice(accountDAO,membershipDAO);
			// demoTheAfterReturningAdvice(accountDAO);
			// demoTheAfterThrowingAdvice(accountDAO);
			// demoTheAfterAdvice(accountDAO);
			// dmeoTheAroundAdvice(trafficFortuneService);
			// demoTheAroundDeviceHandleException(trafficFortuneService);
			demoTheAroundDeviceRethrowException(trafficFortuneService);
		};
	}

	private void demoTheBeforeAdvice(AccoutDAO accountDAO,MembershipDAO membershipDAO) {
		// System.out.println("Call for Account add");
		Account tempAcc=new Account();
		tempAcc.setName("ok");
		tempAcc.setLevel("1");
		accountDAO.addAccount(tempAcc,true);

		accountDAO.setName("Paras");
		String name=accountDAO.getName();
		System.out.println("Name is "+name);

		System.out.println("Call for Account soemthing");
		accountDAO.addSomething();

		System.out.println("Call for Membership ");
		membershipDAO.addAccount();
	}

	private void demoTheAfterReturningAdvice(AccoutDAO theAccount){
		List<Account>theAccounts=theAccount.findAccount();
		
		System.out.println("\n \n Main Program : demoTheAfterReturningAdvice");
		System.out.println("--------------------");
		System.out.println(theAccounts);
		System.out.println("\n");
	}

	private void demoTheAfterThrowingAdvice(AccoutDAO theAccountDAO){
		List<Account>theAccounts=null;
		try{
			boolean tripWire=true;
			theAccounts=theAccountDAO.findAccount(tripWire);
		}catch(Exception exc){
			System.out.println("CAught the exception "+exc);
		}
		
		System.out.println("\n \n Main Program : demoTheAfterThrowingAdvice");
		System.out.println("--------------------");
		System.out.println(theAccounts);
		System.out.println("\n");
	}

	private void demoTheAfterAdvice(AccoutDAO accountDAO){
		List<Account>theAccounts=null;
		try{
			boolean tripWire=true;
			theAccounts=accountDAO.findAccount(tripWire);
		}catch(Exception exc){
			System.out.println("CAught the exception "+exc);
		}
		
		System.out.println("\n \n Main Program : demoTheAfterThrowingAdvice");
		System.out.println("--------------------");
		System.out.println(theAccounts);
		System.out.println("\n");
	}

	private void dmeoTheAroundAdvice(TrafficFortuneService trafficFortuneService){
		System.out.println("\nMain: Program : demo Around device");
		System.out.println("Calling get fortune ");
		String data=trafficFortuneService.getFortune();
		System.out.println("\n My Fortune is "+data);
		System.out.println("Finsihed");
	}

	private void demoTheAroundDeviceHandleException(TrafficFortuneService trafficFortuneService){
		System.out.println("\nMain: Program : demo Around device");
		System.out.println("Calling get fortune ");
		boolean tripWire=true;
		String data=trafficFortuneService.getFortune(tripWire);
		System.out.println("\n My Fortune is "+data);
		System.out.println("Finsihed");
	}

	private void demoTheAroundDeviceRethrowException(TrafficFortuneService trafficFortuneService){
		System.out.println("\nMain: Program : demo Around device");
		System.out.println("Calling get fortune ");
		boolean tripWire=true;
		String data=null;
		try {
			data=trafficFortuneService.getFortune(tripWire);
		} catch (Exception e) {
			data="Rethrown exception is handled successfullyy";
		}
		System.out.println("\n My Fortune is "+data);
		System.out.println("Finsihed");
	}
}