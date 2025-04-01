package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.exception.BoardException;
import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private final BoardService bService;
	private final BoardMapper bMapper; // 생성자

	@GetMapping("list") // 알아서 /를 붙여줘서 없어도 상관없다
	public String selectList(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model,
			HttpServletRequest request) {
		int listCount = bService.getListCount(1); // 얘는 보드타입 1번인 일반 게시물을 가지고 온다는 인자

		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);	// 한 페이지의 글이 5개
		ArrayList<Board> list = bService.selectBoardList(pi, 1);
		// 보드타입

		model.addAttribute("list", list).addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		// getRequestURI() : /board/list
		// getRequestURL() : http://localhost:8080/board/list

		return "list";
	}

	@GetMapping("write")
	public String insertBoard() {
		return "write";

	}

	@PostMapping("insert")
	public String insertBoard(@ModelAttribute Board b, HttpSession session) {

		b.setBoardWriter(((Member) session.getAttribute("loginUser")).getId());
		b.setBoardType(1);

		int result = bService.insertBoard(b);
		if (result > 0) {
			return "redirect:/board/list";
		} else {
			throw new BoardException("게시글 작성을 실패하였습니다.");
		}
	}

	@GetMapping("/{id}/{page}") // post방식에서 데이터를 보낼 때 많이 사용?
	public ModelAndView selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page, // @PathVariable 변수의 경로
																									// id라는 걸 가져오겠다
			HttpSession session, ModelAndView mv) {
		// 글 상세조회 + 조회수 수정
		// 내가 내 글 조회 or 비회원 조회 -> 조회수 올라가지 않음
		// ㄴ 현재 로그인한 사람의 아이디 필요
		Member loginUser = (Member) session.getAttribute("loginUser");
		String id = null;
		if (loginUser != null) {
			id = loginUser.getId();
		}
		// bId, id를 service에 넘겨서 글쓴이 비교 로직 작성
		Board b = bService.selectBoard(bId, id);
		ArrayList<Reply> list = bService.selectReplyList(bId);
		// 게시글이 존재하면, 게시글 데이터(b)와 페이지(page)를 /views/board/detail.html로 전달
		// ㄴ write.html 수정해서 사용
		// 게시글이 존재하지 않으면, 사용자 정의 예외 발생
		if (b != null) {
			mv.addObject("list", list);
			mv.addObject("b", b).addObject("page", page).setViewName("detail");
			return mv;
		} else {
			throw new BoardException("게시글 상세조회를 실패하였습니다.");
		}
	}

	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		model.addAttribute("b", b).addAttribute("page", page);
		return "views/board/edit";
	}

	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page) {
		b.setBoardType(1);
		int result = bService.updateBoard(b);
		
		if(result > 0) {
			// return "redirect:/board/" + b.getBoardId() + "/" + page;
			return String.format("redirect:/board/%d/%d", b.getBoardId(), page);
		}else {
			throw new BoardException("게시글 수정을 실패하셨습니다.");
		}
	}

	@PostMapping("delete")
	public String updateForm(@RequestParam("boardId") int bId, HttpServletRequest request) {
		int result = bService.deleteBoard(bId);
		if(result > 0) {
			// return "redirect:/" + (request.getRequestURI().contains("board") ? "board" : "attm") + "/list";
			return "redirect:/" + (request.getHeader("referer").contains("board") ? "board" : "attm") + "/list";
			// 이전의 url 
			
		}else {
			throw new BoardException("게시글 삭제를 실패하셨습니다.");
		}
	}

	
