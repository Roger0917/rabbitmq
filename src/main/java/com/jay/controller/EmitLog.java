package com.jay.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class EmitLog {
    private static final String EXCHANGE_NAME="logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        System.out.println("输入q退出聊天室!");
        boolean flag=true;
        //分发信息
        while (flag){
            Scanner sc=new Scanner(System.in);
            String message=sc.nextLine();
            if(message.equals("q")){
                flag=false;
                break;
            }
            channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());
            System.out.println("Emit log said"+" "+message);
        }
        channel.close();
        connection.close();

    }

}
