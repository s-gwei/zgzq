package com.pisx.emqxbackendpostgresql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pisx.emqxbackendpostgresql.mapper")
public class EmqxBackendPostgresqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmqxBackendPostgresqlApplication.class, args);
    }

}
