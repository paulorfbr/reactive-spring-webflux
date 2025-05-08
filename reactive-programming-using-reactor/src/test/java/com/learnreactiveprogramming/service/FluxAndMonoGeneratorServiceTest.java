package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void namesMono() {
        //given

        //when
        var namesMono = fluxAndMonoGeneratorService.namesMono();

        //then
        StepVerifier.create(namesMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesFlatMapMono() {
        //given

        //when
        var namesMono = fluxAndMonoGeneratorService.namesFlatMapMono();

        //then
        StepVerifier.create(namesMono)
                .expectNext(List.of("l","i","l","i","a","n"))
                .verifyComplete();
    }

    @Test
    void namesFluxUppercase() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxUppercase();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("PAULO","MEIRE", "GAYA")
                .verifyComplete();
    }

    @Test
    void namesFluxFilter() {
        var namesFluxFilter = fluxAndMonoGeneratorService.namesFluxFilter(4);

        //then
        StepVerifier.create(namesFluxFilter)
                .expectNext("paulo","meire")
                .verifyComplete();
    }

    @Test
    void namesFluxDefaultIfEmpty() {
        var namesFluxFilter = fluxAndMonoGeneratorService.namesFluxFilter(6);

        //then
        StepVerifier.create(namesFluxFilter)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxDefaultSwitchIfEmpty() {
        var namesFluxFilter = fluxAndMonoGeneratorService.namesFluxFilter(6);

        //then
        StepVerifier.create(namesFluxFilter)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        var namesFlatMap = fluxAndMonoGeneratorService.namesFluxFlatMap();

        String s = "paulomeiregaya";
        //then
        StepVerifier.create(namesFlatMap)
                .expectNextCount(s.length())
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {
        var namesConcatMapFilter = fluxAndMonoGeneratorService.namesFluxConcatMap();

        String s = "paulomeiregaya";
        //then
        StepVerifier.create(namesConcatMapFilter)
                .expectNext(s.split(""))
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapMany() {
        var namesFlatMapMany = fluxAndMonoGeneratorService.namesFlatMapMany();

        String s = "lilian";
        //then
        StepVerifier.create(namesFlatMapMany)
                .expectNext(s.split(""))
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        var namesFluxConcat = fluxAndMonoGeneratorService.exploreConcat();

        //then
        StepVerifier.create(namesFluxConcat)
                .expectNext("A","B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        var namesFluxConcatWith = fluxAndMonoGeneratorService.exploreConcatWith();

        //then
        StepVerifier.create(namesFluxConcatWith)
                .expectNext("A","B")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {
        var namesFluxMerge = fluxAndMonoGeneratorService.exploreMerge();

        //then
        StepVerifier.create(namesFluxMerge)
                .expectNext("A","D","B","E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
        var namesFluxZip = fluxAndMonoGeneratorService.exploreZip();

        //then
        StepVerifier.create(namesFluxZip)
                .expectNext("AD","BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZip_1() {
        var namesFluxZip = fluxAndMonoGeneratorService.exploreZip_1();

        //then
        StepVerifier.create(namesFluxZip)
                .expectNext("ADG14","BEH25", "CFI36")
                .verifyComplete();
    }
}