package com.jay.controller;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 消息接收端
 */
public class Client1 {
    private final static Integer port=5672;
    private final static String host="localhost";
    private final static String username="guest";
    private final static String password="guest";
    private final static String virtualHost="/";
    private final static String EXCHANGE_NAME="chatroom";
    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入昵称: ");
        String name=sc.next();
        System.out.println("您的昵称是"+name);
        login();
        System.out.println("成功登陆聊天室,输入你想说的话按回车即可发送,按q退出聊天室");
        while(true){
            String content=sc.next();
            if(content.equals("q")){
                System.out.println("您已退出聊天室");
                break;
            }else{
                Server1.sendMessage(name+" "+"said"+" "+content);
                //String response=client.sayToServer(content);
                //System.out.println(name+"说"+response);
            }
        }
    }

    public static void login() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setPort(port);
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        Connection connection=connectionFactory.newConnection();
        Channel channel=connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //得到一个随机名称的queue
        String queueName=channel.queueDeclare().getQueue();
        //将queue绑定到exchange上接收消息,第三个参数Routing key为空,即所有消息都接收,不为空的话在exchange type为fanout该值被忽略
        channel.queueBind(queueName,EXCHANGE_NAME,"");
        System.out.println("waiting for messages");
        //QueueingConsumer consumer=new QueueingConsumer(channel);
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                String message = new String(body, "utf-8");
                System.out.println(message);
            }
        };
        channel.basicConsume(queueName,true,consumer);

       /* while(true){
            QueueingConsumer.Delivery delivery=consumer.nextDelivery();
            String message=new String(delivery.getBody());

            System.out.println("messge"+message);
        }*/
    }

}
