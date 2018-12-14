package io.piano.demo.controller;

import static io.piano.demo.utils.Constants.MAIN_PAGE;

import io.piano.demo.dto.UserDto;
import io.piano.demo.model.User;
import io.piano.demo.security.DatabaseTokenStore;
import io.piano.demo.security.JwtTokenProvider;
import io.piano.demo.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping
public class AuthorizationController {

    @Value("${security.jwt.token.header-name:Authorization}")
    private String tokenHeaderName;
    @Value("${security.jwt.token.header-prefix:Bearer }")
    private String tokenHeaderPrefix;

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final DatabaseTokenStore tokenStore;

    public AuthorizationController(UserService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager, ModelMapper modelMapper,
            DatabaseTokenStore tokenStore) {
        this.userService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.tokenStore = tokenStore;
    }

    @PostMapping(value = "/register")
    public String register(@Valid @ModelAttribute("registeringUser") UserDto registeringUser,
            BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return MAIN_PAGE;
        }
        User user = mapDtoToModel(registeringUser);
        userService.register(user);
        authenticateUser(user, response, request);

        return MAIN_PAGE;
    }

    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute("loggingInUser") UserDto loggingInUser,
            BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return MAIN_PAGE;
        }
        authenticateUser(mapDtoToModel(loggingInUser), response, request);

        return MAIN_PAGE;
    }

    @GetMapping("/auth")
    public String getUserDetails(HttpServletRequest request, HttpServletResponse response) {
        final String jwt = jwtTokenProvider.resolveToken(request);
        setJwtResponseHeader(jwt, response);
        return MAIN_PAGE;
    }

    @GetMapping("/login")
    public String openLoginPage() {
        return MAIN_PAGE;
    }

    @GetMapping("/register")
    public String openRegistrationPage() {
        return MAIN_PAGE;
    }

    private void authenticateUser(User user, HttpServletResponse response,
            HttpServletRequest request) {
        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtTokenProvider.createToken(authentication);

        tokenStore.storeToken(jwtToken);

        setJwtResponseHeader(jwtToken, response);

        request.setAttribute("jwt", jwtToken);
    }

    private void setJwtResponseHeader(String jwt, HttpServletResponse response) {
        String tokenHeader = String.format("%s %s", tokenHeaderPrefix, jwt);
        response.setHeader(tokenHeaderName, tokenHeader);
    }

    private User mapDtoToModel(@ModelAttribute @Valid UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
