package com.cs203.cs203system.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration class for setting up security configurations, including JWT authentication,
 * role-based access control, password encoding, and defining security filter chains.
 */
@Configuration
public class SecurityConfig {

    private final RSAKeyProperties keys;

    /**
     * Constructor that accepts RSA key properties.
     *
     * @param keys The {@link RSAKeyProperties} used to retrieve public and private RSA keys.
     */
    public SecurityConfig(RSAKeyProperties keys) {
        this.keys = keys;
    }

    /**
     * Configures the security filter chain, defining security settings such as session management,
     * permitted requests, and request matchers.
     *
     * @param http         The {@link HttpSecurity} instance used to configure HTTP security.
     * @param introspector The {@link HandlerMappingIntrospector} for managing MVC request mappings.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs while configuring the security filter chain.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

        // PLEASE KEEP THIS
        //disable frame options to allow h2 console access
        http.headers(httpSecurityHeadersConfigurer ->
                httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // Add your firewall to HttpSecurity
        //set custome firewall
        http.setSharedObject(HttpFirewall.class, allowUrlEncodedSlashHttpFirewall());

        MvcRequestMatcher h2RequestMatcher = new MvcRequestMatcher(introspector, "/**");
        h2RequestMatcher.setServletPath("/h2-console");

        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/ws/**").permitAll()  // Explicitly allow WebSocket access
                        .requestMatchers(HttpMethod.GET, "/api/tournament/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tournament/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tournament/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tournament/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/player/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/match/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/match/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers("/socket.io/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }



    /**
     * Creates and returns an {@link AuthenticationManager} for managing authentication.
     *
     * @param detailsService The {@link UserDetailsService} for loading user-specific data.
     * @return The configured {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService detailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(detailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }

    /**
     * Configures a {@link PasswordEncoder} for encoding passwords.
     *
     * @return The {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and returns a {@link JwtDecoder} to decode JWT tokens.
     *
     * @return The {@link NimbusJwtDecoder} configured with the RSA public key.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    /**
     * Creates and returns a {@link JwtEncoder} to encode JWT tokens.
     *
     * @return The {@link NimbusJwtEncoder} configured with the RSA key pair.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * Configures and returns a {@link JwtAuthenticationConverter} to convert JWT tokens to authentication objects.
     *
     * @return The {@link JwtAuthenticationConverter} configured with granted authorities.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedDoubleSlash(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedPeriod(true);
        firewall.setAllowUrlEncodedLineFeed(true);
        return firewall;
    }

    /**
     * WebSecurityCustomizer to apply the custom firewall.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }




    /**
     * Configures and returns a {@link RoleHierarchy} for hierarchical role-based access control.
     *
     * @return The configured {@link RoleHierarchy}.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = "ROLE_ADMIN > ROLE_PLAYER";
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }
}
