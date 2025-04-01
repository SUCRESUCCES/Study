package kh.springboot.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final BoardMapper mapper;

	public int getListCount(int i) {
		return mapper.getlistCount(i);
	}

	public ArrayList<Board> selectBoardList(PageInfo pi, int i) {
		int offset = (pi.getCurrentPage() - 1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectBoardList(i, rowBounds);
		// 해당 메소드를 호출하지않는다, 쿼리명 넣는게 빠짐?
	}

	public int insertBoard(Board b) {
		return mapper.insertBoard(b);
	}

	public Board selectBoard(int bId, String id) {
		Board b = mapper.selectBoard(bId);
		if (b != null && id != null && !b.getBoardWriter().equals(id)) {
			int result = mapper.updateCount(bId);
			b.setBoardCount(b.getBoardCount() + 1);
		} else {
		}
		return b;
	}
	public int updateBoard(Board b) {
		return mapper.updateBoard(b);
	}

	public int deleteBoard(int bId) {
		return mapper.deleteBoard(bId);
	}

	public ArrayList<Attachment> selectAttmBoardList(Integer bId) {	// null과 bId를 둘 다 만족시켜줘야함
		return mapper.selectAttmBoardList(bId);
	}
	
	public int insertAttm(ArrayList<Attachment> list) {
		return mapper.insertAttm(list);
	}

	public int deleteAttm(ArrayList<String> delRename) {
		return mapper.deleteAttm(delRename);
	}

	public void updateAttmLevel(int boardId) {
		mapper.updateAttmLevel(boardId);
		// 오ㅐ return 안써요?
	}

	/*
	public int statusNAttm(int bId) {
		return mapper.statusNAttm(bId);
	}
	 */
	public ArrayList<Board> selectTop() {
		return mapper.selectTop();
	}

	public ArrayList<Reply> selectReplyList(int bId) {
		return mapper.selectReplyList(bId);
	}

	public int insertReply(Reply r) {
		return mapper.insertReply(r);
	}

	public int deleteReply(int rId) {
		return mapper.deleteReply(rId);
	}

	public int updateReply(Reply r) {
		return mapper.updateReply(r);
	}

	public ArrayList<Board> selectRecentBoards() {
		return mapper.selectRecentBoards();
	}

	public int updateBoardStatus(HashMap<String, Object> map) {
		return mapper.updateBoardStatus(map);
	}

	public ArrayList<Attachment> selectAllAttm() {
		return mapper.selectAllAttm();
	}
}