package com.hailin.iot.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hailin
 */
@SpringBootApplication
public class RouteApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class , args);
        LOGGER.info("启动route成功");
    }

    @Override
    public void run(String... args) throws Exception {

        //监听服务
        Thread thread = new Thread();
        thread.setName("");
        thread.start();
    }
}
