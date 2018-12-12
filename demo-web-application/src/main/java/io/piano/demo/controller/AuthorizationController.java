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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @PostMapping(value = "/register")
    public String register(@Valid @ModelAttribute UserDto userDto,
            BindingResult bindingResult, HttpServletResponse response) {
        final String password = userDto.getPassword();
        final String username = userDto.getUsername();

        userService.register(new User(username, password));

        return "redirect:/login";
    }

    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute UserDto userDto, HttpServletResponse response,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

        }
        String jwt = getJwt(userDto.getUsername(), userDto.getPassword());
        model.addAttribute("jwt", jwt);
        setJwtResponseHeader(jwt, response);
        return returnMainPage(model);
    }

    @GetMapping("/auth")
    public void getUserDetails(@RequestParam String redirectUrl, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        setJwtResponseHeader(jwtTokenProvider.resolveToken(request), response);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login")
    public String openLoginPage(Model model) {
        return returnMainPage(model);
    }

    @GetMapping("/register")
    public String openRegistrationPage(Model model) {
        return returnMainPage(model);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    private String returnMainPage(Model model) {
        UserDto personForm = new UserDto();
        model.addAttribute("user", personForm);
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