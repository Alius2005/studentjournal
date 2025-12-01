package org.example.studentjournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.SQLException;

@SpringBootApplication
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        System.out.println("Веб-приложение запущено! Доступно по http://localhost:8080");
        System.out.println("Логин: admin / пароль: admin (для ADMIN роли).");
    }

    // Бин для Config
    @Bean
    public Config config() {
        try {
            return new Config("settings.xml");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки settings.xml", e);
        }
    }

    // Бин для PasswordEncoder (единственный)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Вложенная конфигурация Security (единственная)
    @Configuration
    public static class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/", "/login", "/css/**", "/js/**", "/error").permitAll()
                            .requestMatchers("/students/**", "/groups/**", "/subjects/**", "/grades/**", "/attendance/**").hasRole("USER")
                            .requestMatchers("/admin/**", "/users/**").hasRole("ADMIN")  // Добавил /users/** для UserController
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .defaultSuccessUrl("/students", true)
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login?logout")
                            .permitAll()
                    )
                    .exceptionHandling(ex -> ex
                            .accessDeniedPage("/access-denied")
                    )
                    .csrf(csrf -> csrf.disable());  // Отключено для теста; включите позже для продакшена
            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
    }

    @ControllerAdvice
    public static class GlobalExceptionHandler {
        @ExceptionHandler(SQLException.class)
        public String handleSQLException(SQLException e) {
            e.printStackTrace();
            return "error";
        }

        @ExceptionHandler(Exception.class)
        public String handleGenericException(Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
