package kh.springboot.admin.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final BoardService bService;
	private final MemberService mService;

	@GetMapping("home")
	public String moveToMainAdmin(Model model) {
		File f = new File("D:/logs/member/");
		File[] files = f.listFiles();
		
		// 키가 가지고 있는 정렬 기준?(스트링은 내부에다 오름차순 정렬.컨퍼러블이 implements가 내장되어있음, 다른기준 컴퍼레이터?)
		TreeMap<String, Integer> dateCount = new TreeMap<String, Integer>();
				//날짜	횟수	: TreeMap: 정렬을 쓰기 위해 만든 것
		BufferedReader br = null;
		try {
		for (File file : files) {
			// System.out.println(file); // "D:/logs/member/"의 파일들을 전부 콘솔에 찍어줌
			// 문자기반의 fileReader(한국어 사용)
			// buffered는 기반스트림의 종류(문자/바이트)/ 입력 or 출력
				br = new BufferedReader(new FileReader(file));
				// 바꾸는 방법 : 변수 사용
				String data;
				while ((data = br.readLine()) != null) {
					// System.out.println(br.readLine());
					// readLine : 줄바꿈이 이뤄지기 전 무조건 한 줄을 읽어 옴
					String date = data.split(" ")[0];
					if(dateCount.containsKey(date)) {	// containsKey : 키가 있으면
						dateCount.put(date, dateCount.get(date) + 1);
					}else {
						dateCount.put(date, 1);
				}
			}
		}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
			}
		}

		ArrayList<Board> list = bService.selectRecentBoards();
		
		System.out.println(list);
		
		
		model.addAttribute("dateCount", dateCount);
		model.addAttribute("list", list);
		
		return "admin";
	}
	
	@GetMapping("members")
	public String selectMembers(Model model) {
		ArrayList<Member> list = mService.selectMembers();
		model.addAttribute("list", list);
		return "members";
	}
	
	@GetMapping("boards")
	public String selectBoard(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
		/*
		// 쿼리 : 게시글 개수(getListCount)
			select count(*)
			from board
			where board_type = 1
		*/
		int listCount = bService.getListCount(-1);	// 음수를 사용해서 뭐?(강의 09:05)
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 10);
		ArrayList<Board> list = bService.selectBoardList(pi, -1);
		
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		
		return "boards";
	}

	// 페이징 처리하고 데이터 넘기기
	@GetMapping("attms")
	public String selectAttms(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
		int listCount = bService.getListCount(-2);	// -2가 admin을 말하는 것임..								// 현재 url을 유지하기 위해서?
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 10);
		ArrayList<Board> list = bService.selectBoardList(pi, -2);	// 그냥 2는 일반 게시물이기 때문에 -2로 admin을 찾아줌?
		ArrayList<Attachment> aList = bService.selectAllAttm();
		
		model.addAttribute("list", list);
		model.addAttribute("aList", aList).addAttribute("pi", pi).addAttribute("aList", aList);
        model.addAttribute("loc", request.getRequestURI());

		return "attms";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}