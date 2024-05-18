package board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class MemberExam {
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	
	public MemberExam() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servletex?useUnicode=true&characterEncoding=utf8",
					"root","1234");
		}catch(Exception e) {
			e.printStackTrace();
			exit();
		}
	}
	
	public void list() {
		System.out.println();
		System.out.println("[회원 목록]");
		System.out.println("-------------------------------------------------------");
		System.out.printf("%-6s%-12%-16%-40s\n","아이디","이름","email","가입일자");
		System.out.println("-------------------------------------------------------");
		
		try {
			String sql="SELECT id,name,email,joinDate FROM t_member order by joinDate desc";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Member member = new Member();
				String id = rs.getString("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date joinDate = rs.getDate("joinDate");
				member.setId(id);
				member.setName(name);
				member.setEmail(email);
				member.setJoinDate(joinDate);
				System.out.printf("%-6s%-12%-16%-40s\n",member.getId(),
						member.getName(),member.getEmail(),member.getJoinDate());
			}
			rs.close();
			pstmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	public void exit() {
		if(conn != null) {
			try {
				conn.close();
			}catch(SQLException e) {
			}
		}
		System.out.println("** 회원 게시판 종료 **");
		System.exit(0);
	}
	public void mainMenu() {
		System.out.println();
		System.out.println("---------------------------------------------------------");
		System.out.println("메인메뉴:1.회원가입 | 2.회원보기 | 3.회원삭제 | 4.프로그램 종료");
		System.out.print("메뉴선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();
		
		switch(menuNo) {
		case "1" -> create();
		case "2" -> read();
		case "3" -> clear();
		case "4" -> exit();
		}
	}
	
	public void create() {
		Member member = new Member();
		System.out.println("[회원가입]");
		System.out.print("아이디: ");
		member.setId(scanner.nextLine());
		System.out.print("비밀번호: ");
		member.setPwd(scanner.nextLine());
		System.out.print("이름: ");
		member.setName(scanner.nextLine());
		System.out.print("email: ");
		member.setEmail(scanner.nextLine());
		
		System.out.println("--------------------------------------------------------");
		System.out.println("가입하시겠습니다?:1.OK | 2.Cancel");
		System.out.print("메뉴선택: ");
		String menuNo = scanner.nextLine();
		if(menuNo.equals("1")) {
			try {
				String sql=""+
							"INSERT INTO t_member(id,pwd,name,email,joinDate)"+
							"VALUES(?,?,?,?,now())";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, member.getId());
				pstmt.setString(2, member.getPwd());
				pstmt.setString(3, member.getName());
				pstmt.setString(4, member.getEmail());
				pstmt.executeUpdate();
				pstmt.close();
			}catch(Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		list();
	}
	
	public void read() {
		System.out.println("[회원 보기]");
		System.out.print("아이디: ");
		String _id=scanner.nextLine();
		
		try {
			String sql="SELECT id,name,email,joinDate FROM t_member WHERE id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, _id);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Member member = new Member();
				String id = rs.getString("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date joinDate = rs.getDate("joinDate");
				member.setId(id);
				member.setName(name);
				member.setEmail(email);
				member.setJoinDate(joinDate);
				System.out.println("######################");
				System.out.println("아이디: "+member.getId());
				System.out.println("이름: "+member.getName());
				System.out.println("email:"+member.getEmail());
				System.out.println("가입날짜: "+member.getJoinDate());
				
				System.out.println("--------------------------------------------------");
				System.out.println("회원수정: 1.Update | 2.Delete | 3.List");
				System.out.print("메뉴선택: ");
				String menuNo = scanner.nextLine();
				System.out.println();
				
				if(menuNo.equals("1")) {
					update(member);
				}else if(menuNo.equals("2")) {
					delete(member);
				}
			}
			rs.close();
			pstmt.close();
		}catch(Exception e) {
			e.printStackTrace();
			exit();
		}
		list();
	}
	
	public void update(Member member) {
		System.out.println("[수정 내용 입력]");
		System.out.print("이름: ");
		member.setName(scanner.nextLine());
		System.out.print("비밀번호: ");
		member.setPwd(scanner.nextLine());
		System.out.print("email: ");
		member.setEmail(scanner.nextLine());
		
		System.out.println("-----------------------------------------------------");
		System.out.println("수정하시겠습니까?: 1.OK | 2.Cancel");
		System.out.print("메뉴선택: ");
		String menuNo = scanner.nextLine();
		if(menuNo.equals("1")) {
			try {
				String sql = ""+
								"UPDATE t_member SET pwd=?,name=?,email=? WHERE id=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, member.getPwd());
				pstmt.setString(2, member.getName());
				pstmt.setString(3, member.getEmail());
				pstmt.setString(4, member.getId());
				pstmt.executeUpdate();
				pstmt.close();
			}catch(Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		list();
	}
	
	public void delete(Member member) {
		try {
			String sql = ""+"DELETE FROM t_member WHERE id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getId());
			pstmt.executeLargeUpdate();
			pstmt.close();
		}catch(Exception e) {
			e.printStackTrace();
			exit();
		}
		list();
	}
	
	public void clear() {
		System.out.println("[회원전체삭제]");
		System.out.println("-----------------------------------------");
		System.out.println("회원전체를 삭제하시겠습니까?: 1.OK | 2.Cancel");
		System.out.print("메뉴선택: ");
		String menuNo = scanner.nextLine();
		if(menuNo.equals("1")) {
			try {
				String sql = "TRUNCATE TABLE t_member";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();
			}catch(Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		list();
	}
	
	public static void main(String[] args) {
		MemberExam memberExam = new MemberExam();
		memberExam.list();

	}

}
