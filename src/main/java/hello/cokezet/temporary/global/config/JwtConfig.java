package hello.cokezet.temporary.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.net.URI;

@Configuration
public class JwtConfig {

    private static final String URI = "https://www.googleapis.com/oauth2/v3/certs";

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(URI).build();
    }
}
