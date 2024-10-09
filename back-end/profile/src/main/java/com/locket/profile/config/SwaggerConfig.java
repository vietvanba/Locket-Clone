package com.locket.profile.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI profileOpenAPI() {
        Info info = new Info().title("Profile Service API")
                .description("This is the REST API for profile service")
                .version("1.0.0")
                .license(new License().name("Apache 2.0"));
        ExternalDocumentation externalDocs = new ExternalDocumentation()
                .description("You can refer to the Profile wiki documentation")
                .url("https://github.com/locket/profile/");
                ;
        return new OpenAPI()
                .info(info)
                .externalDocs(externalDocs);
    }
}