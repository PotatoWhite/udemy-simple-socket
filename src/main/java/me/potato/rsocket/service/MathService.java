package me.potato.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import me.potato.rsocket.dto.ChartResponseDto;
import me.potato.rsocket.dto.RequestDto;
import me.potato.rsocket.dto.ResponseDto;
import me.potato.rsocket.util.ObjectUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class MathService implements RSocket {
    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        var requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        System.out.println("Received payload: " + requestDto + " " + Thread.currentThread().getName());
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return Mono.fromSupplier(() -> {
            var requestDto  = ObjectUtil.toObject(payload, RequestDto.class);
            var responseDto = new ResponseDto(requestDto.getInput(), requestDto.getInput() * requestDto.getInput());
            System.out.println("ResponseDto payload: " + responseDto + " " + Thread.currentThread().getName());
            return ObjectUtil.toPayload(responseDto);
        });
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        var requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        return Flux.range(1, 10)
                .map(i -> i * requestDto.getInput())
                .map(i -> new ResponseDto(requestDto.getInput(), i))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(System.out::println)
                .doFinally(System.out::println)
                .map(ObjectUtil::toPayload);
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads)
                .map(payload -> ObjectUtil.toObject(payload, RequestDto.class))
                .map(RequestDto::getInput)
                .subscribeOn(Schedulers.boundedElastic())
                .map(input -> new ChartResponseDto(input, (input * input) + 1))
                .map(ObjectUtil::toPayload);
    }
}
