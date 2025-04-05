package org.example.githubrepofetcher.controller.rest.exception.handler;

import org.example.githubrepofetcher.model.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleResponseStatusException(ResponseStatusException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(ex.getStatusCode().value())
                .message(ex.getReason())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }
}
