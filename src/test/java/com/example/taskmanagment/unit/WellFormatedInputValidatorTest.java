package com.example.taskmanagment.unit;

import com.example.taskmanagment.exceptions.InvalidQueryParamsException;
import com.example.taskmanagment.validation.validators.WellFormattedInputValidator;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PAGEABLE;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WellFormatedInputValidatorTest {

    private final WellFormattedInputValidator validator = new WellFormattedInputValidator();

    @Test
    void validateTaskPageable() {
        Pageable rightPageable = PageRequest.of(1, 1, Sort.by("status", "header", "status"));
        Pageable wrongPageable = PageRequest.of(1, 1, Sort.by(DUMMY));

        assertAll(
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> validator.validateTaskPageable(wrongPageable),
                        INVALID_PAGEABLE),
                () -> assertThatNoException().isThrownBy(() -> validator.validateTaskPageable(rightPageable))
        );
    }
}
