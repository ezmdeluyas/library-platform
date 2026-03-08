package com.zmd.library_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {

    private final JwtConfigProperties props;

    @Bean
    public SecretKey secretKey() {
        if (props.secret() == null || props.secret().isBlank()) {
            throw new IllegalStateException("app.jwt.secret must be set");
        }
        byte[] keyBytes = Hex.decode(props.secret());
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();

        OAuth2TokenValidator<Jwt> issuerAndDefaults =
                JwtValidators.createDefaultWithIssuer(props.issuer());

        decoder.setJwtValidator(issuerAndDefaults);
        return decoder;
    }
}