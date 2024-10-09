package com.locket.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class Routes {
    @Value(value ="${servers.profile.url}")
    private String profileServiceUrl;
    @Value(value ="${servers.media.url}")
    private String mediaServiceUrl;
    @Bean
    public RouterFunction<ServerResponse> profileServiceSwaggerRoute() {
        return RouterFunctions.route()
                .path("/aggregate/profile-service/v3/api-docs", builder -> builder
                        .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON),
                                request -> WebClient.create(profileServiceUrl)
                                        .get()
                                        .uri("/api-docs")
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .flatMap(s -> ServerResponse.ok().bodyValue(s)
                                        )
                        )
                ).build();
    }
    @Bean
    public RouterFunction<ServerResponse> mediaServiceSwaggerRoute() {
        return RouterFunctions.route()
                .path("/aggregate/media-service/v3/api-docs", builder -> builder
                        .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON),
                                request -> WebClient.create(mediaServiceUrl)
                                        .get()
                                        .uri("/api-docs")
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .flatMap(s -> ServerResponse.ok().bodyValue(s)
                                        )
                        )
                ).build();
    }
}
