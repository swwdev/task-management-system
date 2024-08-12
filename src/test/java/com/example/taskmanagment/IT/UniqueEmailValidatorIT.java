package com.example.taskmanagment.IT;

import com.example.taskmanagment.util.IntegrationTestBase;
import com.example.taskmanagment.validation.validators.UniqueEmailValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class UniqueEmailValidatorIT extends IntegrationTestBase {

    @Autowired
    private UniqueEmailValidator uniqueEmailValidator;

    @Test
    void isValid() {
        assertAll(
                () -> assertFalse(uniqueEmailValidator.isValid(EMAIL, null)),
                () -> assertTrue(uniqueEmailValidator.isValid(DUMMY, null))
        );
    }
}
