package io.piano.demo.interceptor;

import static java.util.Objects.nonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor manages session objects
 *
 * @preHandle for removing error messages stored in session
 */
public class SessionStorageManagingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        final HttpSession session = request.getSession(false);
        if (nonNull(session)) {
            session.removeAttribute("error");
        }
        return super.preHandle(request, response, handler);
    }
}
