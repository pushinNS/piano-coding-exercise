package io.piano.demo.controller;

import io.piano.demo.dto.UserDto;
import io.piano.demo.model.User;
import io.piano.demo.security.JwtTokenProvider;
import io.piano.demo.service.UserService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping
public class AuthorizationController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public AuthorizationController(UserService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager, ModelMapper modelMapper) {
        this.userService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/register")
    public String register(@Valid @ModelAttribute("registeringUser") UserDto registeringUser,
            BindingResult bindingResult,
            Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return configuredAuthPage(model, registeringUser);
        }
        User user = mapDtoToModel(registeringUser);
        userService.register(user);

        String jwt = authenticate(user);

        setJwtResponseHeader(jwt, response);
        model.addAttribute("jwt", jwt);

        return configuredAuthPage(model);
    }

    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute("loggingInUser") UserDto loggingInUser,
            BindingResult bindingResult,
            Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return configuredAuthPage(model, loggingInUser);
        }

        String jwt = authenticate(mapDtoToModel(loggingInUser));

        setJwtResponseHeader(jwt, response);
        model.addAttribute("jwt", jwt);

        return configuredAuthPage(model);
    }

    @GetMapping("/auth")
    public void getUserDetails(@RequestParam String redirectUrl, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        setJwtResponseHeader(jwtTokenProvider.resolveToken(request), response);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login")
    public String openLoginPage(Model model) {
        return configuredAuthPage(model);
    }

    @GetMapping("/register")
    public String openRegistrationPage(Model model) {
        return configuredAuthPage(model);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    private String authenticate(User user) {
        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.createToken(authentication);
    }

    private String configuredAuthPage(Model model) {
        UserDto user = new UserDto();
        return configuredAuthPage(model, user);
    }

    private String configuredAuthPage(Model model, UserDto user) {
        //Using different names for entities to distinguish forms
        model.addAttribute("registeringUser", user);
        model.addAttribute("loggingInUser", user);
        return "auth";
    }

    private void setJwtResponseHeader(String jwt, HttpServletResponse response) {
        String tokenHeader = "Bearer " + jwt;
        response.setHeader("Authorization", tokenHeader);
    }

    private User mapDtoToModel(
            @ModelAttribute @Valid UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

}
