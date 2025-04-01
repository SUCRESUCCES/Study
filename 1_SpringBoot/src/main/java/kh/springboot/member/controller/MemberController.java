package kh.springboot.member.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 컨트롤러 역할을 할 bean 생성
// 프레임워크 객체생성
@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
@RequestMapping("/member/")
@Slf4j
public class MemberController {

	// 의존성 주입 1. 필드 주입 @Autowired

	// @Autowired
	// private MemberService mService = new MemberService();
	// private MemberService mService; // 프레임워크에서 IOC 객체 생성도 해줘서 안해줘도됨

	// 의존성 주입 2. 생성자 주입 @RequiredArgsConstructor + final
	// @RequiredArgsConstructor : 특정 변수(final이 붙은 상수 혹은 @NonNull이 붙은 변수)만 가지고 생성자 생성
	private final MemberService mService;

	private final BCryptPasswordEncoder bcrypt;

	private Logger log = LoggerFactory.getLogger(MemberController.class);
	
	// 빈 주입받기 위해 final
	//private final JavaMailSender mailSender;
	
	@GetMapping("signIn")
	// 로그인 화면 연결 메소드
	public String signIn() {
		System.out.println(bcrypt.encode("1234"));
		System.out.println(bcrypt.encode("pass01"));
		System.out.println(bcrypt.encode("pass02"));

		return "/login";
	}

	/***** 파라미터 전송받는 방법 *****/
	// 로그인

	// 1. (Servlet방식) HttpServletRequest 이용
//	@PostMapping("/member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println("id2 : " + id);
//		System.out.println("pw2W : " + pwd);
//		}

	// 2. @RequestParam 이용
	// value view에서 받아올 파라미터 이름(view의 name)이 들어가는 곳
	// @RequestParam에 들어가는 속성이 value 하나 뿐이라면 생략 가능
	// defaultValue 값이 null이거나 들어오지 않았을 때 기본적으로 들어갈 데이터를 지정하는 속성 (기본값)
	// required 기본 값 true, 지정한 파라미터가 꼭 필요한(필수적인)변수인지 설정하는 속성

	/*
	 * @PostMapping("/member/signIn") // public void login(@RequestParam("id")
	 * String userId, @RequestParam("pwd") String userPwd) { public void
	 * login(@RequestParam(value="id", defaultValue="testId") String userId,
	 * 
	 * @RequestParam("pwd") int userPwd, @RequestParam(value="tt", required=false)
	 * String t) { // tt = null // @RequestParam("pwd") int
	 * userPwd, @RequestParam(value="tt", defaultValue="tt") String t) { // tt =
	 * tt(기본값 지정해줬으니 tt로 나옴) // 파싱 안해줘도 됨(requestParam) System.out.println("id1 : "
	 * + userId); System.out.println("pw1 : " + userPwd); System.out.println("tt : "
	 * + t); }
	 */

//	 3. @RequestParam 생략
//	@PostMapping("/member/signIn")
//		public void login(String id, String pwd) { 		
//		System.out.println("id3 : " + id);
//		System.out.println("pw3 : " +pwd);
//		}

//	// 3. @ModelAttribute 이용
//	// 		기본 생성자와 setter를 이용한 주입 방식
//	// 		(둘 중 하나가 없으면 작동X) ->  settr명과 파라미터명이 같아야함!
//	@PostMapping("/member/signIn")
//	public void login(@ModelAttribute Member m) {
//		System.out.println("id4 : " + m.getId());
//		System.out.println("pw4 : " + m.getPwd());
//	}

	// 5. @ModelAttribute 생략
	/*
	 * @PostMapping("/member/signIn") public String login(Member m, HttpSession
	 * session ) { //System.out.println("id5 : " + m.getId());
	 * //System.out.println("pw5 : " + m.getPwd()); Member loginUser =
	 * mService.login(m); if(loginUser != null) { session.setAttribute("loginUser",
	 * loginUser); return "redirect:/home"; }else { // throw : 오류를 강제 발생시키기(사용자 정의
	 * 예외) throw new MemberException("로그인을 실패하였습니다."); } }
	 */
	/*
	 * @GetMapping("/member/logout") public String logout(HttpSession session) {
	 * session.invalidate(); // 세션 날려주고 return "redirect:/home";// redirect }
	 */

