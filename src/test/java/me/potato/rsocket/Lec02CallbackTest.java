package me.potato.rsocket;

import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import me.potato.rsocket.client.CallbackService;
import me.potato.rsocket.dto.RequestDto;
import me.potato.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec02CallbackTest {
    private static RSocket rSocket;

    @BeforeAll
    public static void setup() {
        rSocket = RSocketConnector.create()
                .acceptor(SocketAcceptor.with(new CallbackService()))
                .connect(TcpClientTransport.create("localhost", 6565))
                .block();
    }

    @Test
    public void callbackTest() throws InterruptedException {
        var payload = ObjectUtil.toPayload(new RequestDto(5));
        var mono = rSocket.fireAndForget(payload)
                .log();

        StepVerifier
                .create(mono)
                .verifyComplete();

        System.out.println("[TEST] going to sleep");

        Thread.sleep(1000);
    }
}
