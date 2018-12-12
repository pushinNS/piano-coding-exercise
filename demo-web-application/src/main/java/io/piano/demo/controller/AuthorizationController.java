package io.piano.demo.controller;

import io.piano.demo.dto.UserDto;
import io.piano.demo.model.User;
import io.piano.demo.security.JwtTokenProvider;
import io.piano.demo.service.UserService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping
public class AuthorizationController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final static String AUTH_PAGE = "auth";

    public AuthorizationController(UserService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager) {
        this.userService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login")
    public String login(@Valid @RequestBody UserDto userDto) {

        String jwt = getJwt(userDto.getUsername(), userDto.getPassword());

        return "forward:/auth";
    }

    @GetMapping("/auth")
    @PostMapping("/auth")
    public void getUserDetails(@RequestParam String redirectUrl, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        setJwtResponseHeader(jwtTokenProvider.resolveToken(request), response);
        request.getRequestDispatcher(redirectUrl).forward(request, response);
    }

    @PostMapping(value = "/register")
    public String register(@Valid @RequestBody UserDto dto,
            HttpServletResponse response) {
        final String password = dto.getPassword();
        final String username = dto.getUsername();

        userService.register(new User(username, password));

        final String jwt = getJwt(username, password);
        setJwtResponseHeader(jwt, response);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String openLoginPage() {
        return AUTH_PAGE;
    }

    @GetMapping("/register")
    public String openRegistrationPage() {
        return AUTH_PAGE;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    @GetMapping("error")
    public String handleErrors() {
        return AUTH_PAGE;
    }

    private String getJwt(@RequestParam String username,
            @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.createToken(authentication);
    }

    private void setJwtResponseHeader(String jwt, HttpServletResponse response) {
        String tokenHeader = "Bearer " + jwt;
        response.setHeader("Authorization", tokenHeader);
    }
}