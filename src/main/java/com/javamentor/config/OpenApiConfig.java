package com.javamentor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration - API Documentation
 * Access: /swagger-ui.html (when enabled)
 */
@Configuration
public class OpenApiConfig {

    private final AppConfig appConfig;

    public OpenApiConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(appConfig.getTitle())
                .version(appConfig.getVersion())
                .description("Java面試題庫學習平台 API\n\n功能：\n- 獲取題目\n- 提交答案\n- 查看進度\n- 模擬測試\n\n注意：所有需要登入既操作都通過 Cookie Session 自動處理")
                .contact(new Contact()
                    .name("JavaMentor Team")
                    .url("https://github.com/Kitkwok1012/JavaMentor"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")));
    }
}
