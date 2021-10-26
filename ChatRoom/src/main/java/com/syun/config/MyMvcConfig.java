package com.syun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
	
	// 控制網頁跳轉
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/admin/main").setViewName("admin/main");
		registry.addViewController("/user/main").setViewName("user/main");
	}
	
	// 設定所有請求都需要經過自訂的攔截器，excludePathPatterns設定的請求除外
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/index", "/", "/login", "/user/add", "/user/add/ajax", "/js/*", "/css/*", "/img/*");
	}
	

}
