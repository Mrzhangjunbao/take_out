package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.annotation.WebServlet;

@Slf4j // (...log.info())
@SpringBootApplication
@ServletComponentScan(basePackages = "com.itheima.reggie")  //这样才会扫描filter下的@WebFilter类型的注解
@EnableTransactionManagement(proxyTargetClass = true)  //开启事务注解支持
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
    }
}
