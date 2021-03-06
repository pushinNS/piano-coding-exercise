package io.piano.demo.interceptor;

import io.piano.demo.dto.UserDto;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor manages models: it adds empty UserDto objects to model to use in forms
 */
public class ModelAttributesManagingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView mav) throws Exception {
        if (isAnonymous()) {
            addValidationLinkedObjectsToModel(mav);
        }
        super.postHandle(request, response, handler, mav);
    }

    private void addValidationLinkedObjectsToModel(ModelAndView mav) {
        final UserDto userDto = new UserDto();
        mav.addObject("loggingInUser", userDto);
        mav.addObject("registeringUser", userDto);
    }

    private boolean isAnonymous() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                .equals("anonymousUser");
    }
}
