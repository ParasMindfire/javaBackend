package com.AOP.AOP.dao;

import org.springframework.stereotype.Repository;

import com.AOP.AOP.Account;

@Repository
public class AccountDAOimpl implements AccoutDAO{

    @Override
    public void addAccount(Account theAccount,boolean flag) {
        System.out.println(getClass()+" : step 1  ");
    }

    @Override
    public boolean addSomething() {
        System.out.println(getClass()+" : step 2 ");
        return false;
    }
    
}
