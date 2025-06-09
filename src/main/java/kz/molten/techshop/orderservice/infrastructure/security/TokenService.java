package kz.molten.techshop.orderservice.infrastructure.security;

import kz.molten.techshop.orderservice.infrastructure.kafka.dto.TokenResponse;
import kz.molten.techshop.orderservice.infrastructure.external.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${techshop.services.order.username}")
    private String serviceUserUsername;

    @Value("${techshop.services.order.password}")
    private String serviceUserPassword;

    private final JwtDecoder jwtDecoder;
    private final UserServiceClient userServiceRestClient;

    private final AtomicReference<String> cachedAccessToken = new AtomicReference<>();
    private final AtomicReference<String> cachedRefreshToken = new AtomicReference<>();

    public String getJwt() {
        log.info("Getting JWT of order-service user");
        String token = cachedAccessToken.get();

        if (token == null) {
            log.info("JWT is null");
            updateJWTData(userServiceRestClient.getAccessAndRefreshToken(serviceUserUsername, serviceUserPassword));
        } else if (isExpired(token)) {

            updateJWTData(userServiceRestClient.refreshAccessToken(cachedRefreshToken.get()));
        }

        return cachedAccessToken.get();
    }

    private boolean isExpired(String token) {
        log.debug("Checking is JWT expired");

        try {
            jwtDecoder.decode(token);
        } catch (JwtValidationException exception) {
            if (exception.getMessage().contains("expired")) {
                return true;
            }
        }

        return false;
    }

    private void updateJWTData(TokenResponse tokenResponse) {
        cachedAccessToken.set(tokenResponse.token());
        cachedRefreshToken.set(tokenResponse.refreshToken());

        log.info("JWT were updated");
    }
}
