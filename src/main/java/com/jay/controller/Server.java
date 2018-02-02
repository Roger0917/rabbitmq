package com.jay.controller;

import com.rabbitmq.client.*;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        String exchangeName="rpc_exchange";     //交换器名称
        String queueName="rpc_queue";   //队列名称
        String routingKey="rpc_key";    //路由键

        ConnectionFactory factory=new ConnectionFactory();
        factory.setVirtualHost("/");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection=factory.newConnection();  //创建链接
        Channel channel=connection.createChannel();
        channel.exchangeDeclare(exchangeName,"direct",false,false,null);    //定义交换器
        channel.queueDeclare(queueName,false,false,false,null); //定义队列
        channel.queueBind(queueName,exchangeName,routingKey,null);  //绑定队列
        QueueingConsumer consumer=new QueueingConsumer(channel);    //创建一个消费者
        channel.basicConsume(queueName,true,consumer);  //消费信息
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery(); //获得一条信息
            String correlationId = delivery.getProperties().getCorrelationId();   //获得额外携带的correlationId
            String replyTo = delivery.getProperties().getReplyTo();   //获得回调的队列路由键
            String body = (String) SerializationUtils.deserialize(delivery.getBody());    //获得请求的内容
            String responseMsg =  body;  //处理返回内容
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlationId).build();

            channel.basicPublish("", replyTo, properties, SerializationUtils.serialize(responseMsg));  //返回处理结果
        }

    }
}
