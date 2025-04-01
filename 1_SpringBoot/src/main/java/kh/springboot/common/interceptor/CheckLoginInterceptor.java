package kh.springboot.common.interceptor;

import java.util.regex.Pattern;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.vo.Member;

// 로그인이 되어있는지 검사
public class CheckLoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
							// 로그인이 있는지 없는지 확인해야하는 request
			throws Exception {
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser == null) {
			String url = request.getRequestURI();
			String msg = null;
			String regExp = "/(board|attm)/\\d+/\\d+";	// 정규표현식 사용해줘야함 왜??????
										// 숫자가 두자리 이상이라서 + 붙여줌
			// /board/24/1		/attm/3/1	
			if(Pattern.matches(regExp, url)) {
				msg = "로그인 후 이용하세요.";
			}else {
				msg = "로그인 세션이 만료되어 로그인 화면으로 넘어갑니다.";
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write("<script>alert('" + msg + "'); location.href='/member/signIn';</script>");
		
		return false;	// if에 걸리면 이렇게 해줘야함!!
		
		}	
		return HandlerInterceptor.super.preHandle(request, response, handler);
		// true값이니까 그다음 컨트롤러로 요청을 보냄
	}
}
