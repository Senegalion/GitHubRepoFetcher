package org.example.githubrepofetcher.api.controller.rest.exception;

import org.example.githubrepofetcher.domain.model.dto.api.ErrorResponseDto;
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
