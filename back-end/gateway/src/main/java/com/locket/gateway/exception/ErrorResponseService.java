package com.locket.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ErrorResponseService {
    public Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, ErrorResponse errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer buffer = createErrorBuffer(bufferFactory, errorResponse);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private DataBuffer createErrorBuffer(DataBufferFactory bufferFactory, ErrorResponse errorResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ErrorResponse to JSON", e);
        }
    }
}
