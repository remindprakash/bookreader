package com.bookreader.app.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;



@ComponentScan(basePackages = { "com.bookreader.app" })
@Configuration
@Import({ Log4jConfig.class ,DataBaseConfig.class })
public class AppConfig {

	
	@Bean(name = "messageSource")
    public ResourceBundleMessageSource getMessageSource() {
		ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
        resource.setBasename("config/locale/messages");
        resource.setDefaultEncoding("UTF-8");
        resource.setUseCodeAsDefaultMessage(true);
        return resource;
    }
	
	
}
