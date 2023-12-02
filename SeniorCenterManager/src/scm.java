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
	// connection.createStatement();

	public static void main(String[] args) throws SQLException {
		try {
			Scanner scan = new Scanner(System.in);

			System.out.println("Connecting PostgreSQL database");
			connection = DriverManager.getConnection(url, username, password);

			boolean state = true;
			while (state) {
				System.out.println("======== 메인 화면 ========");
				System.out.println("0. 종료하기");
				System.out.println("1. 회원가입");
				System.out.println("2. 로그인");
				System.out.print(">> ");

				int menu = scan.nextInt();
				switch (menu) {
				case 0:
					state = false;
					break;
				case 1:
					// 회원가입() 함수 호출
					signUp();
					System.out.println();
					break;
				case 2:
					// 로그인() 함수 호출
					signIn();
					System.out.println();
					break;
				default:
					System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
					break;
				}
			}

		} catch (SQLException ex) {
			throw ex;
		}
	}
	
	// 회원가입
	public static void signUp() throws SQLException {
		Scanner scan = new Scanner(System.in);

		System.out.println("사용할 아이디를 입력하세요.");
		String uID = scan.nextLine();
		System.out.println("사용할 비밀번호를 입력하세요.");
		String pwd = scan.nextLine();
		System.out.println("이름을 입력하세요.");
		String uName = scan.nextLine();

		String signUp = "insert into Users values ('"+ uID +"', '"+ uName +"', '"+ pwd +"');";
		try {
			statement = connection.createStatement();
			statement.executeUpdate(signUp);
			System.out.println("회원가입이 완료되었습니다.");
			// 이미 있는 아이디 입력했을 때 에러 처리 해야 함
		} catch (SQLException ex) {
			throw ex;
		}
	}

	// 로그인
	public static void signIn() throws SQLException {
		Scanner scan = new Scanner(System.in);

		System.out.println("아이디를 입력하세요.");
		String uID = scan.nextLine();
		System.out.println("비밀번호를 입력하세요.");
		String pwd = scan.nextLine();
		
		String signIn = "select * from Users where uID='"+ uID +"' and pwd='"+ pwd +"';";
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(signIn);

			if (result.next())
			{
				System.out.println("로그인에 성공했습니다.\n");
				boolean state = true;
				while(state) {
					System.out.println("====== 서비스 화면 ======");
					System.out.println("0. 뒤로 가기");
					System.out.println("1. 경로당 조회");
					System.out.println("2. 경로당 선택");
					System.out.println("3. 내 경로당 조회");
					
					int menu = scan.nextInt();
					switch(menu) {
						case 0:
							state=false;
							break;
						case 1:
							// 조회함수() 호출
							inquirySC();
							System.out.println();
							break;
						case 2:
							// 선택함수() 호출
							chooseSC(uID);
							break;
						case 3:
							// 내 경로당 출력함수() 호출
							printMySC(uID);
							break;
						default:
							System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
							break;
					}
				}
			}
			else
				System.out.println("로그인에 실패했습니다.");
		}catch(SQLException ex) {
			throw ex;
		}
	}

	// 경로당 조회
	public static void inquirySC() throws SQLException {
		System.out.println("====== 경로당 조회 ======");
		System.out.print("도로명 주소를 입력하세요 >> "); // 입력한 주소가 없을 때 에러 처리 해야 함

		Scanner scan = new Scanner(System.in);
		String address = scan.nextLine();

		// 입력받은 주소에서 '로' 찾기
		int index = address.lastIndexOf("로");

		String roAddress = "";
		if (index != -1) {
			int spaceIndex = address.lastIndexOf(" ", index);
			if (spaceIndex != -1)
				// 서울특별시 성동구 뚝섬로5길 17
				roAddress = address.substring(spaceIndex + 1, index + 1);
		} else
			System.out.println("입력된 주소에 '로'가 없습니다.");

		// 입력받은 주소에서 '로'를 기준으로 근처에 위치한 경로당을 검색하는 query
		String inquiry = "select scName from SeniorCenter where scAddress like '%"+ roAddress +"%';";
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(inquiry);

			System.out.printf("'%s'에 위치한 경로당입니다.", roAddress);
			
			int num = 0;
			while (result.next()) {
				num++;
				System.out.printf("%d. %s\n", num, result.getString(1));
			}

		} catch (SQLException ex) {
			throw ex;
		}
	}

	// 경로당 선택
	public static void chooseSC(String uID) throws SQLException {
		System.out.println("====== 경로당 선택 ======");
		System.out.print("경로당 이름을 입력하세요 >> ");

		Scanner scan = new Scanner(System.in);
		String scName = scan.nextLine();

		String inquiry = "select * from SeniorCenter where scName='"+ scName +"';";
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(inquiry);

			if (result.next())
				scInterface(uID, result.getInt(1), result.getString(2));
			else
				System.out.println("존재하지 않는 경로당입니다.\n");

		} catch (SQLException ex) {
			throw ex;
		}

	}
	
	// 경로당 선택 화면
	public static void scInterface(String uID, int scID, String scName) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		try {
			statement = connection.createStatement();
		}catch(SQLException ex) {
			throw ex;
		}

		boolean state = true;
		while (state) {
			System.out.printf("\n====== %s ======\n", scName);
			System.out.println("0. 뒤로가기");
			System.out.println("1. 내 경로당에 추가하기");
			System.out.println("2. 위치 정보");
			System.out.println("3. 방문 예약");
			System.out.println("4. 리뷰");
			System.out.println("5. 커뮤니티");
			System.out.println("6. 근처 무료 급식소");
			System.out.print(">> ");

			int menu = scan.nextInt();
			switch (menu) {
			case 0:
				state = false;
				break;
			case 1:
            		String query1 = "insert into MySeniorCenter values ('"+ uID +"', '"+ scID +"');";
            		try {
                    	statement.executeUpdate(query1);
                    	System.out.println("내 경로당 목록에 추가되었습니다.");
            	    }catch(SQLException ex){
            	    	throw ex;
            	    }
				break;
			case 2:
				String query2 = "select scAddress from SeniorCenter where scID='"+ scID +"';";
				try {
                	result = statement.executeQuery(query2);
                	if(result.next())
                		System.out.println("위치 : " + result.getString(1));
        	    }catch(SQLException ex){
        	    	throw ex;
        	    }
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				// 커뮤니티()
				community(uID, scID, scName);
				break;
			case 6:
				try {
					String scAddress="";
					String query6 = "select scAddress from SeniorCenter where scID='"+ scID +"';";
                	result = statement.executeQuery(query6);

                	if(result.next())
                		scAddress=result.getString(1);
                	
                	int index=scAddress.lastIndexOf("로");
                	String roAddress = "";
    				if (index != -1) {
    					int spaceIndex = scAddress.lastIndexOf(" ", index);
    					if (spaceIndex != -1)
    						// 서울특별시 성동구 뚝섬로5길 17
    						roAddress = scAddress.substring(spaceIndex + 1, index + 1);
    					else
    						roAddress = scAddress.substring(0, index+1);
    				}
    				
    				String inquiry = "select * from FreeMealCenter where fmcAddress like '%"+ roAddress +"%';";
					result = statement.executeQuery(inquiry);

					System.out.printf("'%s' 근처에 위치한 무료급식소 정보입니다.\n", scName);
					
					String tab ="\t\t\t";
					String tab2 ="\t\t";
					System.out.printf("%-20s %s%s %s%-15s %s%s %s%s %s%s %s%s\n", "시설명", tab, "주소", tab, "전화번호", tab, "장소", tab, "대상", tab, "시간", tab, "요일");
					while (result.next()) {
						System.out.printf("%-20s %s%s %s%-15s %s%s %s%s %s%s %s%s\n", result.getString(1), tab2, result.getString(2), tab2, result.getString(3), tab2, result.getString(4), tab2, result.getString(5), tab2, result.getString(6), tab2, result.getString(7));
					}

				}catch(SQLException ex) {
					throw ex;
				}
				break;
			default:
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				break;
			}
		}
	}
	
	// 내 경로당 조회
	public static void printMySC(String uID) throws SQLException{
		String inquiry = "select * from MySeniorCenter where uID='"+ uID +"';";
		try {
	    	statement = connection.createStatement();
        	result = statement.executeQuery(inquiry);
        	
        	int num=0;
        	System.out.printf("%s %s\n", "no", "scID");
        	while (result.next()) {
        		num++;
				System.out.printf("%d %s\n", num, result.getString(2)); // 내 경로당 출력할 떄 이름도 출력해야 할 듯? -> 테이블 바꿔야 함
			}
        	
//        	select M.scID, scName
//        	from MySeniorCenter M, SeniorCenter
//        	where M.scID=SeniorCenter.scID;
        	
	    }catch(SQLException ex){
	    	throw ex;
	    }
	}
	
	// 커뮤니티
	public static void community(String uID, int scID, String scName) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		String inquiry = "select * from Post where scID='"+ scID+"';";
		try {
			statement = connection.createStatement();
			result=statement.executeQuery(query5);
			
			// 게시물 출력
			System.out.printf("%s %s %s %s %s %s\n", "pID", "uID", "title", "content", "timestamp");
			while(result.next())
				System.out.printf("%s %s %s %s %s %s", result.getString(1), result.getString(5), result.getString(2), result.getString(3), result.getString(4));
			
			boolean state = true;
			while (state) {
				System.out.printf("\n====== %s 커뮤니티 ======\n", scName);
				System.out.println("0. 뒤로가기");
				System.out.println("1. 게시물 작성");
				System.out.println("2. 게시물 수정");
				System.out.println("3. 게시물 삭제");
				System.out.print(">> ");
				
				int menu = scan.nextInt();
				switch(menu) {
					case 0:
						state = false;
						break;
					case 1:
						System.out.println("제목을 입력하세요.");
						String title = scan.nextLine();
						System.out.println("내용을 입력하세요.");
						String content = scan.nextLine();
						
						String query1 = ""
						break;
					case 2:
						break;
					case 3:
						break;
					default:
						System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
						break;
				}
			// 게시물 테이블에 scID 값을 포함한 게시물 get()
			// 0. 뒤로가기
			// 1. 게시물 작성 -> uID, scID
			// 제목을 입력하세요
			// 내용을 입력하세요
			// 2. 게시물 수정 -> uID, scID
			// 3. 게시물 삭제 -> uID, scID
		}catch(SQLException ex) {
			throw ex;
		}
		
	}

	

}
// 조회함수()

// string s = scan.nextline();
// '로' 단위로 끊어 sql문 입력
// sql문 결과 print()

// 선택함수()
// System.out.println("====== 경로당 선택 ======");
// System.out.print("경로당 이름을 입력하세요 >> ");
// string s = scan.nextline();
// 경로당 이름 기반 sql문 입력
// sql문 결과 scID값 저장
// 경로당 선택 화면 함수()

// 내 경로당 출력함수()
// 내 경로당 테이블 print()

// 경로당 선택 화면 함수() -> scID값 활용
// System.out.println("====== 푸른 경로당 ======");
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
