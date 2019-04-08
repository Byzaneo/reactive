package io.byzaneo.test.config;

import com.auth0.spring.security.api.authentication.PreAuthenticatedAuthenticationJsonWebToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ReactiveContextRepository implements ServerSecurityContextRepository {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = this.tokenFromRequest(exchange.getRequest());
        Authentication authentication = PreAuthenticatedAuthenticationJsonWebToken.usingToken(token);
        if (authentication != null) {
            return this.authenticationManager.authenticate(authentication).map(auth -> {
                final SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                return context;
            });
//            TODO: log.debug("Found bearer token in request. Saving it in SecurityContext");
        }
        return Mono.empty();
    }

    private String tokenFromRequest(ServerHttpRequest request) {
        String value = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (value != null && value.toLowerCase().startsWith("bearer")) {
            String[] parts = value.split(" ");
            return parts.length < 2 ? null : parts[1].trim();
        } else {
            return null;
        }
    }
}
