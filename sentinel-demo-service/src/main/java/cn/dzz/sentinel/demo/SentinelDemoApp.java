package cn.dzz.sentinel.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SentinelDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(SentinelDemoApp.class, args);
    }

}
