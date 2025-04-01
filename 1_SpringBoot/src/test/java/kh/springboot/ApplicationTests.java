package kh.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kh.springboot.board.controller.BoardController;
import kh.springboot.board.model.vo.Board;

@SpringBootTest
public class ApplicationTests {	// public 생략가능 / class명 변경 가능/ 접근제한자 생략 가능
	
	@Autowired
	private BoardController controller;
	
	@BeforeAll
	public static void startTest() {
		System.out.println("테스트를 시작합니다.");
	}
	
	@Test
	public void contextLoads() {
		Board b = new Board(1, "test...", "user01", "건강최고", "...test", 0,
				new Date(new java.util.Date().getTime()),
				new Date(new java.util.Date().getTime()), "Y", 1);
		
		// assertEquals() : 두 값을 비교해서 일치 여부 판단
		assertEquals("redirect:/board/1/1", controller.updateBoard(b, 1));
		}
	// assertArrayEquals() : 두 배열을 비교하여 일치 여부 판단
	// assertNotNull() / assertNull() : 객체의 null 여부 확인
	// assertTrue() / assertFalse() : 특정 조건이 true인지 false인지 판단
	
	// 메소드는 여러개 만들어도 괜찮음
	
//	@Test
//	public void test2() {
//		assertEquals("", "");
//	}
	
	@AfterAll
	public static void endTest() {
		System.out.println("테스트를 종료합니다.");
	}
}
