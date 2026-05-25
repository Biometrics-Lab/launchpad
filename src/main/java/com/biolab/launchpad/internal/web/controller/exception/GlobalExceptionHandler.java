package com.biolab.launchpad.internal.web.controller.exception;

import com.biolab.launchpad.internal.security.exceptions.ConflictException;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.BindErrorUtils;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseDto handleException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseCode.INTERNAL_SERVER_ERROR.getResponseDto();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());

        String errors = Stream.of(
                        BindErrorUtils.resolveAndJoin(ex.getGlobalErrors().stream().sorted(Comparator.comparing(ObjectError::getObjectName)).toList()),
                        BindErrorUtils.resolveAndJoin(ex.getFieldErrors().stream().sorted(Comparator.comparing(FieldError::getField)).toList())
                )
                .map(String::valueOf)
                .filter(StringUtils::hasText)
                .sorted()
                .collect(Collectors.joining(", and "));

        return ResponseCode.VALIDATION_ERROR.getResponseDto(errors);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundByException.class)
    public ResponseDto handleNotFoundByException(NotFoundByException ex) {
        return ResponseDto.builder()
                .status(ResponseCode.NOT_FOUND_BY.getStatus())
                .message(ex.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseDto handleConflictException(ConflictException ex) {
        return ResponseDto.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }
}