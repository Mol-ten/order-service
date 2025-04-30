package kz.molten.techshop.orderservice.infrastructure.config;

import kz.molten.techshop.orderservice.api.exception.WebClientInternalServerException;
import kz.molten.techshop.orderservice.api.exception.WebClientUsersideException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${techshop.services.catalog.url}")
    public String CATALOG_SERVICE_URL;

    @Value("${techshop.services.user.url}")
    public String USER_SERVICE_URL;

    @Bean(name = "catalogServiceWebClient")
    public WebClient catalogServiceWebClient() {
        return WebClient.builder()
                .baseUrl(CATALOG_SERVICE_URL)
                .filter(errorHandler())
                .filter(logRequest())
                .filter(logResponse())
                .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    @Bean(name = "userServiceWebClient")
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(USER_SERVICE_URL)
                .filter(errorHandler())
                .filter(logRequest())
                .filter(logResponse())
                .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    private static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new WebClientInternalServerException(errorBody)));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new WebClientUsersideException(errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> log.debug(name + ": " + value)));
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) ->
                    values.forEach(value -> log.debug("Response header: {}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }
}
