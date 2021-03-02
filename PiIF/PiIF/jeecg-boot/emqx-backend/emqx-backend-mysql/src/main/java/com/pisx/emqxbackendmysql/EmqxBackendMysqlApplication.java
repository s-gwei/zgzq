package com.pisx.emqxbackendmysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pisx.emqxbackendmysql.mapper")
public class EmqxBackendMysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmqxBackendMysqlApplication.class, args);
    }

}
