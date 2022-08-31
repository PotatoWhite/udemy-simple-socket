package me.potato.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import me.potato.rsocket.service.SocketAcceptorImpl;

public class Server {
    public static void main(String[] args) {
        var socketServer = RSocketServer.create(new SocketAcceptorImpl());
        var closeableChannel = socketServer.bindNow(TcpServerTransport.create(6565));

        // keep the server running
        closeableChannel.onClose().block();
    }
}
