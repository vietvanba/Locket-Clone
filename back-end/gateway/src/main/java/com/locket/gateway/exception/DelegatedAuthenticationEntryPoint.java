package com.locket.gateway.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DelegatedAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    private final ErrorResponseService service;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ErrorResponse response = new ErrorResponse("Unauthorized", "Please try to login again!");
        return service.writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, response);
    }
}
