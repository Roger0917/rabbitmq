package com.jay.controller;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs1 {
    private static final String EXCHANGE_NAME="logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        //产生一个随机的队列名称
        String queueName=channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,"");  //对队列进行绑定

        System.out.println("ReceiveLogs1 waiting for messages");
        Consumer consumer=new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag,Envelope envelope,AMQP.BasicProperties properties,byte[] body) throws UnsupportedEncodingException {
                String message=new String(body,"UTF-8");
                System.out.println("ReceiveLogs1 received"+" "+message);
            }
        };
        channel.basicConsume(queueName,true,consumer);

    }
}
