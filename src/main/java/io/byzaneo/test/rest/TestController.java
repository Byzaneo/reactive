package io.byzaneo.test.rest;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(TestController.BASE_PATH)
public class TestController {

    public static final String BASE_PATH = "/api";

//    @Price(value = "0.01", description = "Example of priced endpoint")
    @GetMapping
    public ResponseEntity<String> info() {
        try {
            return ok("Hello! I'm the test service.");
        } catch (Exception e) {
            return status(BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/secure")
    public Mono<String> secure() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getDetails)
                .filter(DecodedJWT.class::isInstance)
                .map(DecodedJWT.class::cast)
                .map(jwt -> jwt.getClaim("https://byzaneo.io/email"))
                .map(Claim::asString)
                .map(email -> "Hello ".concat(email).concat("!"));
    }
}
