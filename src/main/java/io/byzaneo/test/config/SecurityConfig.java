package io.byzaneo.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private ReactiveContextRepository reactiveContextRepository;

    @Bean
    public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
        http.exceptionHandling()
                .authenticationEntryPoint((swe, e) -> {
                    return Mono.fromRunnable(() -> {
                        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    });
                }).accessDeniedHandler((swe, e) -> {
            return Mono.fromRunnable(() -> {
                swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            });
        }).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(reactiveContextRepository)
                .authorizeExchange()
                .pathMatchers("/api").permitAll()
                .pathMatchers("/api/**").hasAuthority("home")
                .anyExchange().permitAll();

/*        http.authenticationManager(this.authenticationManager)
//                .securityContextRepository(new ReactiveBearerSecurityContextRepository())
                .exceptionHandling()
//                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
//                .httpBasic().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().disable()
                .csrf().disable()
                .authorizeExchange()
                    .pathMatchers("/api/**").hasAuthority("home")
                    .anyExchange().permitAll()
                .and()
                    .oauth2ResourceServer()
                    .jwt();*/
        return http.build();
    }

//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri);
//    }

}
