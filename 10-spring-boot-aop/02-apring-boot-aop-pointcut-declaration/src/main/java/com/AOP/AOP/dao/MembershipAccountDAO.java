package com.AOP.AOP.dao;

import org.springframework.stereotype.Repository;

@Repository
public class MembershipAccountDAO implements MembershipDAO {

    @Override
    public void addAccount() {
        System.out.println(getClass()+" I am doing something here in membership account");
    }

}
