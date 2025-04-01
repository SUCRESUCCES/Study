package kh.springboot.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter				// 라이브러리를 자동으로 읽어서 설정해준 건데 눈으로 보이진 않음,,,
@NoArgsConstructor	// 기본 생성자
@AllArgsConstructor // 전체 생성자
@ToString
public class Member {
	
	private String id;
	private String pwd;
	private String name;
	private String nickName;
	private String email;
	private String gender;
	private int age;
	private String phone;
	private String address;
	private Date enrollDate;
	private Date updateDate ;
	private String memberStatus;
	private String isAdmin;
	private String profile;
}
