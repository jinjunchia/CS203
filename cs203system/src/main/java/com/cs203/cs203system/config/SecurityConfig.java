//package com.cs203.cs203system.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.formLogin(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(HttpMethod.GET,"api/tournament", "api/tournament/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "api/player", "api/player/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults());
//
//        http.httpBasic(withDefaults());
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }
//}
