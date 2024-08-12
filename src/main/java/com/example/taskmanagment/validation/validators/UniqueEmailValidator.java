package com.example.taskmanagment.validation.validators;

import com.example.taskmanagment.repositories.UserRepository;
import com.example.taskmanagment.validation.annotations.Unique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<Unique, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByEmail(email).isEmpty();
    }
}
