package com.jay.controller;

import com.rabbitmq.client.*;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Client {

   public String sayToServer(String username) throws IOException, TimeoutException, InterruptedException {

       String exchangeName = "rpc_exchange";  //交换器名称
       String queueName = "rpc_queue";    //队列名称
       String routingKey = "rpc_key"; //路由键

       ConnectionFactory factory = new ConnectionFactory();
       factory.setVirtualHost("/");
       factory.setHost("localhost");
       factory.setPort(5672);
       factory.setUsername("guest");
       factory.setPassword("guest");  //创建链接

       Connection connection = factory.newConnection();
       Channel channel = connection.createChannel();
       channel.exchangeDeclare(exchangeName, "direct", false, false, null); //定义交换器
       channel.queueDeclare(queueName, false, false, false, null);  //定义队列
       channel.queueBind(queueName, exchangeName, routingKey, null); //绑定队列
       String callbackQueue = channel.queueDeclare().getQueue();  //获得匿名的独立的默认队列
       String correlationId = UUID.randomUUID().toString();  //产生一个关联Id correlationId
       QueueingConsumer consumer = new QueueingConsumer(channel); //创建一个消费者
       channel.basicConsume(callbackQueue, true, consumer);  //消费信息

       AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()  //创建消息属性
               .correlationId(correlationId)   //携带唯一的correlationID
               .replyTo(callbackQueue).build();  //携带callback 回调的队列路由键
       channel.basicPublish(exchangeName, routingKey, basicProperties, SerializationUtils.serialize(username));

       String response = null;

       while (true) {
           QueueingConsumer.Delivery delivery = consumer.nextDelivery();  //循环获得消息
           if (correlationId.equals(delivery.getProperties().getCorrelationId())) { //匹配CorrelationId是否与发出去的correlationId一致
               response = (String) SerializationUtils.deserialize(delivery.getBody());
               break;
           }
       }
        channel.close();
        connection.close();

        //关闭连接
       return response;
   }

    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
        Client client=new Client();
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入你的昵称: ");
        String name=sc.next();
        System.out.println("您的昵称是"+name);
        System.out.println("成功登陆聊天室,输入你想说的话按回车即可发送,按q退出聊天室");
        while(true){
            String content=sc.next();
            if(content.equals("q")){
                System.out.println("您已退出聊天室");
                break;
            }else{
                String response=client.sayToServer(content);
                System.out.println(name+"说"+response);
            }
        }


        //String respnse=client.sayToServer("Bob");
        //System.out.println("server response: "+respnse);
    }

}
