package me.potato.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import me.potato.rsocket.dto.RequestDto;
import me.potato.rsocket.util.ObjectUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FastProducerService implements RSocket {
    private RSocket rSocket;


    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        var requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        System.out.println("Received payload: " + requestDto + " " + Thread.currentThread().getName());
        return Mono.empty();
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return Flux.range(1, 1000)
                .map(String::valueOf)
                .doOnNext(System.out::println)
                .doFinally(System.out::println)
                .map(DefaultPayload::create);
    }
}
