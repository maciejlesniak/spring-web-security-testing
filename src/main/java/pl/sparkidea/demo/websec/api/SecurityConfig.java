package pl.sparkidea.demo.websec.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .sessionManagement(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .addFilterBefore(new CustomFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v1/**").hasAnyAuthority("ROLE_R1")
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        configuration.setAllowedMethods(List.of("GET", "OPTIONS", "HEAD", "POST", "PUT", "DELETE", "PATCH"));
        configuration.addAllowedHeader(CorsConfiguration.ALL);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    private static class CustomFilter extends OncePerRequestFilter {

        private static final Logger LOG = LoggerFactory.getLogger(CustomFilter.class);

        @Override
        protected void doFilterInternal(
                @NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain) throws ServletException, IOException {

            var currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null && currentAuth.isAuthenticated()) {
                LOG.debug("Request already authenticated; current user: {}", currentAuth.getPrincipal());
                filterChain.doFilter(request, response);
                return;
            }

            var userName = Objects.requireNonNull(request.getHeader("X-User-Custom-Name"));
            LOG.info("Custom auth header user name: {}", userName);
            var roles = Arrays.stream(Objects.requireNonNull(request.getHeader("X-User-Custom-Roles")).split(" "))
                    .map(String::trim)
                    .filter(role -> !role.isBlank())
                    .toList();
            LOG.info("Custom auth header roles: {}", roles);

            if (!userName.isBlank()) {
                SecurityContextHolder.getContext().setAuthentication(
                        new OneTimeTokenAuthenticationToken(
                                userName,
                                roles.stream().map(SimpleGrantedAuthority::new).toList()
                        )
                );
                filterChain.doFilter(request, response);
            }

        }
    }

    /**
     * Disables UserDetailsServiceAutoConfiguration by providing authentication manager with no users
     *
     * @return No-ops authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() {
        return authentication -> null;
    }

}
