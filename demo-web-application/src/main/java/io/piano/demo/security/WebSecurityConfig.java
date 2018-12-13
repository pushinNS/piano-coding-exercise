package io.piano.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.jwt.token.header-name:Authorization}")
    private String tokenHeaderName;
    @Value("${security.jwt.token.header-prefix:Bearer}")
    private String tokenHeaderPrefix;

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationEntryPoint unauthorizedHandler;
    private final JwtTokenAuthenticationFilter filter;
    private final InMemoryTokenStore tokenStore;

    public WebSecurityConfig(UserDetailsService userDetailsService,
            BCryptPasswordEncoder passwordEncoder,
            AuthenticationEntryPoint unauthorizedHandler,
            JwtTokenAuthenticationFilter filter,
            InMemoryTokenStore tokenStore) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.unauthorizedHandler = unauthorizedHandler;
        this.filter = filter;
        this.tokenStore = tokenStore;
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LogoutHandler logoutHandler(){
        return (httpServletRequest, httpServletResponse, authentication) ->
        {
            final String jwtHeader = httpServletRequest.getHeader(tokenHeaderName);
            String jwt = jwtHeader.substring(tokenHeaderPrefix.length() + 1);
            tokenStore.revokeToken(jwt);
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .headers().frameOptions().disable()
                    .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .authorizeRequests()
                .antMatchers("/register", "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/css/**").permitAll()
                .antMatchers("/auth").authenticated()
                    .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .addLogoutHandler(logoutHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}

