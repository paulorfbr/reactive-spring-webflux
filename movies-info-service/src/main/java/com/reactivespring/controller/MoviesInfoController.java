package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Slf4j
public class MoviesInfoController {

    public static final String MOVIEINFOS = "movieinfos";
    @Autowired
    private MovieInfoService movieInfoService;

    @GetMapping("/" + MOVIEINFOS)
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year){
        log.info("year is :{}", year);
        if (year!=null){
            return movieInfoService.getMovieInfoByYear(year).log();
        }
        return movieInfoService.getAllMovieInfos().log();
    }

    @GetMapping("/" + MOVIEINFOS + "/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id){
        return movieInfoService.getMovieInfoById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @PostMapping("/" + MOVIEINFOS)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.addMovieInfo(movieInfo);
    }

    @PutMapping("/" + MOVIEINFOS + "/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id, @RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.updateMovieInfo(id, movieInfo)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("/" + MOVIEINFOS + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id){
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
