package org.example.githubrepofetcher.controller.rest.exception.handler;

import org.example.githubrepofetcher.model.dto.ErrorResponseDto;
import org.example.githubrepofetcher.service.exception.GitHubUserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GitHubUserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleGitHubUserNotFoundException(GitHubUserNotFoundException ex) {
        return ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }
}