/*
 * // 수정페이지 이동
 * 
 * @PostMapping("updForm") public String updForm(Board b2, Model model,
 * HttpSession session) { // 수정 전의 제목, 내용을 가져오려고 Member loginUser = (Member)
 * session.getAttribute("loginUser"); Board b =
 * bService.selectBoard(b2.getBoardId(), loginUser.getId());
 * model.addAttribute("b", b); return "views/board/edit"; }
 * 
 * // 게시글 수정
 * 
 * @PostMapping("update") public String update(@ModelAttribute Board b) { int
 * result = bService.editBoard(b); } }
 */

	
	
	/* JSON 버전 
	@GetMapping(value="top",  produces="application/json; charset=UTF-8") //  response에 컨텐트타입을 관리(제어,관리, 설정)을 할 수 있게 하는 속성 -> 스프링프레임워크에서만 볼 수 있는 속성으로 세가지 방법 중 이것 권장!
	@ResponseBody
	public String selectTop(HttpServletResponse response) {	// HttpServletResponse 사용해서 객체 형태로 반환
		ArrayList<Board> list = bService.selectTop();
		// System.out.println(list);
		JSONArray array = new JSONArray();
		for(Board b : list) {
			JSONObject json = new JSONObject();		
			json.put("boardId", b.getBoardId());
			json.put("boardTitle", b.getBoardTitle());
			json.put("nickName", b.getNickName());
			json.put("modifyDate", b.getModifyDate());	// 파싱해주지 않아도 됨
			json.put("boardCount", b.getBoardCount());
			
			array.put(json);	// json 라이브러리에서는 add 대신 put 사용
		}
		
		// response.setContentType("application/json; charset=UTF-8"); // 이 방법과 data타입 json 작성 같이 해주는게 좋음 한글 깨질 수 있음(response만 있으면 다른 거에서 사용 가능)
		
		return array.toString();
	}
	
	*/
	
	//GSON 버전
	/*
	 * @GetMapping("top") public void selectTop(HttpServletResponse response) {
	 * ArrayList<Board> list = bService.selectTop();
	 * 
	 * response.setContentType("application/json; charset=UTF-8");
	 * 
	 * // Gson gson = new Gson();
	 * 
	 * // GsonBuilder gb = new GsonBuilder(); GsonBuilder gb = new
	 * GsonBuilder().setDateFormat("yyyy-MM-dd"); Gson gson = gb.create(); try {
	 * gson.toJson(list, response.getWriter()); } catch (Exception e) {
	 * e.printStackTrace();
	 * 
	 * } }
	 */
	   
//	   // JSON 버전
//	   // @GetMapping("rinsert") : HttpServletResponse 이거 사용했을 때!	
//	   @GetMapping(value="rinsert",  produces="application/json; charset=UTF-8")	// 4. response에 컨텐트타입을 관리(제어,관리, 설정)을 할 수 있게 하는 속성
//	   @ResponseBody	// 3. @ResponseBody추가
//	   public String insertReply(@ModelAttribute Reply r /*, HttpServletResponse response*/) {
//		   	int result = bService.insertReply(r);
//		   	ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//		   
//		   	JSONArray array = new JSONArray();	// 1. JSONArray 먼저 만들어준다
//		   
//			   for(Reply R : list) {	// 필요한 것만 put 해준다.			
//				   JSONObject json = new JSONObject();						// 2. Json 형식으로 받아주기 위해 Object 생성
//				   json.put("replyId", R.getReplyId());
//				   json.put("replyContent", R.getReplyContent());
//				   json.put("refBoardId", R.getRefBoardId());
//				   json.put("replyWriter", R.getReplyWriter());
//				   json.put("nickName", R.getNickName());						
//				   json.put("replyCreateDate", R.getReplyCreateDate());
//				   json.put("replyModifyDate", R.getReplyModifyDate());
//				   json.put("replyStatus", R.getReplyStatus());
//				   
//				   array.put(json);
//				   
//			   }
//				
//			   // response.setContentType("application/json; charset=UTF-8");
//			   return array.toString();
//	   }
	   
//	   // GSON 버전
//	   @GetMapping("rinsert")	
//	   public void insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
//		   	int result = bService.insertReply(r);
//		   	ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//		   	// gson은 reply의 vo 따라감
//		   	// json은 내가 쓴 이름대로 뷰에 넘어감
//		   	
//		   	response.setContentType("application/json; charset=UTF-8");
//			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//		   	try {
//				gson.toJson(list, response.getWriter());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}  
//	   }

	   // jackson 버전
//	   @GetMapping(value="rinsert", produces="application/json; charset=UTF-8")
//	   @ResponseBody
//	   public String insertReply(@ModelAttribute Reply r) {
//		   // 저장
//		   int result = bService.insertReply(r);
//		   // 가져오기
//		   ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//		
//		   ObjectMapper om = new ObjectMapper();
//		   
//		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		   om.setDateFormat(sdf);
//		   
//		   String strJson = null;
//		   try {
//			strJson = om.writeValueAsString(list);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		   return strJson;
//	   }
//	   
//	   
//	   @GetMapping("rdelete")
//	   @ResponseBody
//	   public int deleteReply(@RequestParam("rId") int rId) {
//		   return  bService.deleteReply(rId);
//		
//	   }
//	   
//	   @GetMapping("rupdate")
//	   @ResponseBody
//	   public int updateReply(@ModelAttribute Reply r) {
//		   return bService.updateReply(r);
//	   }
	   
	   
	   
	   
	   
	   
	   
	   
	   
} 
