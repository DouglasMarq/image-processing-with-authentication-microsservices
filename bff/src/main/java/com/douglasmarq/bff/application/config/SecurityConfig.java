package com.douglasmarq.bff.application.config;

import com.douglasmarq.bff.domain.dto.UserToken;
import com.douglasmarq.bff.infraestructure.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authz ->
                        authz.requestMatchers("/graphql", "/graphiql/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        exception -> {
                            exception.authenticationEntryPoint(
                                    (request, response, authException) -> {
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                "Invalid or missing token");
                                    });
                            exception.accessDeniedHandler(
                                    (request, response, accessDeniedException) -> {
                                        response.sendError(
                                                HttpServletResponse.SC_FORBIDDEN, "Access denied");
                                    });
                        })
                .addFilterBefore(
                        new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws java.io.IOException, jakarta.servlet.ServletException {
            String bearerToken = request.getHeader("Authorization");

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                if (JwtUtil.validateToken(token)) {
                    UserToken userToken = JwtUtil.parseTokenClaimsIntoMap(token);
                    String username = JwtUtil.getSubject(token);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());

                    authentication.setDetails(userToken);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
