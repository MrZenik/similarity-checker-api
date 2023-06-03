package com.yevhen.berladyniuk.codesimilaritychecker.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.LoginRequest;
import com.yevhen.berladyniuk.codesimilaritychecker.dto.UserDto;
import com.yevhen.berladyniuk.codesimilaritychecker.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserService userService;

    public UserAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDto userDto) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hour

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withIssuer(userDto.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        UserDto user = userService.findByEmail(decoded.getIssuer());
        Collection<? extends GrantedAuthority> authorities = userService.getUserRolesByEmail(decoded.getIssuer());
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    public Authentication validateCredentials(LoginRequest loginRequest) {
        UserDto user = userService.authenticate(loginRequest);
        Collection<? extends GrantedAuthority> authorities = userService.getUserRolesByEmail(loginRequest.getEmail());
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

}
