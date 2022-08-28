package me.potato.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;

public class Server {
    public static void main(String[] args) {
        var closeableChannel = RSocketServer.create(new SocketAcceptorImpl())
                .bindNow(TcpServerTransport.create(6565));

        // keep the server running
        closeableChannel.onClose().block();
    }
}
