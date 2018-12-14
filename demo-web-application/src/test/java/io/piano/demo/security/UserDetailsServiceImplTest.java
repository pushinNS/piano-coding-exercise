package io.piano.demo.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import io.piano.demo.repository.UserRepository;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

    private static final String USERNAME = "test";
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl underTests;

    @Test
    public void testUserIsLoaded() {

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(new io.piano.demo.model.User(1L, USERNAME, "pass"));

        User expected = new User(USERNAME, "pass", Collections.emptyList());
        final UserDetails actual = underTests.loadUserByUsername(USERNAME);

        assertEquals(expected, actual);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testExceptionIsThrown() {
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(null);
        underTests.loadUserByUsername(USERNAME);
    }
}