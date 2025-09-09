package com.biolab.launchpad.internal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity()
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST  , "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET   , "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT   , "/api/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")
                        .anyRequest().authenticated()                 )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
