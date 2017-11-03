package com.lee.entity;

import com.lee.MainAppController;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class NetworkClient {

    private MainAppController controller;

    private AsynchronousSocketChannel client;

    public NetworkClient(AsynchronousSocketChannel client, MainAppController controller) {
        this.client = client;
        this.controller = controller;
    }

    public void start() {
        readResponse();
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

    void readResponse() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        read(buffer);
    }

    void read(ByteBuffer buffer) {
        client.read(buffer, buffer, reader);
    }

    CompletionHandler<Integer, ByteBuffer> reader = new CompletionHandler<Integer, ByteBuffer>() {
        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if (result > 0) {
                byte[] btArray = new byte[result];
                System.out.println(result);
                System.out.println(attachment.position());
                for (int i=0; i< result; i++)
                    btArray[i] = attachment.get(i);
                attachment.clear();
                System.out.println("接收完成");
                controller.showInput(btArray);
                read(attachment);
            } else {
                System.out.println("服务器关闭");
                try {
                    shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("接收失败");
        }
    };

    public void send(String msg) {
        send(msg.getBytes());
    }

    public void send(byte[] msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        client.write(buffer, buffer, writer);
    }

    public void shutdown() throws IOException {
        client.shutdownInput();
        client.shutdownOutput();
    }

    public void close() throws IOException {
        client.close();
    }

}
