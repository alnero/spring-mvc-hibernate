package com.example.project.config;

import com.example.project.service.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
}
