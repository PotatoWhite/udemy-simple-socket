package me.potato.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import me.potato.rsocket.service.BatchJobService;
import reactor.core.publisher.Mono;

public class SocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket rSocket) {
        System.out.println("SocketAcceptorImpl accept " + setup + " " + Thread.currentThread().getName());

//        return Mono.fromCallable(MathService::new);

        return Mono.fromCallable(() -> new BatchJobService(rSocket));
    }
}
