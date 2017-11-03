package com.lee;

import com.lee.entity.NetworkClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private CheckBox chbHexInput;

    private StringBuilder sbInput;

    private AsynchronousSocketChannel channel;

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
            channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress(ip, port), channel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
                @Override
                public void completed(Void result, AsynchronousSocketChannel attachment) {
                    setUIEnable(true);
                    client = new NetworkClient(attachment, MainAppController.this);
                    client.start();
                    System.out.println("连接成功");
                }

                @Override
                public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                    System.out.println("连接失败");
                    setUIEnable(false);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void shutdown() {
        try {
            client.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

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

    public void setUIEnable(boolean isConnected) {
        btnConnect.setDisable(isConnected);
        btnClose.setDisable(!isConnected);
        btnSend.setDisable(!isConnected);
    }

    @FXML
    private void isSelectedOnOutBuffer() {
        String str = txtOutput.getText();
        StringBuilder strOutput = new StringBuilder();

        if (chbHexOutput.isSelected())
            for (Byte bt : str.getBytes())
                strOutput.append(Integer.toHexString(bt)).append(" ");
        else {
            String[] strArray = str.split(" ");
            byte[] btArray = new byte[strArray.length];
            int i = 0;
            for (String s : strArray)
                if (!s.equals(""))
                    btArray[i++] = (byte) Integer.parseInt(s, 16);

            strOutput.append(new String(btArray));
        }
        strOutput.append("\n");

        txtOutput.setText(strOutput.toString().trim());
    }

    public void showInput(byte[] msg) {
        sbInput.append("[ 接收 ]\n");
        if (chbHexInput.isSelected())
            for (byte bt : msg)
                sbInput.append(Integer.toHexString(bt)).append(" ");
         else
            sbInput.append(new String(msg));

        sbInput.append("\n");
        txtInput.setText(sbInput.toString());
        txtInput.positionCaret(sbInput.length());
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

    public void close() {

    }
}
