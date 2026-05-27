package com.project.platform;

import com.project.platform.bootstrap.DatabaseBootstrap;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.project.platform.mapper"})
public class ProjectManagement {
    public static void main(String[] args) {
        if (DatabaseBootstrap.shouldRun(args)) {
            System.exit(DatabaseBootstrap.runFromEnvironment(args));
            return;
        }
        SpringApplication.run(ProjectManagement.class, args);
    }
}
