package io.byzaneo.test.config;

import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.spring.security.api.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveJwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final String issuerUri;
    private final JwtAuthenticationProvider provider;

    public ReactiveJwtAuthenticationManager(@Value("${spring.security.oauth2.resourceserver.jwk.issuer-uri}") String issuerUri) {
        this.provider = new JwtAuthenticationProvider(
                new JwkProviderBuilder(issuerUri).build(),
                issuerUri,
                "urn:one");
        this.issuerUri = issuerUri;
    }

    public Mono<Authentication> authenticate(Authentication var1) {


        return Mono.justOrEmpty(this.provider.authenticate(var1));
    }
}