	@GetMapping("/enroll")
	public String enroll() {
		// 로그 레벨 : DEBUG INFO WARN < ERROR < FATAL
		// fatal		: 아주 심각한 에러
		// error 		: 어떤 요청 처리 중 문제 발생
		// warn			: 프로그램 실행에는 문제가 없지만 향후 시스템 에러의 원인이 될 수 있다는 경고성 메세지
		// info			: 정보성 메세지
		// debug		: 디버깅 용도로 사용하는 메세지
		// trace		: 디버그 레벨이 너무 광범위한 것을 해결하기 위해 좀더 상세한 이벤트를 나타냄
		
		// log.fatal("회원가입 페이지"); 제공을 안함
		log.error("회원가입 페이지");
		log.warn("회원가입 페이지");
		log.info("회원가입 페이지");
		log.debug("회원가입 페이지");
		log.trace("회원가입 페이지");
		
		return "enroll";
	}
	// 
	
	
	
	
	

	// @ModelAttribute
	@PostMapping("/enroll")
	public String enroll(@ModelAttribute Member m, @RequestParam("emailId") String emailId,
			@RequestParam("emailDomain") String emailDomain) {
		if (!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		m.setPwd(bcrypt.encode(m.getPwd()));
		// $2a$10$9Z0D0IFY.awGifIndDxdd.F.6h1xirAI7u0Lf2zkDH2qC4sGUzVdi
		// $2a$10$G1bCBP9eEDEoxZDdpnQlfe1aYracJnCNmwm2Q9Jxvd/x5j3FftsyC
		// 암호가 계속 다른거로 나와서 보안에 좋다
		// System.out.println(m);

		int result = mService.insertMember(m);
		if (result > 0) {
			return "redirect:/home";
		} else {
			throw new MemberException("회원가입을 실패하였습니다.");
		}
	}

	// 암호화 후 로그인
	/*
	 * @PostMapping("/member/signIn") public String login(Member m, HttpSession
	 * session) {
	 * 
	 * Member loginUser = mService.login(m); if (loginUser != null &&
	 * bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
	 * session.setAttribute("loginUser", loginUser); return "redirect:/home"; } else
	 * { throw new MemberException("로그인을 실패하였습니다."); } }
	 */

	/****** 요청 후 전달하고자 있는 데이터가 있는 경우 ******/
	// 1. Model 이용
	// 맵 형식(key, value)으로 데이터를 담아 request scope에 데이터를 담아 전달
	// 내 정보 조회
	/*
	 * @GetMapping("/member/myInfo") public String myInfo(HttpSession session, Model
	 * model) { Member loginUser = (Member) session.getAttribute("loginUser"); if
	 * (loginUser != null) { String id = loginUser.getId();
	 * 
	 * ArrayList<HashMap<String, Object>> list = mService.selectMyList(id); //
	 * System.out.println(list); model.addAttribute("list", list); }
	 * 
	 * return "views/member/myInfo"; }
	 */

	// 2. ModelAndView 이용
	// Model + View
	@GetMapping("/myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		if (loginUser != null) {
			String id = loginUser.getId();

			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
			// System.out.println(list);
			
			ArrayList<TodoList> todoList = mService.selectTodoList(id);
			
			mv.addObject("list", list).addObject("todoList", todoList);
			mv.setViewName("/myInfo");
		}

		return mv;
	}

