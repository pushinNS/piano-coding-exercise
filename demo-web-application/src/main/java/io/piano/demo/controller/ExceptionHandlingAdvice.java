package io.piano.demo.controller;

import io.piano.demo.dto.UserDto;
import io.piano.demo.exception.UnauthorizedException;
import io.piano.demo.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingAdvice {

    private final static String AUTH_PAGE = "auth";

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(Exception e, Model model) {
        return returnBasePageWithError(e, model);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(Exception e, Model model) {
        return returnBasePageWithError(e, model);
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthException(Exception e, Model model) {
        return returnBasePageWithError(e, model);
    }

    private String returnBasePageWithError(Exception e, Model model) {
        final String message = e.getMessage();
        log.warn(message);
        model.addAttribute("error", message);
        model.addAttribute("loggingInUser", new UserDto());
        model.addAttribute("registeringUser", new UserDto());
        return AUTH_PAGE;
    }
}
