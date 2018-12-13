package io.piano.demo.controller;

import static io.piano.demo.utils.Constants.MAIN_PAGE;

import io.piano.demo.exception.UserAlreadyExistsException;
import io.piano.demo.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingAdvice {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(Exception e, Model model) {
        return returnBasePageWithError(e, model);
    }

    private String returnBasePageWithError(Exception e, Model model) {
        final String message = e.getMessage();
        log.warn(message);

        AuthUtils.addFormObjectsToModel(model);
        model.addAttribute("error", message);
        return MAIN_PAGE;
    }
}
