package com.lee;

import com.lee.entity.NetworkClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class MainAppController {

    @FXML
    private TextField txtIP;

    @FXML
    private TextField txtPort;

    @FXML
    private TextArea txtInput;

    @FXML
    private TextArea txtOutput;

    @FXML
    private Button btnSend;

    @FXML
    private Button btnConnect;

    @FXML
    private Button btnClose;

//    final Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

    private NetworkClient client;

    public MainAppController() throws IOException {

    }

    @FXML
    private void connect() {
        String ip = txtIP.getText();
        int port = Integer.valueOf(txtPort.getText());

        System.out.println(ip + ", " + port);
        try {
            client = new NetworkClient(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.connect(connetComplation);
    }

    CompletionHandler<Void, AsynchronousSocketChannel> connetComplation = new CompletionHandler<Void, AsynchronousSocketChannel>() {
        @Override
        public void completed(Void result, AsynchronousSocketChannel attachment) {
            System.out.println("连接成功");
            setUIEnable(true);
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("连接失败");
            setUIEnable(false);
        }
    };

    @FXML
    private void close() {

    }

    @FXML
    private void clearInputBuffer() {

    }

    @FXML
    private void send() {

    }

    private void setUIEnable(boolean isConnected) {
        btnConnect.setDisable(isConnected);
        btnClose.setDisable(!isConnected);
        btnSend.setDisable(!isConnected);
    }
}
