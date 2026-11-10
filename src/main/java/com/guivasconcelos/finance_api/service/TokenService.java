package com.guivasconcelos.finance_api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.guivasconcelos.finance_api.config.JwtProperties;
import com.guivasconcelos.finance_api.model.User;

@Service
public class TokenService {
    private final JwtEncoder encoder;
    private JwtProperties props;

    public TokenService(JwtEncoder encoder, JwtProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public String generate(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("finance-api")
            .subject(user.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plus(props.expirationMinutes(), ChronoUnit.MINUTES))
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long expiresInSeconds() {
        return props.expirationMinutes() * 60;
    }

}
