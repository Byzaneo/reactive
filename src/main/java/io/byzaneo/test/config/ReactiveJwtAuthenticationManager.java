package io.byzaneo.test.config;

import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Component
public class ReactiveJwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final String audience = "urn:one";
    private final String issuer = "https://id.byzaneo.io";
    private final long leeway = 0;
    private final JwkProvider provider;

    public ReactiveJwtAuthenticationManager() {
        this.provider = new JwkProviderBuilder(this.issuer).build();
    }

    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!BearerTokenAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return null;
        }

        BearerTokenAuthenticationToken auth = (BearerTokenAuthenticationToken) authentication;
        DecodedJWT jwt = JWT.decode(auth.getToken());
        final String kid = jwt.getKeyId();
        if (kid == null) {
            throw new BadCredentialsException("No kid found in jwt");
        }
        try {
            final Jwk jwk = this.provider.get(kid);
            final JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null))
                    .withIssuer(this.issuer)
                    .withAudience(this.audience)
                    .acceptLeeway(this.leeway)
                    .build();
            verifier.verify(jwt);
            HashMap<String, Object> claims = new HashMap<>();
            jwt.getClaims().forEach(claims::put);
            return Mono.just(
                    new JwtAuthenticationToken(new Jwt(
                            auth.getToken(),
                            jwt.getIssuedAt().toInstant(),
                            jwt.getExpiresAt().toInstant(),
                            Map.of(
                                    PublicClaims.TYPE, jwt.getType(),
                                    PublicClaims.ALGORITHM, jwt.getAlgorithm(),
                                    PublicClaims.KEY_ID, jwt.getKeyId()
                            ),
                            claims),
                            this.getAuthorities(jwt)));
        } catch (SigningKeyNotFoundException e) {
            throw new AuthenticationServiceException("Could not retrieve jwks from issuer", e);
        } catch (InvalidPublicKeyException e) {
            throw new AuthenticationServiceException("Could not retrieve public key from issuer", e);
        } catch (JwkException e) {
            throw new AuthenticationServiceException("Cannot authenticate with jwt", e);
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("Not a valid token", e);
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities(DecodedJWT jwt) {
        String scope = jwt.getClaim("scope").asString();
        if (scope == null || scope.trim().isEmpty()) {
            return new ArrayList<>();
        }
        final String[] scopes = scope.split(" ");
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(scopes.length);
        for (String value : scopes) {
            authorities.add(new SimpleGrantedAuthority(value));
        }
        return authorities;
    }
}
