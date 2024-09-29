package com.locket.gateway.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DelegatedAccessDeniedHandler implements ServerAccessDeniedHandler {
    private final ErrorResponseService service;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ErrorResponse response = new ErrorResponse("Not enough permissions", "Please login with account with higher authority");
        return service.writeErrorResponse(exchange, HttpStatus.FORBIDDEN, response);
    }
}
