package com.fitsync.global.error;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorMessageTest {
    @Test
    @DisplayName("에러 메세지 테스트")
    void printErrorMessage() {
        System.out.println(ErrorMessage.EXERCISE_DUPLICATE_NAME.getMessage());
    }

    @Test
    @DisplayName("에러 메세지 ID 넣어서 테스트")
    void printErrorMessageWithId() {
        Long id = 1L;
        System.out.println(ErrorMessage.EXERCISE_NOT_FOUND.format("exercise", id));
    }

}