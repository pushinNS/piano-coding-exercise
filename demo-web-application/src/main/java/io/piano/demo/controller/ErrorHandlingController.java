package io.piano.demo.controller;

import static io.piano.demo.utils.Constants.ERROR_PAGE;
import static io.piano.demo.utils.Constants.MAIN_PAGE;

import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorHandlingController implements ErrorController {

    @GetMapping("/apiError")
    public String returnApiErrorPage(@RequestParam String error, HttpServletRequest request) {
        request.setAttribute("error", error);
        return MAIN_PAGE;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Optional<Object> statusCode = Optional
                .ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        Optional<Object> message = Optional
                .ofNullable(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

        statusCode.ifPresent(code -> request.setAttribute("statusCode", code));
        message.ifPresent(msg -> request.setAttribute("message", msg));

        return ERROR_PAGE;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
