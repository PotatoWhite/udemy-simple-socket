package me.potato.rsocket;

import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import me.potato.rsocket.dto.RequestDto;
import me.potato.rsocket.dto.ResponseDto;
import me.potato.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec05ConnectionSetupTest {
    private static RSocketClient rSocketClient;
    private static RSocketClient rSocketClientForFree;

    @BeforeAll
    public static void setup() {
        var socketMono = RSocketConnector.create().setupPayload(DefaultPayload.create(DefaultPayload.create("user:password")))
                .connect(TcpClientTransport.create("localhost", 6565))
                .doOnNext(i -> System.out.println("connected : " + i.toString() + " " + Thread.currentThread().getName()));

        var freeSocketMono = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .doOnNext(i -> System.out.println("connected : " + i.toString() + " " + Thread.currentThread().getName()));

        rSocketClient = RSocketClient.from(socketMono);
        rSocketClientForFree = RSocketClient.from(freeSocketMono);


    }

    private static void accept(ResponseDto i) {
        System.out.println("Received : " + i + " " + Thread.currentThread().getName());
    }

    @Test
    public void connectionTest() throws InterruptedException {
        var requestDto = ObjectUtil.toPayload(new RequestDto(5));

        // default buffer size is 32
        var flux = rSocketClient.requestStream(Mono.just(requestDto))
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(i -> System.out.println("Received : " + i + " " + Thread.currentThread().getName()));
        StepVerifier
                .create(flux)
                .expectNextCount(10)
                .verifyComplete();

    }


    @Test
    public void freeConnectionTest() throws InterruptedException {
        var requestDto = ObjectUtil.toPayload(new RequestDto(5));

        // default buffer size is 32
        var flux = rSocketClientForFree.requestStream(Mono.just(requestDto))
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(i -> System.out.println("Received : " + i + " " + Thread.currentThread().getName()));
        StepVerifier
                .create(flux)
                .expectNextCount(3)
                .verifyComplete();

    }
}
