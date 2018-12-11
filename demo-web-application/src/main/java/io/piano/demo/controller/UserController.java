package io.piano.demo.controller;

import io.piano.demo.dto.UserDto;
import io.piano.demo.model.User;
import io.piano.demo.security.JwtTokenProvider;
import io.piano.demo.service.UserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;
    public UserController(UserService userDetailsService, ModelMapper modelMapper,
            JwtTokenProvider jwtTokenProvider) {
        this.userService = userDetailsService;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/auth")
    public ResponseEntity getUserDetails(@RequestBody UserDto userDto) {
        final Map<String, Object> model = new HashMap<>();
        String token = jwtTokenProvider.createToken(userDto.getUsername(), Collections.emptyList());
        model.put("Authorization", token);
        return ResponseEntity.ok(model);
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody UserDto userDto) {
        userService.register(modelMapper.map(userDto, User.class));
        return null;
    }

    @GetMapping("/logout")
    public UserDto logout() {
        return null;
    }
}