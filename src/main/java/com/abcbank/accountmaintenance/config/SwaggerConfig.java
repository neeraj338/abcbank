package com.abcbank.accountmaintenance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {  
	
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)
				// .groupName("api-default")
          .select()
				.apis(RequestHandlerSelectors.basePackage("com.abcbank.accountmaintenance.controller"))
          //.apis(RequestHandlerSelectors.any())              
				// .paths(PathSelectors.regex("(?!/v2).+"))
          .build()
          
          .enable(true)
          ;                                           
    }
    
}