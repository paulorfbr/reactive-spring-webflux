package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfoList = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfoList)
                .blockLast();

    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll(){
        //given

        //when
        var movieInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findByYear(){
        //given

        //when
        var movieInfoFlux = movieInfoRepository.findByYear(2008).log();

        //then
        StepVerifier.create(movieInfoFlux)
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getMovieName());
                })
                .verifyComplete();
    }

    @Test
    void findByName(){
        //given

        //when
        String movieName = "Batman Begins";
        var movieInfoMono = movieInfoRepository.findByMovieName(movieName).log();

        //then
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(movieName, movieInfo.getMovieName());
                })
                .verifyComplete();
    }

    @Test
    void findById(){
        //given

        //when
        var movieInfoMono = movieInfoRepository.findById("abc").log();


        //then
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getMovieName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo(){
        //given
        var newMovieInfo = new MovieInfo(null, "Life of Pi",
                2005, List.of("Suraj Sharma", "Rafe Spall"), LocalDate.parse("2012-09-28"));

        //when
        var movieInfoMono = movieInfoRepository.save(newMovieInfo).log();


        //then
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Life of Pi", movieInfo.getMovieName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo(){
        //given
        var newMovieInfo = movieInfoRepository.findById("abc").block();
        assert newMovieInfo != null;
        newMovieInfo.setYear(2025);

        //when
        var movieInfoMono = movieInfoRepository.save(newMovieInfo).log();


        //then
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(2025, movieInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo(){
        //given

        //when
        var movieInfoMono = movieInfoRepository.deleteById("abc").block();
        var movieInfoFlux = movieInfoRepository.findAll().log();


        //then
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }
}