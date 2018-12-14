package io.piano.demo.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public class JwtTokenProviderTest {

    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_HEADER_VALUE = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZXMiOltdLCJpYXQiOjE1NDQ4MjU1MjYsImV4cCI6MTYzNDgyNTUyNn0.Cmvwe9WE6Ur2kWfTWMcHpyQzHh3J2ULH1fvvX0dT2RU";
    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZXMiOltdLCJpYXQiOjE1NDQ4MjUzOTAsImV4cCI6MTU0NDgyNTM5MX0.ZSXdo_D8ehCvCxE2_fZr-07BUCA_2f7PJbvmdT4hAM0";
    private static final String NOT_EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZXMiOltdLCJpYXQiOjE1NDQ4MjU1MjYsImV4cCI6MTYzNDgyNTUyNn0.Cmvwe9WE6Ur2kWfTWMcHpyQzHh3J2ULH1fvvX0dT2RU";
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Authentication authentication;

    private JwtTokenProvider underTests = new JwtTokenProvider(HEADER_NAME,
            "Bearer", "SECRET_KEY", 1000000000, userDetailsService);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    public void testResolvedTokenIsValid() {
        when(request.getHeader(HEADER_NAME)).thenReturn(TOKEN_HEADER_VALUE);
        final String actual = underTests.resolveToken(request);
        assertEquals(NOT_EXPIRED_TOKEN, actual);
    }

    @Test
    public void testNullResponseIfNoToken() {
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        final String actual = underTests.resolveToken(request);
        assertNull(actual);
    }

    @Test
    public void testTokenIsExpired() {
        final boolean notExpired = underTests.notExpired(EXPIRED_TOKEN);
        assertFalse(notExpired);
    }

    @Test
    public void testTokenIsNotExpired() {
        final boolean notExpired = underTests.notExpired(NOT_EXPIRED_TOKEN);
        assertTrue(notExpired);
    }

    @Test
    public void testTokenIsCreated() {
        when(authentication.getPrincipal())
                .thenReturn(new User("test", "test", Collections.emptyList()));

        final String token = underTests.createToken(authentication);
        assertNotNull(token);
    }
}