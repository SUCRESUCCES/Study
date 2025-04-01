package kh.springboot.board.exception;

public class BoardException extends RuntimeException {
	
	public BoardException() {}
	public BoardException(String msg) {
		super(msg);
	}
}
