package io.piano.demo.security;

import static java.util.Objects.nonNull;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class JwtTokenAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final DatabaseTokenStore tokenStore;

    public JwtTokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
            DatabaseTokenStore tokenStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenStore = tokenStore;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        putRedirectUrlToSessionIfAuthRequested(req);

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        if (token != null && jwtTokenProvider.notExpired(token) && tokenStore.isStored(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(req, res);
    }

    private void putRedirectUrlToSessionIfAuthRequested(ServletRequest req) {
        final String redirectUrl = req.getParameter("redirectUrl");
        final String uri = ((HttpServletRequest) req).getRequestURI();
        final HttpSession session = ((HttpServletRequest) req).getSession();

        if (authPageRequested(uri) && redirectUrlPresented(redirectUrl) && nonNull(session)) {
            session.setAttribute("redirectUrl", redirectUrl);
        }
    }

    private boolean redirectUrlPresented(String redirectUrl) {
        return !StringUtils.isEmpty(redirectUrl);
    }

    private boolean authPageRequested(String uri) {
        return "/auth".equals(uri);
    }
}
