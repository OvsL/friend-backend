package org.example.friend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("好友匹配系统API文档")
                        .version("1.0")
                        .description("好友匹配系统相关接口的文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("本地开发环境")
                ));
    }
}