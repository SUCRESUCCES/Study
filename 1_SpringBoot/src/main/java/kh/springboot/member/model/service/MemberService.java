package kh.springboot.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import kh.springboot.member.model.mapper.MemberMapper;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper mapper;
	
	public Member login(Member m) {
		return mapper.login(m);
	}

	public int insertMember(Member m) {
		return mapper.insertMember(m);
	}

	public ArrayList<HashMap<String, Object>> selectMyList(String id) {
		return mapper.selectMyList(id);
	}
	
	// 빈 생성을 했지만 값의 주입이 안됨 -> nullPointerException
	// 
	
	public int updateMember(Member m) {
		return mapper.updateMember(m);
	}

	public int updatePwd(HashMap<String, String> map) {
		return mapper.updatePwd(map);
	}

	public int deleteMember(String id) {
		return mapper.deleteMember(id);
	}

	/*
	 * public int checkId(String id) { return mapper.checkId(id); }
	 * 
	 * public int checkNickName(String nickname) { return
	 * mapper.checkNickName(nickname); }
	 */

	public int checkValue(HashMap<String, String> map) {
		return mapper.checkValue(map);
	}

	public int updateProfile(HashMap<String, String> map) {
		return mapper.updateProfile(map);
	}

//	public String findId(Member m) {
//		return mapper.findId(m);
//	}

//	public Member findPwd(Member m) {
//		return mapper.findPwd(m);
//	}

	public Member findInfo(Member m) {
		return mapper.findInfo(m);
	}

	public ArrayList<TodoList> selectTodoList(String id) {
		return mapper.selectTodoList(id);
	}

	public int insertTodo(TodoList todo) {
		return mapper.insertTodo(todo);
	}

	public int updateTodo(TodoList todo) {
		return mapper.updateTodo(todo);
	}

	public int deleteTodo(int num) {
		return mapper.deleteTodo(num);
	}

	public ArrayList<Member> selectMembers() {
		return mapper.selectMembers();
	}

	public int updateMemberItem(HashMap<String, String> map) {
		return mapper.updateMemberItem(map);
	}

}
