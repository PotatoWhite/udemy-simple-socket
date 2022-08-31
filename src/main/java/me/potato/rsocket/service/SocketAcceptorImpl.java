package me.potato.rsocket.service;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import reactor.core.publisher.Mono;

public class SocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket rSocket) {
        System.out.println("SocketAcceptorImpl accept " + setup + " " + Thread.currentThread().getName());

        if(isValidClient(setup.getDataUtf8()))
            return Mono.just(new MathService());
        else
            return Mono.just(new FreeService());

        // return Mono.fromCallable(MathService::new);
        // return Mono.fromCallable(FastProducerService::new);
    }

    private boolean isValidClient(String credential) {
        return "user:password".equals(credential);
    }
}
