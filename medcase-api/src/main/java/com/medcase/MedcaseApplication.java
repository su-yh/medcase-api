package com.medcase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 启动程序
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.medcase.**.mapper")
public class MedcaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedcaseApplication.class, args);
    }
}
