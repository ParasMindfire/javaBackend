package com.AOP.AOP.dao;

import java.util.List;

import com.AOP.AOP.Account;

public interface AccoutDAO {
    List<Account> findAccount(boolean tripWire);
    List<Account> findAccount();
    void addAccount(Account theAccount,boolean flag);
    boolean addSomething();
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String email);
}
