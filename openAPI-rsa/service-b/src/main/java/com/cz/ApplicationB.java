package com.cz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-22
 */
@SpringBootApplication
public class ApplicationB {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        SpringApplication.run(ApplicationB.class, args);
    }
}