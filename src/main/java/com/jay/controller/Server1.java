package com.jay.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息发送端,使用fanout方式,即广播消息,不需要使用queue,发送端不需要关心谁接收
 */
public class Server1 {
    private final static Integer port=5672;
    private final static String host="localhost";
    private final static String username="guest";
    private final static String password="guest";
    private final static String virtualHost="/";
    private final static String EXCHANGE_NAME="chatroom";
    private static Connection connection;
    private static Channel channel;
    static{
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setPort(port);
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) throws IOException, TimeoutException {

        channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());
        channel.close();
        connection.close();
    }
}


