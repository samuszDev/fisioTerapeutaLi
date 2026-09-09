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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/bot/**"))
                .authorizeHttpRequests(auth -> auth
                        // 1. Recursos estáticos públicos (para que carguen estilos, imágenes y scripts)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/public/**").permitAll()

                        // 2. Proteger TODO lo que comience por /admin/ (incluyendo dashboard, agenda, clientes, etc.)
                        .requestMatchers("/admin/**", "/api/modal/**").authenticated()

                        // 3. Cualquier otra ruta pública
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/dashboard", true) // Te redirige al dashboard tras iniciar sesión
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}