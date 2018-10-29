package com.demo.shirodemo.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;

@Configuration
public class MyConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/image/**").addResourceLocations("classpath:/static/photo/softImages/");
        registry.addResourceHandler("/image/**").addResourceLocations("redirect:http://img.pythonplus.xyz/");

        super.addResourceHandlers(registry);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("redirect:/swagger-ui.html");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        registry.addViewController("/image/**").setViewName("redirect:http://routany.cn:81/");
        super.addViewControllers(registry);
    }
}

