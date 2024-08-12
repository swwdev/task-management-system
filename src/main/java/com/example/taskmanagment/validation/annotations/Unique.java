package com.example.taskmanagment.validation.annotations;


import com.example.taskmanagment.validation.validators.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.example.taskmanagment.utils.ResponseUtils.EMAIL_DUPLICATE;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {UniqueEmailValidator.class})
public @interface Unique {

    String message() default EMAIL_DUPLICATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
