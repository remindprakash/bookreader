package com.bookreader.app.config;



import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Log4jConfig {
	
	static final Logger logger = Logger.getLogger(Log4jConfig.class);
	
	@Bean
	public Logger rootLogger() throws IOException{
		
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.DEBUG);
		
		//Define log pattern layout
		PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");
		
		//Add console appender to root logger
		rootLogger.addAppender(new ConsoleAppender(layout));
		
		/*RollingFileAppender fileAppender = new RollingFileAppender(layout, "minnool.log");
		
		//Add the appender to root logger
		rootLogger.addAppender(fileAppender);
		
		logger.info("Welcome to Log4j");*/
		return rootLogger;
	}
	
	/*@Bean
    public ConsoleAppender consoleAppender() {
        ConsoleAppender consoleAppender = new ConsoleAppender();
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern("%d{ISO8601} [%t] %-5p %c %x - %m%n");
        consoleAppender.setLayout(patternLayout);
        return consoleAppender;
    }*/
	
	
	/*@Bean
    public FileAppender fileAppender() {
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setThreshold(Level.ALL);
        fileAppender.setFile("minnool.log");
        fileAppender.setMaxFileSize("100KB");
        fileAppender.setMaxBackupIndex(1);
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern("%d{ISO8601} [%t] %-5p %c %x - %m%n");
        fileAppender.setLayout(patternLayout);
        return fileAppender;
    }*/
	
	/*public static void main(String arg[]) throws IOException{
		Log4jConfig obj=new Log4jConfig();
		
		obj.rootLogger();
	}*/
	
	
	
}
