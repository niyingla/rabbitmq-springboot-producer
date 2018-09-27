package com.pikaqiu.springboot.producer;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.pikaqiu.springboot.entity.Order;

/**
 * @author xiaoye
 */
@Component
public class RabbitSender {

    //自动注入RabbitTemplate模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //回调函数: confirm确认
    final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {

        @Override//
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            //自定义唯一标识
            System.err.println("correlationData: " + correlationData);
            //true 代表送达了
            System.err.println("ack: " + ack);
            System.out.println(cause);
            if (!ack) {
                System.err.println("异常处理....");
            }
        }
    };

    //回调函数: return返回
    final ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
                                    String exchange, String routingKey) {
            System.err.println("return exchange: " + exchange + ", routingKey: "
                    + routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
        }
    };

    //发送消息方法调用: 构建Message消息
    public void send(Object message, Map<String, Object> properties) throws Exception {
        //springframework的类放置消息属性
        MessageHeaders mhs = new MessageHeaders(properties);
        //创建消息 传入object的消息内容 和 消息属性
        Message msg = MessageBuilder.createMessage(message, mhs);
        //设置去确认消息发送类
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //设置消息不可达处理类
        rabbitTemplate.setReturnCallback(returnCallback);
        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData("12345678903");
        rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, correlationData);
    }

    //发送消息方法调用: 构建自定义对象消息
    public void sendOrder(Order order) throws Exception {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData("09876543212");
        rabbitTemplate.convertAndSend("exchange-2", "springboot.def", order, correlationData);
    }

}
