package com.reactivespring.exceptionhandler;

import com.reactivespring.exception.ReviewDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        log.error("Exception message is {}", throwable.getMessage(), throwable);
        DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();
        var errorMessage = bufferFactory.wrap(throwable.getMessage().getBytes());
        if (throwable instanceof ReviewDataException){
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }
        else {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return serverWebExchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
