package com.AOP.AOP.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.AOP.AOP.Account;

@Repository
public class AccountDAOimpl implements AccoutDAO{

    private String name;
    private String email;

    @Override
    public void addAccount(Account theAccount,boolean flag) {
        System.out.println(getClass()+" : step 1  ");
    }

    @Override
    public boolean addSomething() {
        System.out.println(getClass()+" : step 2 ");
        return false;
    }

    public String getName() {
        System.out.println(getClass()+" : in getName()");
        return this.name;
    }

    public void setName(String name) {
        System.out.println(getClass()+" : in setName()");
        this.name = name;
    }

    public String getEmail() {
        System.out.println(getClass()+" : in getEmail()");
        return this.email;
    }

    public void setEmail(String email) {
        System.out.println(getClass()+" : in setEmail()");
        this.email = email;
    }

    @Override
    public List<Account> findAccount() {
        return findAccount(false);
    }

    @Override
    public List<Account> findAccount(boolean tripWire) {

        if(tripWire==true){
            throw new RuntimeException("No Soup for youuu");
        }

        List<Account>myAccounts=new ArrayList<>();

        Account acc1=new Account("john","1");
        Account acc2=new Account("Paras","2");
        Account acc3=new Account("hauu","3");

        myAccounts.add(acc1);
        myAccounts.add(acc2);
        myAccounts.add(acc3);

        return myAccounts;
    }
    
}
