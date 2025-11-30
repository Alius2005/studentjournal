package org.example.studentjournal.security;

import org.example.studentjournal.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Публичные страницы (без авторизации)
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        // Админские права: полный доступ
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Преподаватели: доступ к оценкам, посещаемости, урокам и т.д.
                        .requestMatchers(HttpMethod.GET, "/grades/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT")
                        .requestMatchers(HttpMethod.POST, "/grades/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/grades/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/grades/**").hasRole("ADMIN")
                        // Посещаемость: HEAD_STUDENT может редактировать (POST/PUT), все роли могут читать (GET)
                        .requestMatchers(HttpMethod.GET, "/attendance/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/attendance/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/attendance/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/attendance/**").hasRole("ADMIN")
                        // Группы: просмотр для всех, редактирование для админа/преподавателя
                        .requestMatchers(HttpMethod.GET, "/groups/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/groups/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/groups/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/groups/**").hasRole("ADMIN")
                        // Журнал: доступ аналогично группам с учетом HEAD_STUDENT
                        .requestMatchers("/journal/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT", "STUDENT")
                        // Студенты: просмотр для всех, редактирование для админа/преподавателя
                        .requestMatchers(HttpMethod.GET, "/students/**").hasAnyRole("ADMIN", "TEACHER", "HEAD_STUDENT", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/students/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/students/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/students/**").hasRole("ADMIN")
                        // Предметы и уроки: доступ для админа/преподавателя
                        .requestMatchers("/subjects/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/lessons/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                        // Пользователи: только админ
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        // Все остальное требует аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/journal", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);  // Отключено для простоты (в продакшене включите с настройкой)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
