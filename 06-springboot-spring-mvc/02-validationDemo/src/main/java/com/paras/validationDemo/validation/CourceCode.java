package com.paras.validationDemo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Constraint(validatedBy=CourseCodeConstraintValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CourceCode {

    public String value() default "LUV";

    public String message() default "must start with LUV";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
