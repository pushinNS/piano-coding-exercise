package io.piano.demo.utils;

import io.piano.demo.dto.UserDto;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public class AuthUtils {

    public static void addFormObjectsToModel(ModelAndView modelAndView) {
        final UserDto userDto = new UserDto();
        modelAndView.addObject("loggingInUser", userDto);
        modelAndView.addObject("registeringUser", userDto);
    }

    public static void addFormObjectsToModel(Model model) {
        final UserDto userDto = new UserDto();
        model.addAttribute("loggingInUser", userDto);
        model.addAttribute("registeringUser", userDto);
    }
}
