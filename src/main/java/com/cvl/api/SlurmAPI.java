package com.cvl.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.net.InetAddress;

@SpringBootApplication
@EnableScheduling
public class SlurmAPI {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SlurmAPI.class, args);
    }

}
