package com.usta.fisioterapiali.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/bot/**"))
                .authorizeHttpRequests(auth -> auth
                        // El bot de n8n consume estas rutas sin sesion
                        .requestMatchers("/api/bot/**").permitAll()

                        // Paginas publicas del sitio
                        .requestMatchers("/", "/index", "/login", "/register", "/contactanos",
                                "/service", "/sobre-nosotros",
                                "/css/**", "/js/**", "/images/**", "/public/**").permitAll()

                        // Zona privada
                        .requestMatchers("/admin/**", "/api/modal/**").authenticated()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}