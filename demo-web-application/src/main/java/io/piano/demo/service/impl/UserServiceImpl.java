package io.piano.demo.service.impl;

import io.piano.demo.exception.UserAlreadyExistsException;
import io.piano.demo.model.User;
import io.piano.demo.repository.UserRepository;
import io.piano.demo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserServiceImpl(BCryptPasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public void register(User user) {
        final User byUsername = userRepository.findByUsername(user.getUsername());
        if (byUsername == null) {
            User encoded = encodeUserPassword(user);
            userRepository.save(encoded);
            return;
        }
        throw new UserAlreadyExistsException("User already exists");
    }

    private User encodeUserPassword(User user) {
        final String rawPass = user.getPassword();
        final String username = user.getUsername();
        return new User(username, encoder.encode(rawPass));
    }
}
