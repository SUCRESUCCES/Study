package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration // 설정 파일 클래스를 빈으로 등록(빈 생성 = 객체 생성)
public class SecurityConfig { // 설정 파일의 역할을 할 클래스
	
	@Bean
	public BCryptPasswordEncoder getPasswordEncoding() {
		return new BCryptPasswordEncoder();
	}

}
