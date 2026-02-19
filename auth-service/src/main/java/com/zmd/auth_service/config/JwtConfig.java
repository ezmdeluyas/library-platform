package com.zmd.auth_service.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    private final JwtConfigProperties props;

    public JwtConfig(JwtConfigProperties props) {
        this.props = props;
    }

    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = Hex.decode(props.secret());
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();

        // Validate issuer + timestamps (exp/nbf)
        OAuth2TokenValidator<Jwt> issuerAndDefaults = JwtValidators.createDefaultWithIssuer(props.issuer());

        decoder.setJwtValidator(issuerAndDefaults);
        return decoder;
    }

}