	// 암호화 후 로그인 + @SessionAttiributes
	// @SessionAttributes는 model에 attribute가 추가될 때 자동으로 키 값을 찾아 세션에 등록하는 어노테이션
	@PostMapping("/signIn")
	public String login(Member m, Model model, @RequestParam("beforeURL") String beforeURL) {

		Member loginUser = mService.login(m);
		if (loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser", loginUser);
			
			// 로그인 시 계정이 관리자면 /admin/home으로 넘어가게 하기
			if(loginUser.getIsAdmin().equals("Y")) {
				return "redirect:/admin/home";
			}else {
				// log.debug(m.getId());
				return "redirect:" + beforeURL;
			}
		} else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	}

	// @SessionAttributes 추가 후 로그아웃
	@GetMapping("/logout")
	public String logout(SessionStatus session) {
		session.setComplete();
		return "redirect:/home";
	}

	@GetMapping("/edit")
	public String edit() {
		return "/edit";
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Member m, @RequestParam("emailId") String emailId,
			@RequestParam("emailDomain") String emailDomain, Model model) {
		if (!emailId.trim().equals("")) { // emailId가 비어있지 않으면
			m.setEmail(emailId + "@" + emailDomain);
		}

		System.out.println(m.getEmail());
		int result = mService.updateMember(m);
		if (result > 0) {
			model.addAttribute("loginUser", mService.login(m));
			return "redirect:/member/myInfo";
		} else {
			throw new MemberException("회원정보 수정을 실패했습니다.");
		}
	}

	@PostMapping("updatePassword")
	public String updatePassword(@RequestParam("currentPwd") String pwd,
								 @RequestParam("newPwd") String newPwd, 
								 Model model) {
		
		Member m = (Member)model.getAttribute("loginUser");

		if (bcrypt.matches(pwd, m.getPwd())) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", m.getId());
			map.put("newPwd", bcrypt.encode(newPwd));

			int result = mService.updatePwd(map);
			if (result > 0) {
				model.addAttribute("loginUser", mService.login(m));
				return "redirect:/home";
		} else {
			throw new MemberException("비밀번호 수정을 실패하였습니다.");
		}
	}else {
		throw new MemberException("비밀번호 수정을 실패하였습니다."); 
	}
}
	
	@GetMapping("delete")
	public String delete(HttpSession session) {
		int result = mService.deleteMember(((Member)session.getAttribute("loginUser")).getId());
		if(result > 0) {
			return "redirect:/member/logout";
		}else{
			throw new MemberException("회원탈퇴를 실패하였습니다.");
		}
	}
	
/*	@GetMapping("checkId")
	public void checkId(@RequestParam("id") String id, PrintWriter out) {
		int count = mService.checkId(id);
		out.print(count);
	}
	
	@GetMapping("checkNickName")
	@ResponseBody	// view이름이 아닌 데이터 자체로 보내겠다
	public String checkNickname(@RequestParam("nickName") String nickname) {
		int count = mService.checkNickName(nickname);
		return count == 0 ? "usable" : "unusable";
	}*/
	
//	Ajax Controller에서 RestController로 옮겨줌!!!
//	@GetMapping("checkValue")
//	@ResponseBody
//	public int checkValue(@RequestParam("value") String value, @RequestParam("column") String column) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("column", column);
//		map.put("value", value);
//		int count = mService.checkValue(map);
//		return count;
//		}
	
