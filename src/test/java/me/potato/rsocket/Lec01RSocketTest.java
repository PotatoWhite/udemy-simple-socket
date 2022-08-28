package me.potato.rsocket;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import me.potato.rsocket.dto.ChartResponseDto;
import me.potato.rsocket.dto.RequestDto;
import me.potato.rsocket.dto.ResponseDto;
import me.potato.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec01RSocketTest {

    private static RSocket rSocket;

    @BeforeAll
    public static void setup() {
        rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .block();
    }

    @Test
    public void fireAndForgetTest() {
        var payload = ObjectUtil.toPayload(new RequestDto(5));
        var mono    = rSocket.fireAndForget(payload);

        StepVerifier
                .create(mono)
                .verifyComplete();

    }

    @Test
    public void requestResponseTest() {
        var payload = ObjectUtil.toPayload(new RequestDto(5));
        var mono = rSocket.requestResponse(payload)
                .map(response -> ObjectUtil.toObject(response, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier
                .create(mono)
                .expectNextMatches(responseDto -> responseDto.getOutput() == 25)
                .verifyComplete();
    }

    @Test
    public void requestStreamTest() {
        var payload = ObjectUtil.toPayload(new RequestDto(5));
        var flux = rSocket.requestStream(payload)
                .map(response -> ObjectUtil.toObject(response, ResponseDto.class))
                .doOnNext(System.out::println)
                .doFinally(System.out::println)
                .take(4);

        StepVerifier
                .create(flux)
                .expectNextCount(4)
                .verifyComplete();
    }


    @Test
    public void requestChannelTest() {
        var request = Flux.range(-10, 21)
                .map(RequestDto::new)
                .map(ObjectUtil::toPayload);

        var flux = rSocket.requestChannel(request)
                .map(response -> ObjectUtil.toObject(response, ChartResponseDto.class))
                .doOnNext(System.out::println)
                .doFinally(System.out::println);

        StepVerifier
                .create(flux)
                .expectNextCount(21)
                .verifyComplete();
    }

}
