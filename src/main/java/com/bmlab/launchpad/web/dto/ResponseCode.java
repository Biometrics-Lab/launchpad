package com.bmlab.launchpad.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    //OK
    OK(HttpStatus.OK.value(), "Success"),

    // General errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "Bad Request"),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Not Found"),

    // Specific errors
    NOT_FOUND_BY(HttpStatus.NOT_FOUND.value(), "%s Not Found by id: %d"),
    ALREADY_EXISTS(CONFLICT.value(), "%s Already Exists"),
    VALIDATION_ERROR(UNPROCESSABLE_ENTITY.value(), "Validation failed: %s");

    private final int status;
    private final String message;

    public ResponseDto getResponseDto(Object... args) {
        return ResponseDto.builder()
                .status(status)
                .message(message.formatted(args))
                .build();
    }

    public ResponseDto getResponseDto() {
        return getResponseDto(this.message);
    }
}
