package com.lee;

import com.lee.entity.NetworkClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;

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

    @FXML
    private CheckBox chbHexOutput;

    private StringBuilder sbInput;

    private NetworkClient client;

    public MainAppController() throws IOException {
        sbInput = new StringBuilder();
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
    private void shutdown() {
        try {
            client.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setUIEnable(false);
    }

    @FXML
    private void clearInputBuffer() {
        sbInput.delete(0, sbInput.length());
        txtInput.setText(sbInput.toString());
    }

    @FXML
    private void send() {
        String msg = txtOutput.getText();

        if (chbHexOutput.isSelected())
            sendAsHex(msg);
        else
            sendAsChar(msg);

        txtInput.setText(sbInput.append("发送\n").append(msg).append("\n").toString());
    }

    private void sendAsHex(String msg) {
        String[] strArray = msg.split(" ");
        byte[] btArray = new byte[strArray.length];
        int i = 0;
        for (String str : strArray)
            btArray[i++] = (byte) Integer.parseInt(str, 16);

        client.send(btArray);

    }

    private void sendAsChar(String msg) {
        client.send(msg);
    }

    private void setUIEnable(boolean isConnected) {
        btnConnect.setDisable(isConnected);
        btnClose.setDisable(!isConnected);
        btnSend.setDisable(!isConnected);
    }

    @FXML
    private void isSelectedOnOutBuffer() {
        String str = txtOutput.getText();

        if (chbHexOutput.isSelected())
            str = strAsHex(str);
        else
            str = hexAsString(str);

        txtOutput.setText(str);
    }

    private String strAsHex(String msg) {
        StringBuilder sb = new StringBuilder();
        boolean isByteH = false;   // 判断当前位时高位还是低位
        for (int i=0; i<msg.length(); i++) {
            sb.append(msg.substring(i, i + 1));
            isByteH = !isByteH;

            if (!isByteH)
                sb.append(" ");
        }

        return sb.toString().trim();
    }

    private String hexAsString(String msg) {
        return msg.replace(" ", "");
    }
}
