import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SeniorDB {
	
	 static String url = "jdbc:postgresql://127.0.0.1:5433/SENIORDB";
	 static String username = "postgres";
	 static String password = "9176";
	 
	 static Connection connection = null;
	 static Statement statement = null;
	 static ResultSet result = null;
	 //connection.createStatement();
	 
	public static void main(String[] args) throws SQLException{
		try {
			 Scanner scan = new Scanner(System.in);
			 
			 System.out.println("Connecting PostgreSQL database");
			 connection = DriverManager.getConnection(url, username, password);
			 
			 boolean state = true;	
			 while(state) {
	                System.out.println("======== 메인 화면 ========");
	                System.out.println("0. 종료하기");
	                System.out.println("1. 경로당 조회");
	                System.out.println("2. 경로당 선택");
	                System.out.println("3. 내 경로당 조회");
	                System.out.print(">> ");
	                
	                int menu =  scan.nextInt();
	                switch(menu) {
	                    case 0:
	                        state = false;
	                        break;
	                    case 1:
	                    	//조회함수() 호출
	                    	inquirySC();
	               		 	System.out.println();
	                        break;
	                    case 2:
	                        //선택함수() 호출
	                    	chooseSC();
	                        break;
	                    case 3:
	                        //내 경로당 출력함수()
	                        break;
	                    default:
	                        System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
	                        break;
	                }
	            }
	            
		} catch(SQLException ex) {
			throw ex;
		}
	}
	// 입력받은 주소에서 '로' 찾기
	public static void inquirySC() throws SQLException {
		System.out.println("====== 경로당 조회 ======");
	    System.out.print("도로명 주소를 입력하세요 >> ");
	    
	    Scanner scan = new Scanner(System.in);
	    String address = scan.nextLine();
	    
	    int index = address.lastIndexOf("로");
	    
	    String roAddress="";
	    if(index!=-1)
	    {
	    	 int spaceIndex = address.lastIndexOf(" ", index);
	    	 if(spaceIndex!=-1)
	    		 //서울특별시 성동구 뚝섬로5길 17
	    		 roAddress = address.substring(spaceIndex+1,index+1);
	    }else
	    	System.out.println("입력된 주소에 '로'가 없습니다.");
	    
    	// 입력받은 주소에서 '로'를 기준으로 근처에 위치한 경로당을 검색하는 query
    	String inquiry = "select scName from SeniorCenter where scAddress like '%" + roAddress + "%';";
    	try {
    		statement = connection.createStatement();
        	result = statement.executeQuery(inquiry);
        	
        	System.out.println("'로'를 기준으로 근처에 위치한 경로당을 조회합니다...");
        	System.out.println("\n요청하신 경로당 목록입니다.");
        	
        	int num=0;
    		while (result.next()) {
    			num++;
    			System.out.printf("%d. %s\n", num, result.getString(1));
    		}
    		
    	}catch(SQLException ex) {
    		throw ex;
    	}
	}
	
	public static void chooseSC() throws SQLException {
		System.out.println("====== 경로당 선택 ======");
		System.out.print("경로당 이름을 입력하세요 >> "); //잘못 입력했을 때 에러 처리
		
		Scanner scan = new Scanner(System.in);
	    String scName = scan.nextLine();
	    
	    String inquiry = "select * from SeniorCenter where scName='" + scName + "';";
	    try {
	    	statement = connection.createStatement();
        	result = statement.executeQuery(inquiry);
        	System.out.println(result.getInt(1), result.getString(2));
        	
        	scInterface(result.getInt(1), result.getString(2));
        	
	    }catch(SQLException ex){
	    	throw ex;
	    }
	  
	}

	
	public static void scInterface(int scID, String scName) {
		Scanner scan = new Scanner(System.in);
		
		boolean state = true;
		while(state) {
			System.out.printf("====== %s ======", scName);
            System.out.println("0. 뒤로가기");
            System.out.println("1. 내 경로당에 추가하기");
            System.out.println("2. 위치 정보");
            System.out.println("3. 방문 예약");
            System.out.println("4. 리뷰");
            System.out.println("5. 커뮤니티");
            System.out.println("6. 근처 무료 급식소");
            System.out.print(">> ");
		
            int menu = scan.nextInt();
            switch(menu) {
            	case 0:
            		state = false;
            		break;
            	case 1:
            		break;
            	case 2:
            		break;
            	case 3:
            		break;
            	case 4:
            		break;
            	case 5:
            		break;
            	case 6:
            		break;
            }


	}
				
	}
	    
}
	// 조회함수()
    
    //string s = scan.nextline();
    // '로' 단위로 끊어 sql문 입력
    // sql문 결과 print()

    // 선택함수()
    //System.out.println("====== 경로당 선택 ======");
    //System.out.print("경로당 이름을 입력하세요 >> ");
    //string s = scan.nextline();
    // 경로당 이름 기반 sql문 입력
    // sql문 결과 scID값 저장
    // 경로당 선택 화면 함수()

    // 내 경로당 출력함수()
    // 내 경로당 테이블 print()

    // 경로당 선택 화면 함수() -> scID값 활용
    //System.out.println("====== 푸른 경로당 ======");
    // 0. 뒤로 가기
    // 1. 내 경로당에 추가하기
    // 2. 위치 정보
    // 3. 방문 예약
    // 4. 리뷰
    // 5. 커뮤니티
    // 6. 근처 무료 급식소

    // 내 경로당에 추가하기()
    // 내 경로당 테이블에 uID, scID 값 set()

    // 위치 정보()
    // 경로당 테이블에서 scID 값 위치 정보 get(), 출력

    // 방문 예약() -> 체크
    // #월 #일
    // 방문 예약 테이블에서 scID 값을 포함한 시간정보 get()
    // 9시 10시 11시 14시 15시 16시 17시
    // 0. 뒤로 가기
    // 1. 예약 하기
        // 예약 시간 입력 -> 예약 테이블에 set()

    // 리뷰() -> 체크
    // 리뷰 테이블에 scID값을 포함한 리뷰 get()
    // 0. 뒤로 가기
    // 1. 리뷰 작성 -> uID, scID
    // 2. 리뷰 수정 -> uID, scID
    // 3. 리뷰 삭제 -> uID, scID

    // 커뮤니티()
    // 게시물 테이블에 scID 값을 포함한 게시물 get()
    // 0. 뒤로가기
    // 1. 게시물 작성 -> uID, scID
        // 제목을 입력하세요
        // 내용을 입력하세요
    // 2. 게시물 수정 -> uID, scID
    // 3. 게시물 삭제 -> uID, scID

    // 근처 무료 급식소
    // 근처 무료 급식소 테이블에서 scID의 위치정보 '로' 값인 tuple get(), 출력
