package com.fss.dev;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class MVCConfig {
		
	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(PathSelectors.ant("/api/*"))
				.apis(RequestHandlerSelectors.basePackage("com.fss.dev"))
				.build()
				.apiInfo(getApiInfo())
				;
	}
	
	public ApiInfo getApiInfo() {
		return new ApiInfoBuilder()
				.title("Mastercard Incoming File Parser Api (*_*)") 
				.description("This Api helps in parsing Master card Incoming file, and gives you JSON data.")
				.version("v1.0.0")
				.contact(new Contact("Mandar Dongare", "https://www.fsstech.com/", "mandard@fss.co.in"))
				.license("Open-source")
				.licenseUrl("https://www.fsstech.com/")
				.build();
	}

}
