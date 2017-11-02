package com.lee.entity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class NetworkClient {

    private AsynchronousSocketChannel client;

    private final String ip;

    private final int port;

    public NetworkClient(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        client = AsynchronousSocketChannel.open();
    }

    public void connect(CompletionHandler<Void, AsynchronousSocketChannel> completionHandler) {
        client.connect(new InetSocketAddress(ip, port), client, completionHandler);
//        new CompletionHandler<Void, AsynchronousSocketChannel>() {
//            @Override
//            public void completed(Void result, AsynchronousSocketChannel attachment) {
//                System.out.println("连接成功");
//            }
//
//            @Override
//            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
//                System.out.println("连接失败");
//            }
//        });

    }

    CompletionHandler<Integer, ByteBuffer> writer = new CompletionHandler<Integer, ByteBuffer>() {
        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if (attachment.hasRemaining())
                client.write(attachment, attachment, this);
            else
                System.out.println("发送成功 ");
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("发送失败");
        }
    };

    public void send(String msg) {
        send(msg.getBytes());
    }

    public void send(byte[] msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        client.write(buffer, buffer, writer);
    }

    public void close() {
        try {
            client.shutdownInput();
            client.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}