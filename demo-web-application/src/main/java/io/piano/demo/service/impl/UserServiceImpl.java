package io.piano.demo.service.impl;

import io.piano.demo.model.User;
import io.piano.demo.repository.UserRepository;
import io.piano.demo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserServiceImpl(BCryptPasswordEncoder encoder,
            UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public void register(User user) {
        User encoded = withEncodedPassword(user);
        userRepository.save(encoded);
    }

    private User withEncodedPassword(User user) {
        String rawPass = user.getPassword();
        user.setPassword(encoder.encode(rawPass));
        return user;
    }
}