//	@PostMapping("profile")
//	@ResponseBody
//	public int updateProfile(@RequestParam(value="profile", required=false) MultipartFile profile, Model model) {
//		//System.out.println(profile);
//		
//		Member m = (Member)model.getAttribute("loginUser");
//		
//		String savePath = "c:\\profiles";
//		File folder = new File(savePath);
//		if(!folder.exists()) folder.mkdirs();	// if문이 한줄이면 중괄호 생략 가능 두줄이상부터는 중괄호 생략 불가능.. 근데 중괄호 없는 것보다 있는게 좋긴함
//		
//		if(m.getProfile() != null) {
//			File f = new File(savePath + "\\" + m.getProfile());
//			f.delete();
//		}
//		
//		String renameFileName = null;
//		if(profile != null) {
//			
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//		int ranNum = (int)(Math.random()*1000000);
//		String originFileName = profile.getOriginalFilename();
//		renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
//		
//		try {
//			profile.transferTo(new File(folder + "\\" + renameFileName));
//		} catch (IllegalStateException | IOException e) {
//			e.printStackTrace();
//		}
//	
//		}
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("id", m.getId());
//		map.put("profile", renameFileName);
//		
//		int result = mService.updateProfile(map);
//		if(result > 0) {
//			m.setProfile(renameFileName);
//			model.addAttribute("loginUser", m);
//		}
//		
//		return result;
//		
//		// Ensure that the compiler uses the '-parameters' flag. 이런 오류가 뜨면?
//		
//	}
	
//	@GetMapping("echeck")
//	@ResponseBody
//	public String checkEmail(@RequestParam("email") String email) {
//		MimeMessage mimeMessage = mailSender.createMimeMessage();
//		
//		String subject = "[SpringBoot] 이메일 확인";
//		String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br>";
//		body += "<div style='border: 3px solid green; text-align: center; font-size: 15px;'>본 메일은 이메일을 확인하기 위해 발송되었습니다.<br>";
//		body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br><br>";
//		
//		String random = "";
//		for(int i = 0; i < 5; i++) {
//			random += (int)(Math.random() * 10);
//		}
//		
//		body += "<span style='font-size: 30px; text-decoration: underline;'><b>" + random +"</b></span><br></div>";
//		
//		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
//		try {
//			mimeMessageHelper.setTo(email);
//			mimeMessageHelper.setSubject(subject);
//			mimeMessageHelper.setText(body, true);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
//		mailSender.send(mimeMessage);
//		
//		return random;
//	}
	
	@GetMapping("findIDPW")
	public String findIdPW() {
		return "findIDPW";
	}
	
//	@PostMapping("fid")
//	public String findId(@ModelAttribute Member m, Model model) {
//		String id = mService.findId(m);
//		if(id != null) {
//			model.addAttribute("id", id);
//			return "findId";
//		}else {
//			throw new MemberException("존재하지 않는 회원입니다.");
//		}
//	}
	
//	@PostMapping("fpw")
//	public String findPwd(@ModelAttribute Member m, Model model) {
//		Member member = mService.findPwd(m);
//		if(member != null) {
//			return " resetPw";
//		}else
//			throw new MemberException("존재하지 않는 회원입니다.");
//	}
	
	@PostMapping("fInfo")
	public String findId(@ModelAttribute Member m, Model model) {
		Member member = mService.findInfo(m);
		if(member != null) {
			model.addAttribute("id", member.getId());
			return m.getName() != null ? "findId" : "resetPw";
		}else {
			throw new MemberException("존재하지 않는 회원입니다.");
		}
	}
	
	@PostMapping("fpwUpdate")
	public String updatePwd(@ModelAttribute Member m, Model model) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", m.getId());
		map.put("newPwd", bcrypt.encode(m.getPwd()));
		int result = mService.updatePwd(map);
		if(result > 0) {
			model.addAttribute("msg", "비밀번호 수정이 완료되었습니다.");
			model.addAttribute("url", "/home");
			return "views/common/sendRedirect";
		}else {
			throw new MemberException("비밀번호 수정 실패");
		}
	}
	
//	@GetMapping("linsert")
//	@ResponseBody
//	public int insertTodo(@ModelAttribute TodoList todo) {
//		return mService.insertTodo(todo);
//	}
//	
//	@GetMapping("lupdate")
//	@ResponseBody
//	public int updateTodo(@ModelAttribute TodoList todo) {
//		return mService.updateTodo(todo);
//	}
//	
//	@GetMapping("ldelete")
//	@ResponseBody
//	public int deleteTodo(@RequestParam("num") int num) {
//		return mService.deleteTodo(num);
//	}
	
	
	}