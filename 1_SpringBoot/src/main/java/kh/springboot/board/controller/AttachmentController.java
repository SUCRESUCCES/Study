package kh.springboot.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attm/")	// 뒤에 있는 /는 있어도 되고 없어도 됨
@RequiredArgsConstructor
public class AttachmentController {
	private final BoardService bService; 
	
	@GetMapping("list")
	public String selectList(@RequestParam(value= "page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
		
		// 페이징 처리
		int listCount = bService.getListCount(2);	// 보드 타입이 2인 첨부파일 게시물만 가져오겠다
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9);
		
		ArrayList<Board> bList = bService.selectBoardList(pi, 2);	// 두번째 인자는 보드타입 2...작성글..작성자 등 보드타입을 가져오는 것
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null);	// 썸네일의 사진...가져오는 것?(레벨이 0인 것만 가져올 것임)
		
		if(bList != null) {
			model.addAttribute("bList", bList).addAttribute("aList", aList).addAttribute("pi", pi).addAttribute("loc", request.getRequestURI());
			return "views/attm/list";
		}else {
			throw new BoardException("첨부파일 게시글 조회를 실패하였습니다.");
		}	
	}
	
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	@PostMapping("insert")
	@Transactional	// 해당 메소드가 에러나면 롤백?????????????????????????????????????????????????????
	public String insertAttmBoard(@ModelAttribute Board b, @RequestParam("file") ArrayList<MultipartFile> files, HttpSession session) {	//파일이 여러개 첨부될 수 있으니 MultipartFile 사용 (파일관련 무조건!)
		System.out.println("초기 b : " + b);
		//System.out.println(files);
		
		String id = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardWriter(id);
		
		ArrayList<Attachment> list = new ArrayList<Attachment>(); // view에서 넘긴 파일 정보
		for(int i = 0; i < files.size(); i++) {	// 일부로 일반 for문 만듬 없어도 됨) 
			MultipartFile upload = files.get(i);
			//System.out.println(upload.getOriginalFilename());
			if(!upload.getOriginalFilename().equals("")) { //isEmpty :  파일 사이즈가 0인 것도 isEmpty에 올라감(비어있는 걸로 확인되기때문에 isEmpty 사용X)
				String[] returnArr = saveFile(upload);// 첨부파일 폴더에 파일 저장
				if(returnArr[1] != null) { // 혹시라도 에러났을 때를 대비..
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					// 담아주기
					list.add(a);
				}	
			}
		}
		// 썸네일 만드는 과정
		for(int i = 0; i < list.size(); i++) {
			Attachment a = list.get(i);
			if(i == 0) {
				a.setAttmLevel(0);
			}else {
				a.setAttmLevel(1);
			}
		}
		
		System.out.println(list);
		
		int result1 = 0;	// 각각 저장할 수 있는 것...만들어줌..
		int result2 = 0;
		if(list.isEmpty()) { // 첨부파일이 없어서 일반게시판으로 넘어가야 한다면
			b.setBoardType(1);
			result1 = bService.insertBoard(b);
		}else {
			b.setBoardType(2);
			result1 = bService.insertBoard(b);
			System.out.println("insert 후  b : " + b);
			
			for(Attachment a : list) {	// for each : for(변수 : 반복할 배열/컬렉션)
				a.setRefBoardId(b.getBoardId());
			}
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1) { 
			// result1 : 보드 insert (결과 1 or 0) restul2는 숫자가 바뀜(첨부파일 갯수에 따라 달라짐?) attachment (list.size(첨부파일 갯수..?)  '+1' : result1(보드 한개의 갯수)가 1이면 제대로 들어갔는지....
		//  (result1 + result2 == list.size()+1에서 result1(board insert 개수 -> 잘 됐으면 1) + result2(첨부파일 게시글에서 insert된 게시글의 개수) = list.size() -> insert된 게시글의 개수 + 1(board가 insert가 됐으면 1)
			if(result2 == 0) {
				return "redirect:/board/list";
			}else {
				return "redirect:/attm/list";
			}
		}else {
			for(Attachment a: list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 작성을 실패하였습니다.");
		}
	}
	
	

	public String[] saveFile(MultipartFile upload) {
		// (첨부파일)폴더 지정
		String savePath = "c:\\uploadFiles";	// \\ : \의 의미
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();	// mkdirs: 폴더 생성
		}
		// 같은 폴더에 같은 이름의 파일이 저장되지 않도록 rename -> 년월일시분초밀리랜덤수, 확장자
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100000);
		String originFileName = upload.getOriginalFilename();
		String renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf(".")); // originFileName : 확장자
																							// apple.png, a.pp.le.png (가장 마지막의 .)
		//System.out.println(renameFileName);
		
		String renamePath = folder + "\\" + renameFileName;
		try {
			upload.transferTo(new File(renamePath));	// transferTo : 원본 파일의 데이터를 대상 파일로 복사
		} catch (IllegalStateException | IOException e) {
			System.out.println("파일 전송 에러 : " + e.getMessage());
		}
		
		String[] returnArr = new String[2];
		returnArr[0] = savePath;	// 집어넣을 경로
		returnArr[1] = renameFileName;
		
		return returnArr;
	}
	
	
	public void deleteFile(String renameName) {
		String savePath = "c:\\uploadFiles";
		
		File f = new File(savePath + "\\" + renameName);
		if(f.exists()) {
			f.delete();
		}
	}
	
	@GetMapping("/{id}/{page}")
	public ModelAndView selectAttm(@PathVariable("id") int bId, @PathVariable("page") int page, HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		String id = null;
		if(loginUser != null) {
			id = loginUser.getId();
		}
		Board b = bService.selectBoard(bId, id);	// 두번째 인자는 조회수..?
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		/*
		 	select *
		 	from attachment
		 	where attm_status = 'Y' and ref_board_id = #{bId}
		 */
		if(b != null) {
			mv.addObject("b", b).addObject("page", page).addObject("list", list).setViewName("views/attm/detail");
			return mv;
		}else {
			throw new BoardException("첨부파일 게시글 상세보기를 실패하였습니다.");
		}	
}
	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		
		model.addAttribute("b", b).addAttribute("list", list).addAttribute("page", page);
		return "views/attm/edit";
	}
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page,
							  @RequestParam("deleteAttm") String[] deleteAttm, 
							  @RequestParam("file") ArrayList<MultipartFile> files) {
		
		System.out.println(b);
		System.out.println(Arrays.toString(deleteAttm));
		for(MultipartFile mf : files) {
			System.out.println("fileName : " + mf.getOriginalFilename());
		}
		
		/*
		  1. 새 파일 O
			  	(1) 기존 파일 모두 삭제 --> 기존 파일 모두 삭제 & 새 파일 저장
			  						   	새 파일 중에서 level 0, 1 지정
			  						   	(level0 = 썸네일)
			  						   	Board(boardId=34, boardTitle=에휴/..., boardWriter=null, nickName=null, boardContent=sda, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011411122082487908.jpg/0]
										fileName : 다운로드.png
										fileName : 

			  	(2) 기존 파일 일부 삭제 --> 기존 파일 일부 삭제 & 새 파일 저장
			  							삭제할 파일의 level 검사 후 
			  							level이 0인 파일이 삭제되면 다른 기존 파일의 레벨을 0으로 재지정,
			  							새파일의 레벨은 모두 1로 지정
			  							Board(boardId=25, boardTitle=이제 문제 없어, boardWriter=null, nickName=null, boardContent=잘될거야, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011317074336696428.jpg/0, ]
										fileName : 다운로드.png
										fileName : 

			  	(3) 기존 파일 모두 유지 --> 새 파일 저장
			  							새 파일의 레벨은 모두 1로 지정
			  							Board(boardId=21, boardTitle=제목을 작성합니다, boardWriter=null, nickName=null, boardContent=내용을 작성합니다, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[]
										fileName : 다운로드.png
										fileName : 
										[, ] : 2
										[] : 0 -> 기존첨부파일 1개, 삭제X
		  2. 새 파일 X
			  	(1) 기존 파일 모두 삭제 --> 기존 파일 모두 삭제
			  							일반 게시판으로 이동 : board_type = 1
			  							Board(boardId=34, boardTitle=에휴/..., boardWriter=null, nickName=null, boardContent=sda, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011411122082487908.jpg/0]
										fileName : 
			  	(2) 기존 파일 일부 삭제 --> 기존 파일 일부 삭제
			  							삭제할 파일의 level 검사 후 level이 0인 파일이 삭제되면
			  							다른 기존 파일의 레벨을 0으로 재지정
			  							Board(boardId=25, boardTitle=이제 문제 없어, boardWriter=null, nickName=null, boardContent=잘될거야, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011317074336696428.jpg/0, ]
										fileName : 
			  	(3) 기존 파일 모두 유지 --> board만 수정
		  								Board(boardId=25, boardTitle=이제 문제 없어, boardWriter=null, nickName=null, boardContent=잘될거야, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011317074336696428.jpg/0, ]
										fileName : 
										Board(boardId=21, boardTitle=제목을 작성합니다, boardWriter=null, nickName=null, boardContent=황 바보, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[]
										fileName : 
		 */
		
		b.setBoardType(2);
		
		// 새로 넣는 파일이 있다면 list에 옮겨담기
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i < files.size(); i++) {
			MultipartFile upload = files.get(i);
			
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload);
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					a.setRefBoardId(b.getBoardId());
					
					list.add(a);
				}
			}
		}
		
		// 삭제할 파일이 있다면 삭제할 파일의 이름과 레벨을 각각 delRename과 delLevel에 옮겨담기
		ArrayList<String> delRename = new ArrayList<String>();
		ArrayList<Integer> delLevel = new ArrayList<Integer>();
		for(String rename : deleteAttm) {
			if(!rename.equals("")) {
				String[] split = rename.split("/");
				delRename.add(split[0]);
				delLevel.add(Integer.parseInt(split[1]));
			}
		}
		
		int deleteAttmResult = 0;		// 파일 delete 후 결과 값
		int updateBoardResult = 0;		// 게시글 update 후 결과 값
		boolean existBeforeAttm = true;	// 이전 첨부파일이 존재하는지에 대한 여부
		if(!delRename.isEmpty()) {		// 저장했던 파일 중 하나라도 삭제하겠다고 한 경우
			deleteAttmResult = bService.deleteAttm(delRename);	// 삭
			// 첨부파일 폴더에서도 삭제
			if(deleteAttmResult > 0) {
				for(String rename : delRename) {
					deleteFile(rename); // 폴더에 있는 파일을 지워주는 메소드?
				}
			}

			// 일부만 삭제한건지 전부 삭제한건지? (삭제 갯수)
			if(deleteAttm.length != 0 && delRename.size() == deleteAttm.length) { // 기존 파일을 모두 삭제
				//delRename.size() == deleteAttm.length
				//[] : 0 -> 기존첨부파일 1개, 삭제X 
				// 0 == 0 -> true가 되면 안되니까 제외시켜줘야함 그래서 조건문 앞에 deleteAttm이 0이 아니라는 조건을 걸어준다
				existBeforeAttm = false;	// 기존 파일이 있니?
				if(list.isEmpty()){	// list : 새파일을 집어 넣는 곳
					b.setBoardType(1);
			}
		}else {
			// 일부만 삭제했다면(레벨삭제했는지 확인(썸넬)
			for(int level : delLevel) {
				if(level == 0) {
					bService.updateAttmLevel(b.getBoardId());	// 썸네일이 지워졌으면 기존의 파일들 중에서 level을 0으로 바꾸는 작업
					break;
				}
			}
		}
	}
	
	for(int i = 0; i < list.size(); i++) {
		Attachment a = list.get(i);
		if(existBeforeAttm) {
			a.setAttmLevel(1);
		}else {
			if(i == 0) {
				a.setAttmLevel(0);
			}else {
				a.setAttmLevel(1);
			}
		}
	}
		
	updateBoardResult = bService.updateBoard(b);
	
	int updateAttmResult = 0;
	if(!list.isEmpty()) {
		updateAttmResult = bService.insertAttm(list);
	}
		
	if(updateBoardResult + updateAttmResult == list.size() + 1) {
		if(deleteAttm.length != 0 && delRename.size() == deleteAttm.length && updateAttmResult == 0) {	// 기존파일도 다 삭제 & 새 파일이 없을 떄
			// 한개만 있을때 하나 삭제 -> [2025081411083624160074.jpg/0] 1
			// 한개만 있을때 0개 삭제 -> []0

			// 2개 있을때 하나 삭제 -> [, 2025071317072247914933.jpg/1]2
			// 2개 있을때 0개 삭제 -> [ , ]2
			// 2개 있을때 둘다 삭제 -> [202507131707224778686.jpg/0, 2025071317072247914933.jpg/1]2
			// ??????????????2:35강의 봐야해..
			return "redirect:/board/list";
		}else {
			return String.format("redirect:/attm/%d/%d", b.getBoardId(), page);
		}
	}else {
		throw new BoardException("첨부파일 게시글 수정을 실패하였습니다.");
	}		
}
	/*
	@PostMapping("delete")
	public String deleteBoard(@RequestParam("boardId") int bId) {
		int result1 = bService.deleteBoard(bId);
		int result2 = bService.statusNAttm(bId);
		
		if(result1 > 0 && result2 > 0) {
			return "redirect:/attm/list";
		}else {
			throw new BoardException("첨부파일 게시글 삭제를 실패하였습니다.");
		}
	}
	*/
	
	// 트리거 : 테이블이 dml이 일어날때 자동으로 실행되는 (시점에 따라 비포&애프터
	// 보드가 삭제되면 그걸 참조하고 있는 attach, reply도 자동으로 삭제되는????!!!!
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
