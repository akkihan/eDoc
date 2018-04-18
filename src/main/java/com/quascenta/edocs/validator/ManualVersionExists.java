package com.quascenta.edocs.validator;

import com.quascenta.edocs.validator.ManualVersionExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ak on 3/5/2018.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ManualVersionExistsValidator.class)
public @interface ManualVersionExists {
    String message() default "In manual versioning version number can not be empty!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


