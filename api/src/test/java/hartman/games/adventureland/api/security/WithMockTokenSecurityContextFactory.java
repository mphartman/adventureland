package hartman.games.adventureland.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.io.UnsupportedEncodingException;

public class WithMockTokenSecurityContextFactory implements WithSecurityContextFactory<WithMockToken> {

    @Override
    public SecurityContext createSecurityContext(WithMockToken tokenAnnotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        String token = JWT.create()
                .withIssuer("foobar")
                .withAudience("baz")
                .withClaim("GivenName", "Johnny")
                .withClaim("Surname", "Rocket")
                .withClaim("Email", "johnny@user.com")
                .sign(Algorithm.HMAC256("secret"));
        Authentication auth = new MockAuthenticationJsonWebToken(token, tokenAnnotation.isAuthenticated());
        context.setAuthentication(auth);
        return context;
    }
}
