package com.example.taskmanagment.validation.validators;


import com.example.taskmanagment.exceptions.InvalidQueryParamsException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PAGEABLE;

@Component
public class WellFormattedInputValidator {

    public void validateTaskPageable(Pageable pageable) {
        boolean valid = pageable.getSort().get()
                .map(Sort.Order::getProperty)
                .allMatch(s -> s.equals("header") || s.equals("status") || s.equals("priority"));

        if (!valid)
            throw new InvalidQueryParamsException(INVALID_PAGEABLE);
    }

}
