package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        System.out.println("Hello Java");
        SpringApplication.run(DemoApplication.class, args);

    }

}

// API : APplication Interface : 應用程式介面, 負責前後端的溝通, 前端透過 path parameter 在 url 的參數傳遞資訊
//       或在 http request 的 body 以 Json 將資料提供給後端.

//  url? Unified Resource Location
//  http request/response?
//  json?


// ' ' 半型空格
// '　'　全型空格
// abcde 半型字母
// ａｂｃｄｅ全型字母
// 全型半型切換 : Shift + 空格(Space)