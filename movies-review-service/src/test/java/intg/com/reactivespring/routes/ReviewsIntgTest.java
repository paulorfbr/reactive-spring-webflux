package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {
    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIES_REVIEW_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {
        List<Review> movieReviews =   List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        reviewReactiveRepository.saveAll(movieReviews)
                .blockLast();
    }

    @AfterEach
    void tearDown(){
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        //given
        var movieReview = new Review();

        //when
        webTestClient
                .post()
                .uri(MOVIES_REVIEW_URL)
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(movieReviewEntityExchangeResult -> {

                    var savedReview = movieReviewEntityExchangeResult.getResponseBody();
                    assert savedReview!=null;
                    assert savedReview.getReviewId()!=null;
                });


        //then
    }

    @Test
    void getReviews() {
        //given

        //when
        webTestClient
                .get()
                .uri(MOVIES_REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });

    }

    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(MOVIES_REVIEW_URL)
                            .queryParam("movieInfoId", "1")
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });

    }

    @Test
    void updateReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 3.0);
        //when
        assert savedReview != null;

        webTestClient
                .put()
                .uri(MOVIES_REVIEW_URL + "/{id}", savedReview.getReviewId())
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertEquals(3.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });

    }

    @Test
    void deleteReview() {
        //given
        var review = new Review(null, 1L, "Hyper Awesome Movie", 9.5);
        var savedReview = reviewReactiveRepository.save(review).block();
        //when
        assert savedReview != null;
        webTestClient
                .delete()
                .uri(MOVIES_REVIEW_URL + "/{id}", savedReview.getReviewId())
                .exchange()
                .expectStatus().isNoContent();
    }

}
