package com.scienjus.spring.cloud.etcd.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class App {

    @Autowired
    private HelloClient helloClient;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping("hello")
    public String hello() {
        return "world";
    }

    @GetMapping("world")
    public String world() {
        return "hello " + helloClient.hello();
    }

    @FeignClient(name = "application")
    interface HelloClient {
        @GetMapping("hello")
        String hello();
    }
}
