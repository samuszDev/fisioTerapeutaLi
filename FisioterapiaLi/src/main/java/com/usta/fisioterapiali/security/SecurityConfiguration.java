package com.usta.fisioterapiali.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build, BCryptPasswordEncoder passwordEncoder) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Opcional: deshabilita CSRF para evitar bloqueos en formularios POST de prueba
                .authorizeHttpRequests(authorize -> authorize
                        // Acceso total y libre a cualquier URL de la aplicación sin restricciones
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable()) // Desactiva la redirección al login
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}