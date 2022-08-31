package me.potato.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec03BackPressureTest {
    private static RSocket rSocket;

    @BeforeAll
    public static void setup() {
        rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .block();
    }

    @Test
    public void backPressureTest() throws InterruptedException {
        // default buffer size is 32
        var flux = rSocket.requestStream(DefaultPayload.create(""))
                .map(Payload::getDataUtf8)
                .delayElements(Duration.ofMillis(200))
                .take(10)
                .doOnNext(System.out::println)
                .log();
        StepVerifier
                .create(flux)
                .expectNextCount(10)
                .verifyComplete();
    }
}
