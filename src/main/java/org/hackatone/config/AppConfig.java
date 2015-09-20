package org.hackatone.config;  
  
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
@Configuration 
@ComponentScan("org.hackatone") 
@EnableWebMvc   
public class AppConfig {  

}  
