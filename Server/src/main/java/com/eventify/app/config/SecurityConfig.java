package com.eventify.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final XssFilter xssFilter;
    private final SqlInjectionFilter sqlInjectionFilter;
    private final RequestLimitFilter requestLimitFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

          return http
                  .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
                  .addFilterBefore(sqlInjectionFilter, UsernamePasswordAuthenticationFilter.class)
                  .addFilterBefore(requestLimitFilter, UsernamePasswordAuthenticationFilter.class)
                  .csrf(AbstractHttpConfigurer::disable)
                  .requiresChannel(channel ->
                    channel.anyRequest().requiresSecure())
                  .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  .authorizeHttpRequests((requests) -> requests
                          .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                          .requestMatchers(HttpMethod.GET, "/teller/subscribe/**").permitAll()
                          .anyRequest().authenticated())
                  .authenticationProvider(authenticationProvider)
                  .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                  .headers(headers -> headers
                          .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", "script-src 'self';"))
                          .frameOptions(Customizer.withDefaults()))
                  .logout(logout -> logout
                          .logoutUrl("/api/auth/logout")
                          .addLogoutHandler(logoutHandler)
                          .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
                  .build();
        }
    }
