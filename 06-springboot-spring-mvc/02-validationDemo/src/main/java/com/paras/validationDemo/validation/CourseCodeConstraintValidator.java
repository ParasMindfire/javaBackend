package com.paras.validationDemo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CourseCodeConstraintValidator implements ConstraintValidator<CourceCode,String> {

    private String coursePrefix;

    @Override
    public void initialize(CourceCode theCourseCode) {
        coursePrefix=theCourseCode.value();
    }

    @Override
    public boolean isValid(String theCode, ConstraintValidatorContext theConstraintValidatorContext) {
        boolean result;
        if(theCode!=null){
            result=theCode.startsWith(coursePrefix);
        }else{
            result=true;
        }
        return result;
    }

}
