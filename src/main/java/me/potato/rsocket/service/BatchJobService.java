package me.potato.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import me.potato.rsocket.dto.RequestDto;
import me.potato.rsocket.dto.ResponseDto;
import me.potato.rsocket.util.ObjectUtil;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class BatchJobService implements RSocket {

    private RSocket rSocket;

    public BatchJobService(RSocket rSocket) {
        this.rSocket = rSocket;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        var requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        System.out.println("Received payload: " + requestDto + " " + Thread.currentThread().getName());
        Mono.just(requestDto)
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println("Processing " + i + " " + Thread.currentThread().getName()))
                .flatMap(this::findCube)
                .subscribe();

        return Mono.empty();
    }

    private Mono<Void> findCube(RequestDto requestDto) {
        var input = requestDto.getInput();
        var output = input * input * input;

        var responseDto = new ResponseDto(input, output);
        var payload = ObjectUtil.toPayload(responseDto);
        return this.rSocket.fireAndForget(payload);
    }
}
