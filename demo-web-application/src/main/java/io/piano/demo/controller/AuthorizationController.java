package io.piano.demo.controller;

import static io.piano.demo.utils.Constants.MAIN_PAGE;

import io.piano.demo.dto.UserDto;
import io.piano.demo.model.User;
import io.piano.demo.security.InMemoryTokenStore;
import io.piano.demo.security.JwtTokenProvider;
import io.piano.demo.service.UserService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${security.jwt.token.header-name:Authorization}")
    private String tokenHeaderName;
    @Value("${security.jwt.token.header-prefix:Bearer }")
    private String tokenHeaderPrefix;

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final InMemoryTokenStore tokenStore;

    public AuthorizationController(UserService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager, ModelMapper modelMapper,
            InMemoryTokenStore tokenStore) {
        this.userService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.tokenStore = tokenStore;
    }

    @PostMapping(value = "/register")
    public String register(@Valid @ModelAttribute("registeringUser") UserDto registeringUser,
            BindingResult bindingResult, HttpServletResponse response, Model model) {
        if (bindingResult.hasErrors()) {
            return MAIN_PAGE;
        }
        User user = mapDtoToModel(registeringUser);
        userService.register(user);

        final String jwt = authenticateUser(user, response);
        model.addAttribute("jwt", jwt);
        return MAIN_PAGE;
    }

    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute("loggingInUser") UserDto loggingInUser,
            BindingResult bindingResult, HttpServletResponse response, Model model) {
        if (bindingResult.hasErrors()) {
            return MAIN_PAGE;
        }

        String jwt = authenticateUser(mapDtoToModel(loggingInUser), response);
        model.addAttribute("jwt", jwt);
        return MAIN_PAGE;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    @GetMapping("/auth")
    public void getUserDetails(@RequestParam String redirectUrl, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        setJwtResponseHeader(jwtTokenProvider.resolveToken(request), response);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login")
    public String openLoginPage() {
        return MAIN_PAGE;
    }

    @GetMapping("/register")
    public String openRegistrationPage() {
        return MAIN_PAGE;
    }

    @GetMapping("/authError")
    public String returnAuthErrorPage() {
        return MAIN_PAGE;
    }

    private String authenticateUser(User user, HttpServletResponse response) {
        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtTokenProvider.createToken(authentication);
        tokenStore.authorizeToken(jwtToken);
        setJwtResponseHeader(jwtToken, response);

        return jwtToken;
    }

    private void setJwtResponseHeader(String jwt, HttpServletResponse response) {
        String tokenHeader = String.format("%s %s", tokenHeaderPrefix, jwt);
        response.setHeader(tokenHeaderName, tokenHeader);
    }

    private User mapDtoToModel(
            @ModelAttribute @Valid UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

}
