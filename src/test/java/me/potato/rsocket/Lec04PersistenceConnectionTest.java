package me.potato.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec04PersistenceConnectionTest {

    private static RSocketClient rSocketClient;

    @BeforeAll
    public static void setup() {
        var socketMono = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .doOnNext(i -> System.out.println("connected : " + i.toString() + " " + Thread.currentThread().getName()));

        rSocketClient = RSocketClient.from(socketMono);
    }

    @Test
    public void connectionTest() throws InterruptedException {


        // default buffer size is 32
        var flux = rSocketClient.requestStream(Mono.just(DefaultPayload.create("")))
                .map(Payload::getDataUtf8)
                .delayElements(Duration.ofMillis(2000))
                .take(10)
                .doOnNext(i -> System.out.println("Received : " + i + " " + Thread.currentThread().getName()))
                .log();
        StepVerifier
                .create(flux)
                .expectNextCount(10)
                .verifyComplete();


        System.out.println("[TEST] going to sleep");
        Thread.sleep(15000);
        System.out.println("[TEST] woke up");

        var flux2 = rSocketClient.requestStream(Mono.just(DefaultPayload.create("")))
                .map(Payload::getDataUtf8)
                .delayElements(Duration.ofMillis(2000))
                .take(10)
                .doOnNext(i -> System.out.println("Received : " + i + " " + Thread.currentThread().getName()))
                .log();
        StepVerifier
                .create(flux2)
                .expectNextCount(10)
                .verifyComplete();

    }
}
