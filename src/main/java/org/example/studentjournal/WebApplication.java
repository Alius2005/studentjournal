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

@SpringBootApplication  // Автоматически сканирует @Component, @Service, @Controller и т.д. в org.example.studentjournal
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  // Для @PreAuthorize в методах
public class WebApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        System.out.println("Веб-приложение запущено! Доступно по http://localhost:8080");
        System.out.println("Логин: admin / пароль: admin (для ADMIN роли). Или используйте /login для формы.");
    }

    // Бин для DbManager - инжектируется в сервисы и контроллеры
    @Bean
    public DbManager dbManager() {
        try {
            Config config = new Config("settings.xml");  // Загружаем конфиг из XML (как в вашем проекте)
            DbManager dbManager = new DbManager(config.jdbcUrl, config.jdbcUser, config.jdbcPassword, false);  // false: без GUI
            dbManager.getConnection();  // Подключаемся к БД
            return dbManager;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации DbManager для веб-приложения: " + e.getMessage(), e);
        }
    }

    // Бин для Config (если нужно инжектировать отдельно)
    @Bean
    public Config config() {
        try {
            return new Config("settings.xml");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки settings.xml", e);
        }
    }

    // Бин для PasswordEncoder (для хэширования паролей)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Вложенная конфигурация Spring Security (можно вынести в отдельный класс SecurityConfig)
    @Configuration
    public static class SecurityConfig {

        @Bean
        public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
            // Создаем in-memory пользователей (для простоты). В реальности замените на загрузку из DbManager.
            UserDetails admin = User.withUsername("admin")
                    .password(passwordEncoder.encode("admin"))  // Хэшируем пароль
                    .roles("ADMIN")
                    .build();
            UserDetails user = User.withUsername("user")
                    .password(passwordEncoder.encode("user"))
                    .roles("USER")
                    .build();
            return new InMemoryUserDetailsManager(admin, user);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/", "/login", "/css/**", "/js/**", "/error").permitAll()  // Публичные страницы
                            .requestMatchers("/students/**", "/groups/**", "/subjects/**", "/grades/**", "/attendance/**").hasRole("USER")  // Только для USER
                            .requestMatchers("/admin/**").hasRole("ADMIN")  // Только для ADMIN
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
                    .csrf(csrf -> csrf.disable());  // Отключаем CSRF для простоты (в продакшене включите)
            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
    }

    // Глобальный обработчик ошибок (ControllerAdvice)
    @ControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(SQLException.class)
        public String handleSQLException(SQLException e) {
            // Логируем ошибку и перенаправляем на страницу ошибки
            e.printStackTrace();
            return "error";  // Шаблон error.html в templates
        }

        @ExceptionHandler(Exception.class)
        public String handleGenericException(Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
