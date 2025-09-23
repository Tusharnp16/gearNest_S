package com.example.gearnest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Autowired
        private CustomAuthenticationSuccessHandler successHandler;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // Allow public access to homepage, contact page, static resources, etc.
                                                .requestMatchers(
                                                                "/", // Permit homepage
                                                                "/user/dashboard",
                                                                "/dashboard", // Permit homepage alias
                                                                "/contact", // Permit contact page
                                                                "/about", // Permit about page alias
                                                                "/login",
                                                                "/css/**",
                                                                "/javascript/**",
                                                                "/images/**",
                                                                "/user-register",
                                                                "/garage-register",
                                                                "/api/**",
                                                                "/reset-password",
                                                                "/reset-password-form")
                                                .permitAll()

                                                // Secure your application based on role-specific URL prefixes
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/garage/**").hasRole("GARAGE")
                                                .requestMatchers("/user/**").hasRole("USER") // All user pages are now
                                                                                             // under /user/

                                                // All other requests must be authenticated
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login-user")
                                                .successHandler(successHandler)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .permitAll());

                return http.build();
        }
}
