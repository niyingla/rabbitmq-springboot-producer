package com.pikaqiu.springboot;

import com.pikaqiu.springboot.entity.Order;
import com.pikaqiu.springboot.producer.RabbitSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private RabbitSender rabbitSender;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    /**
     * 测试发送消息方法
     * @throws Exception
     */
    @GetMapping("/send")
    public void send() throws Exception {
        Order order = new Order("001", "第一个订单");
        rabbitSender.sendOrder(order);
    }

    @GetMapping("/send1")
    public void send1() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "12345");
        properties.put("send_time", new Date());
        rabbitSender.send("Hello RabbitMQ For Spring Boot!", properties);
    }
}
