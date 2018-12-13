package io.piano.demo.config;

import io.piano.demo.interceptor.ModelAttributesManagingInterceptor;
import io.piano.demo.interceptor.SessionStorageManagingInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfiguration implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ModelAttributesManagingInterceptor())
                .addPathPatterns("/login", "/register", "/authError");
        registry.addInterceptor(new SessionStorageManagingInterceptor())
                .addPathPatterns("/login", "/register");
    }
}
