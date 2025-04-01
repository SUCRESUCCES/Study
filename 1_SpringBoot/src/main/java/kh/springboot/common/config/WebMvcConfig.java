package kh.springboot.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kh.springboot.common.interceptor.CheckAdminInterceptor;
import kh.springboot.common.interceptor.CheckLoginInterceptor;
import kh.springboot.common.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**") // 매핑 uri 설정 ( ex. 파일을 가지고 올 때 경로를 어떻게 쓸지 결정)
		.addResourceLocations("file:///c:/uploadFiles/", "classpath:/static/", "file:///c:/profiles/");	// 정적 리소스 위치(자원의 위치)
			}

	@Override
			public void addInterceptors(InterceptorRegistry registry) {
//				registry.addInterceptor(new TestInterceptor()) // 인터셉터 등록
//					    .addPathPatterns("/**");// 인터셉터가 가로챌 url 등록 //가변인자는 갯수를 지정하지않은것
			registry.addInterceptor(new CheckLoginInterceptor())
					.addPathPatterns("/member/myInfo", "/member/logout", "/member/edit", "/member/updatePassword", "/member/delete")
					.addPathPatterns("/board/**", "/attm/**")
					.excludePathPatterns("/board/list", "/attm/list", "/board/top");
			
			registry.addInterceptor(new CheckAdminInterceptor())
            .addPathPatterns("/admin/**");
			
			registry.addInterceptor(new LoginInterceptor())
					.addPathPatterns("/member/signIn");
	}
		}

