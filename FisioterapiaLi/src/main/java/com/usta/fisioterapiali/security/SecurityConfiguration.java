package com.usta.fisioterapiali.security;

import com.usta.fisioterapiali.handler.LoginSuccessHandler;
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
    private LoginSuccessHandler loginSuccessHandler;



    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build, BCryptPasswordEncoder passwordEncoder) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Acceso público para recursos estáticos, inicio, login y registro
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/vermas", "/login", "/register").permitAll()

                        // Acceso para FISIOTERAPEUTA y ADMINISTRADOR
                        .requestMatchers("/fisio/**", "/citas/**").hasAnyRole("FISIO", "ADMIN")

                        // Acceso para CLIENTE y ADMINISTRADOR
                        .requestMatchers("/cliente/**", "/perfil/**").hasAnyRole("CLIENTE", "ADMIN")

                        // Acceso exclusivo para ADMINISTRADOR
                        .requestMatchers("/admin/**", "/users/**", "/dashboard/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/error404")
                );

        return http.build();
    }
}