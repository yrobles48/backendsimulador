package com.prestaclub.prestaclub_backend.security;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.prestaclub.prestaclub_backend.dto.PublicRoutes;
 
@EnableWebSecurity
@Configuration 
public class SecurityConfig { 

    @Value("${angular.urlFront}")
    private String urlFront;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }  

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    } 
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(
            request->{
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(Arrays.asList(urlFront));
                corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                corsConfiguration.setAllowCredentials(true); 
                corsConfiguration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
                return corsConfiguration;
            }
        )
        .and()
        .csrf().disable()
        .headers()
        .frameOptions().sameOrigin()
        .and()
        .authorizeHttpRequests()
        .requestMatchers(PublicRoutes.PUBLIC_URLS != null ? PublicRoutes.PUBLIC_URLS.toArray(new String[0]) : new String[]{})
        .permitAll()
        .anyRequest()
        .authenticated()
        .and().exceptionHandling()
        .and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    

}
