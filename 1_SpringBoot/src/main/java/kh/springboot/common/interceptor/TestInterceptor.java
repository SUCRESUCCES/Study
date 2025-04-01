package kh.springboot.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestInterceptor implements HandlerInterceptor {	// 상속받음...구현?..
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)	// Object handler : 수행될 핸들러의 정보(메소드의 정보)
			throws Exception {
		// DispatcherServlet이 Controller를 호출하기 전에 수행(Controller로 들어가기 전)
		
		System.out.println("====================== START ======================");
		System.out.println(request.getRequestURI() + "\n");
		return HandlerInterceptor.super.preHandle(request, response, handler);	// 무조건 true값을 리턴함(boolean)
	}
	
	@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
				ModelAndView modelAndView) throws Exception {	//modelAndView가 왜 담겨있음?
			// Controller에서 DisparcherServlet으로 리턴되는 순간에 진행
			System.out.println("---------------------- view ----------------------");
			System.out.println(request.getRequestURI());
			if(modelAndView != null) {
				System.out.println(modelAndView.getModel());
				System.out.println(modelAndView.getViewName() + "\n");
			}
		}
	
	@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) // 완료 후에 수행이 되니 에러도 잡아줘야돼서 exception도 들어가있음
				throws Exception {
			// 뷰에서 모든 작업이 완료된 후에 수행
		System.out.println("~~~~~~~~~~~~~~~~~~~~~ END ~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(request.getRequestURI() + "\n");
		}

}
