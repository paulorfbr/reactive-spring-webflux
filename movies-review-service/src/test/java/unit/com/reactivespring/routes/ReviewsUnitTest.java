package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@WebFluxTest
@ContextConfiguration(classes = { ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class })
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    static String MOVIES_REVIEW_URL = "/v1/reviews";

    @Test
    void addReview(){

        var movieReview = new Review(null, 1L, "Nice movie", 7.5);
        var movieReviewMocked = new Review("abc", 1L, "Nice movie", 7.5);

        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(
                Mono.just(movieReviewMocked)
        );

        //when
        webTestClient
                .post()
                .uri(MOVIES_REVIEW_URL)
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedReview!=null;
                    assert savedReview.getReviewId()!=null;
                    assertEquals("abc", savedReview.getReviewId());
                });
    }

    @Test
    void addReview_validation(){

        var movieReview = new Review(null, null, "Nice movie", -7.5);

        //when
        webTestClient
                .post()
                .uri(MOVIES_REVIEW_URL)
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.movieInfoId : must not be null,review.negative : please pass a non-negative value")
                ;
    }

    @Test
    void getAllReviews(){
        //when
        List<Review> movieReviews =   List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(movieReviews));

        webTestClient
                .get()
                .uri(MOVIES_REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {

        //when
        String movieReviewId = "abc";
        var movieReview = new Review(null, 1L, "Nice movie", 7.5);
        var movieReviewMocked = new Review(movieReviewId, 1L, "Nice movie", 7.5);

        when(reviewReactiveRepository.findById(isA(String.class))).thenReturn(
                Mono.just(movieReviewMocked)
        );

        webTestClient
                .put()
                .uri(MOVIES_REVIEW_URL+"/{id}",movieReviewId)
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieReview!=null;
                    assert updatedMovieReview.getMovieInfoId()!=null;
                    assertEquals("Nice movie", updatedMovieReview.getComment());
                    assertEquals(7.5, updatedMovieReview.getRating());
                });

        //then
    }

    @Test
    void deleteMovieInfo() {

        //when
        String movieReviewId = "abc";
        var mockedReview = new Review("abc", 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(mockedReview));

        when(reviewReactiveRepository.deleteById(isA(String.class))).thenReturn(
                Mono.empty()
        );

        webTestClient
                .delete()
                .uri(MOVIES_REVIEW_URL+"/{id}",movieReviewId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectStatus()
                .isNoContent();

        //then
        verify(reviewReactiveRepository, times(1)).deleteById(anyString());
    }
}
