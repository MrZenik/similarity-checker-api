package com.yevhen.berladyniuk.codesimilaritychecker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationProvider userAuthenticationProvider;

    public SecurityConfig(UserAuthenticationEntryPoint userAuthenticationEntryPoint,
                          UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
                .and()
                .addFilterBefore(new UsernamePasswordAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), UsernamePasswordAuthFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/users/login", "/users/register").permitAll()
                        //Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        //Users
                        .requestMatchers(HttpMethod.POST, "/users/findUsersByEmail").hasRole("TEACHER")
                        //Subjects
                        .requestMatchers(HttpMethod.PUT, "/subject/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/subject/**").hasRole("TEACHER")
                        //Enrollment
                        .requestMatchers(HttpMethod.POST, "/enrollment/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.GET, "/enrollment/get-users/{subjectId}").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.GET, "/enrollment/{userId}").hasRole("STUDENT")
                        //Files
                        .requestMatchers("/files/get-teacher-structure").hasRole("TEACHER")
                        .requestMatchers("/files/get-teacher-structure").hasRole("TEACHER")
                        .requestMatchers("/files/download").hasRole("TEACHER")
                        .requestMatchers("/files/get-structure").hasRole("STUDENT")
                        //Statistic
                        .requestMatchers(HttpMethod.GET, "/files-similar-pairs/**").hasRole("TEACHER")
                        //Rest of endpoints
                        .anyRequest().authenticated())
        ;
        return http.build();
    }
}