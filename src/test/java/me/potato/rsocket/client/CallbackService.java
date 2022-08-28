package me.potato.rsocket.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import me.potato.rsocket.dto.ResponseDto;
import me.potato.rsocket.util.ObjectUtil;
import reactor.core.publisher.Mono;

public class CallbackService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        var responseDto = ObjectUtil.toObject(payload, ResponseDto.class);
        System.out.println("[TEST]Received payload: " + responseDto + " " + Thread.currentThread().getName());
        return Mono.empty();
    }
}
