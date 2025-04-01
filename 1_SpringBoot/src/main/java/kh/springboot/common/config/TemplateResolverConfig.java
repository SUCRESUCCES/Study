package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateResolverConfig {
	
	@Bean //빈등록도 해줘야함
	public ClassLoaderTemplateResolver memberResolver() {
		ClassLoaderTemplateResolver mResolver = new ClassLoaderTemplateResolver();
		mResolver.setPrefix("templates/views/member/");	// templates/
		mResolver.setSuffix(".html");
		mResolver.setTemplateMode(TemplateMode.HTML);
		mResolver.setCharacterEncoding("UTF-8");
		mResolver.setCacheable(false);		// 캐시를 꺼주지 않으면 화면울 바꾸기 위해서는 껐다 켜줬다해야함 /  얘가 자동으로 계속 새로고침 기능을 해줘서
		mResolver.setCheckExistence(true);	// 제일 중요한 것
		
		// 정확한 역할 여부가 없어서??
		return mResolver;
	}
	@Bean
	public ClassLoaderTemplateResolver boardResolber() {
		ClassLoaderTemplateResolver bResolver =  new ClassLoaderTemplateResolver();
		bResolver.setPrefix("templates/views/board/");
		bResolver.setSuffix(".html");
		bResolver.setTemplateMode(TemplateMode.HTML);
		bResolver.setCharacterEncoding("UTF-8");
		bResolver.setCacheable(false);
		bResolver.setCheckExistence(true);
		return bResolver;
	}
	
	@Bean
	public ClassLoaderTemplateResolver adminResolber() {
		ClassLoaderTemplateResolver aResolver =  new ClassLoaderTemplateResolver();
		aResolver.setPrefix("templates/views/admin/");
		aResolver.setSuffix(".html");
		aResolver.setTemplateMode(TemplateMode.HTML);
		aResolver.setCharacterEncoding("UTF-8");
		aResolver.setCacheable(false);
		aResolver.setCheckExistence(true);
		return aResolver;
	}
	
}
