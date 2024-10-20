package com.example.backendservice1.security;

import com.example.backendservice1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   /* @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disabilita CSRF se non necessario
            .authorizeRequests()
                .antMatchers("/api/data").permitAll() // Permetti accesso all'endpoint
                .anyRequest().authenticated() // Richiedi autenticazione per altri endpoint
            .and()
            .formLogin() // Configura il login form
                .loginPage("/login") // Specifica la pagina di login
                .permitAll(); // Permetti l'accesso alla pagina di login
    }*/

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/login", "/login").permitAll() // Permetti accesso alla pagina di login
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // Imposta la pagina di login
                .permitAll()
                .and()
                .logout()
                .permitAll();

        // Gestisci gli errori di autenticazione
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login"); // Redirect alla pagina di login
                });
    }
}
