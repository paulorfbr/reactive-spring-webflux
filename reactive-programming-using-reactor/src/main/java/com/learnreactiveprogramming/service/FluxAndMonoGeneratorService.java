package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

import java.time.Duration;
import java.util.*;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux(){
        return Flux.fromIterable(List.of("paulo","meire","gaya"))
                .log();
    }

    public Flux<String> namesFluxUppercase(){
        return Flux.fromIterable(List.of("paulo","meire","gaya"))
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFluxFilter(int strLength){
        return Flux.fromIterable(List.of("paulo","meire","gaya"))
                .filter(n -> n.length() > strLength)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxFlatMap(){
        return Flux.fromIterable(List.of("paulo","meire","gaya"))
                .flatMap(this::splitString) //no ordering
                .log();
    }

    public Flux<String> namesFluxConcatMap(){
        return Flux.fromIterable(List.of("paulo","meire","gaya"))
                .concatMap(this::splitString) //ordering matters
                .log();
    }



    public Flux<String> namesFlatMapMany(){
        return Mono.just("lilian")
                .flatMapMany(this::splitString)
                .log();
    }

    private Flux<String> splitString(String name){
        var arrayChar = name.split("");
        var delay =  new Random().nextInt(100);
        return Flux.fromArray(arrayChar)
                .delayElements(Duration.ofMillis(delay));
    }

    public Mono<String> namesMono(){
        return Mono.just("lilian")
                .log();
    }

    public Mono<List<String>> namesFlatMapMono(){
        return Mono.just("lilian")
                .flatMap(this::splitStringMono)
                .log();
    }

    private Mono<List<String>> splitStringMono(String name) {
        var arrayChar = name.split("");
        var charList = List.of(arrayChar);
        return Mono.just(charList);
    }

    public Flux<String> exploreConcat() {
        Flux<String> abcFlux = Flux.just("A","B","C");

        Flux<String> defFlux = Flux.just("D","E","F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> exploreConcatWith() {
        Mono<String> aMono = Mono.just("A");

        Mono<String> bMono = Mono.just("B");

        return aMono.concatWith(bMono).log();
    }

    public Flux<String> exploreMerge() {
        Flux<String> abcFlux =
                Flux.just("A","B","C")
                        .delayElements(Duration.ofMillis(100));

        Flux<String> defFlux =
                Flux.just("D","E","F")
                        .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        Flux<String> abcFlux =
                Flux.just("A","B","C");

        Flux<String> defFlux =
                Flux.just("D","E","F");

        return Flux.zip(abcFlux, defFlux, (f1,f2)-> f1 + f2).log();
    }

    public Flux<String> exploreZip_1() {
        Flux<String> abcFlux =
                Flux.just("A","B","C");

        Flux<String> defFlux =
                Flux.just("D","E","F");

        Flux<String> ghiFlux =
                Flux.just("G","H","I");

        Flux<String> _123Flux =
                Flux.just("1","2","3");

        Flux<String> _456Flux =
                Flux.just("4","5","6");


        return Flux.zip(abcFlux, defFlux, ghiFlux, _123Flux, _456Flux)
                .map(t5 -> t5.getT1() + t5.getT2() + t5.getT3() + t5.getT4() + t5.getT5())
                .log();
    }


    public static void main(String[] args) {
        FluxAndMonoGeneratorService monoGeneratorService = new FluxAndMonoGeneratorService();

        monoGeneratorService.namesFlux().subscribe(
                name -> {
                    System.out.println("Flux name is:" + name);
                }
        );

        monoGeneratorService.namesMono()
                .subscribe(
                        name -> {
                            System.out.println("Mono name is:" + name);
                        }
                );
    }
}
