package techlab.backend.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import techlab.backend.security.JwtConfigurer;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtConfigurer jwtConfigurer;

    public SpringSecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, JwtConfigurer jwtConfigurer) {
        this.userDetailsService = userDetailsService;
        this.jwtConfigurer = jwtConfigurer;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests((request) ->
                        request
                                .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs/**",
                                        "/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/v1/signup", "/api/v1/signin").permitAll()
                                .requestMatchers("/api/v1/users/**").permitAll()//.hasAnyAuthority("admin", "user")
                                .requestMatchers("/api/v1/courses/**").authenticated()//.hasAnyAuthority("admin", "user")
                                .requestMatchers("/api/v1/admin/**").permitAll()//hasAuthority("admin")
                                .requestMatchers("/api/v1/testException/**").permitAll()
                                .anyRequest().permitAll())//.denyAll())
                .apply(jwtConfigurer);
        //.formLogin(withDefaults())

        //.httpBasic(withDefaults()

        //);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}


