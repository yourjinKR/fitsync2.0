package com.fitsync.global.error;

import java.text.MessageFormat;

public enum ErrorMessage {
    ERROR("기본 에러"),
    EXERCISE_DUPLICATE_NAME("이미 동일한 이름의 운동이 존재합니다."),
    EXERCISE_NOT_FOUND("해당 ID와 일치하는 {0} 정보를 찾지 못했습니다. (id: {1})");
    ;

    private final String message;
    private static final String PREFIX = "[ERROR]";

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return MessageFormat.format("{0} {1}", PREFIX, message);
    }

    public String format(Object... args) {
        return MessageFormat.format("{0} {1}", PREFIX, MessageFormat.format(message, args));
    }
}
