package com.wzh.suyuan.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SuyuanBackendApplication {
    private static final Logger log = LoggerFactory.getLogger(SuyuanBackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SuyuanBackendApplication.class, args);
    }

    @Bean
    public ApplicationRunner startupLogger(Environment environment,
                                           @Value("${app.version:0.1.0}") String appVersion,
                                           @Value("${spring.datasource.url:}") String datasourceUrl) {
        return args -> {
            String profile = String.join(",", environment.getActiveProfiles());
            String dbHost = maskDbHost(datasourceUrl);
            log.info("backend started: appVersion={}, profile={}, dbHost={}", appVersion, profile, dbHost);
        };
    }

    private String maskDbHost(String datasourceUrl) {
        if (datasourceUrl == null || datasourceUrl.isEmpty()) {
            return "unknown";
        }
        int hostStart = datasourceUrl.indexOf("//");
        if (hostStart == -1) {
            return "unknown";
        }
        int hostEnd = datasourceUrl.indexOf("/", hostStart + 2);
        String hostPort = hostEnd == -1 ? datasourceUrl.substring(hostStart + 2) : datasourceUrl.substring(hostStart + 2, hostEnd);
        return hostPort.replaceAll("[^:]+", "***");
    }
}
