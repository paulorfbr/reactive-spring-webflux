package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {
    public static final String API_VERSION = "v1";

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
                .nest(path("/" + API_VERSION + "/reviews"), builder -> {
                     builder.POST("", reviewHandler::addReview)
                            .GET("", reviewHandler::getReviews)
                             .PUT("/{id}", reviewHandler::updateReview)
                             .DELETE("/{id}", reviewHandler::deleteReview);
                })
                .GET("/" + API_VERSION + "/helloworld",(serverRequest -> ServerResponse.ok().bodyValue("helloworld")))
                .build();
    }
}
