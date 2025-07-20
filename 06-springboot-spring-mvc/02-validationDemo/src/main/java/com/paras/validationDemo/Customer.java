package com.paras.validationDemo;

import com.paras.validationDemo.validation.CourceCode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Customer {
    private String firstName;

    @NotNull(message="is required")
    @Size(min=1,message="is required")
    private String lastName;

    @NotNull(message="is required")
    @Min(value = 0, message = "must be ≥ 0")
    @Max(value = 10, message = "must be ≤ 10")
    private Integer freePasses;

    @Pattern(regexp = "^[a-zA-Z0-9]{5}$", message = "must be exactly 5 letters/digits")
    private String postalCode;

    @CourceCode(value="PARAS",message="Must Start with Paras")
    private String courseCode;
 
    public Customer() {
    }

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getFreePasses() {
        return freePasses;
    }

    public void setFreePasses(Integer freePasses) {
        this.freePasses = freePasses;
    }


    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

}
