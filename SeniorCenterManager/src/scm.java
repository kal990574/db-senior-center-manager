import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class SeniorDB {

	static String url = "jdbc:postgresql://127.0.0.1:5433/SENIORDB";
	static String username = "postgres";
	static String password = "9176";

	static Connection connection = null;
	static Statement statement = null;
	static ResultSet result = null;

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
					break;
				case 2:
					// 로그인() 함수 호출
					signIn();
					break;
				default:
					System.out.println("잘못된 입력입니다. 다시 입력해주세요.\n");
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
		
		boolean state = true;
		while(state)
		{
			System.out.println("사용할 아이디를 입력하세요.");
			String uID = scan.nextLine();
			String idCheck = "select * from Users where uID='"+ uID +"';";
			try {
				statement = connection.createStatement();
				result=statement.executeQuery(idCheck);
				if(result.next())
				{
					System.out.println("이미 존재하는 아이디로 사용할 수 없습니다.\n");
					continue;
				}
				else 
				{
					System.out.println("사용할 비밀번호를 입력하세요.");
					String pwd = scan.nextLine();
					System.out.println("이름을 입력하세요.");
					String uName = scan.nextLine();
					
					String signUp = "insert into Users values ('"+ uID +"', '"+ uName +"', '"+ pwd +"');";
					statement.executeUpdate(signUp);
					System.out.println("회원가입이 완료되었습니다.\n");
					
					state=false;
					break;
				}
			}catch(SQLException ex) {
				throw ex;
			}
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
					System.out.print(">> ");
					
					int menu = scan.nextInt();
					switch(menu) {
						case 0:
							state=false;
							break;
						case 1:
							// 조회함수() 호출
							inquirySC();
							break;
						case 2:
							// 선택함수() 호출
							chooseSC(uID);
							break;
						case 3:
							// 내 경로당 출력함수() 호출
							inquiryMySC(uID);
							break;
						default:
							System.out.println("잘못된 입력입니다. 다시 입력해주세요.\n");
							break;
					}
				}
			}
			else
				System.out.println("로그인에 실패했습니다.\n");
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
				roAddress = address.substring(spaceIndex + 1, index + 1);
			else
				roAddress = address;
		} else
			System.out.println("입력된 주소의 형식이 잘못됐습니다.\n");

		// 입력받은 주소에서 '로'를 기준으로 근처에 위치한 경로당을 검색하는 query
		String inquiry = "select scName from SeniorCenter where scAddress like '%"+ roAddress +"%';";
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(inquiry);
			
			System.out.printf("'%s'에 위치한 경로당입니다.\n", roAddress);
			int num = 0;
			while (result.next()) {
				num++;
				System.out.printf("%d. %s\n", num, result.getString(1));
			}
			if(num==0)
				System.out.printf("'%s'에 위치한 경로당을 찾을 수 없습니다.\n", roAddress);
			
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
				// 내 경로당에 추가하기 함수() 호출
				insertMySC(uID, scID);
				break;
			case 2:
				// 위치 정보 조회 함수() 호출
				printAddress(scID);
				break;
			case 3:
				// 방문 예약 함수() 호출
				reservation(uID, scID);
				break;
			case 4:
				// 리뷰 함수() 호출
				review(uID, scID, scName);
				break;
			case 5:
				// 커뮤니티 함수() 호출
				community(uID, scID, scName);
				break;
			case 6:
				// 근처 무료 급식소 조회 함수() 호출
				inquiryFMC(scID, scName);
				break;
			default:
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				break;
			}
		}
	}
	
	// 내 경로당 조회
	public static void inquiryMySC(String uID) throws SQLException{
		String inquiry = "select scName " +
				"from MySeniorCenter M, SeniorCenter S " +
				"where M.scID=S.scID and M.uID='"+ uID +"';";
		try {
	    	statement = connection.createStatement();
        	result = statement.executeQuery(inquiry);
        	
        	int num=0;
        	while (result.next()) {
        		num++;
				System.out.printf("%d %s\n", num, result.getString(1));
			}
        	System.out.println();
        	
	    }catch(SQLException ex){
	    	throw ex;
	    }
	}
	
	// 내 경로당에 추가하기
	public static void insertMySC(String uID, int scID) throws SQLException{
		String query1 = "select * from MySeniorCenter where uID='"+ uID +"' and scID="+ scID +";";
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query1);
			
			if(!result.next())
			{
				String query2 = "insert into MySeniorCenter values ('"+ uID +"', "+ scID +");";
				statement.executeUpdate(query2);
				System.out.println("내 경로당 목록에 추가되었습니다.");
			}
			else
				System.out.println("이미 추가된 경로당입니다.");
			
		}catch(SQLException ex){
        	throw ex;
   	    }
	}
	
	// 위치 정보 조회
	public static void printAddress(int scID) throws SQLException{
		String query1 = "select scAddress from SeniorCenter where scID="+ scID +";";
		try {
			statement = connection.createStatement();
        	result = statement.executeQuery(query1);
        	if(result.next())
        		System.out.println("위치 : " + result.getString(1));
	    }catch(SQLException ex){
	    	throw ex;
	    }
	}
	
	// 방문 예약
	public static void reservation(String uID, int scID) throws SQLException{
		Scanner scan = new Scanner(System.in);
		
		System.out.println("예약 날짜를 입력하세요 (ex: YYYY-MM-DD).");
		String reserveDate = scan.nextLine();
		
		// 예약 가능한 시간대 확인
		System.out.println("예약 가능한 시간대:");

		for (int hour = 9; hour <= 17; hour++)
		{
			String reserveTime = hour + ":00";
			
			if (!isReserved(connection, scID, reserveDate, reserveTime))
				System.out.println(reserveTime);
		}

		System.out.println("예약할 시간대를 입력하세요.(ex: HH:00)");
		String reserveTime = scan.nextLine();

		// 예약 수행
		if (!isReserved(connection, scID, reserveDate, reserveTime)){
			performReservation(connection, reserveDate, reserveTime, scID, uID);
			System.out.println("예약이 완료되었습니다.");
		} else
			System.out.println("이미 예약된 시간대입니다. 다른 시간을 선택하세요.");
	}
	
	// 예약 확인
	public static boolean isReserved(Connection connection, int scID, String reserveDate, String reserveTime) throws SQLException {
		String checkReservation = "select * from Reservation where scID = ? AND reserveDate = ? AND reserveTime = ?";
		
		try (PreparedStatement selectStatement = connection.prepareStatement(checkReservation)) {
			selectStatement.setInt(1, scID);
			selectStatement.setString(2, reserveDate);
			selectStatement.setString(3, reserveTime);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				return resultSet.next(); // 결과가 있으면 이미 예약이 되어 있는 것
			}
		}
	}
	
	// 예약 진행
	private static void performReservation(Connection connection, String reserveDate, String reserveTime, int scID, String uID) throws SQLException {
		String reservation = "insert into Reservation(reserveDate, reserveTime, uID, scID) VALUES (?, ?, ?, ?)";
		
		try (PreparedStatement insertStatement = connection.prepareStatement(reservation)) {
			insertStatement.setString(1, reserveDate);
			insertStatement.setString(2, reserveTime);
			insertStatement.setString(3, uID);
			insertStatement.setInt(4, scID);

			insertStatement.executeUpdate();
		}
	}
	
	// 시간 포맷팅
	public static String formatTimestamp(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

        try {
            return outputFormat.format(inputFormat.parse(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;
        }
    }
	
	// 리뷰
	public static void review(String uID, int scID, String scName) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		 String inquiry = "select * from Review where scID='" + scID + "';";
		
		try {
			statement = connection.createStatement();

			boolean state = true;
			while (state) {
				System.out.printf("\n====== %s 리뷰 ======\n", scName);
				
				result = statement.executeQuery(inquiry);
				while (result.next()) {
					int reviewID = result.getInt(1);
                    String content = result.getString(2);
                    String timestamp = result.getString(3);
                    String userID = result.getString(4);

                    String formattedTimestamp = formatTimestamp(timestamp);

                    System.out.printf("%d %s %s %s\n", reviewID, content, formattedTimestamp, userID);
				}
				
				System.out.println("0. 뒤로가기");
				System.out.println("1. 리뷰 작성");
				System.out.println("2. 리뷰 수정");
				System.out.println("3. 리뷰 삭제");
				System.out.print(">> ");

				int menu = scan.nextInt();
				switch (menu) {
					case 0:
						state = false;
						break;
					case 1:
						//리뷰 작성
						writeReview(uID, scID);
						break;
					case 2:
						//리뷰 수정
						updateReview(uID, scID);
						break;
					case 3:
						//리뷰 삭제
						deleteReview(uID, scID);
						break;
					default:
						System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
						break;
				}
			}
		} catch(SQLException ex){
			throw ex;
		}
	}
	
	// 리뷰 작성
	public static void writeReview(String uID, int scID) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		System.out.println("리뷰 내용을 입력하세요.");
		String content = scan.nextLine();

		String review = "insert into Review(r_content, uID, scID) VALUES (?, ?, ?)";
		try (PreparedStatement preparedStatement = connection.prepareStatement(review)) {
			preparedStatement.setString(1, content);
			preparedStatement.setString(2, uID);
			preparedStatement.setInt(3, scID);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("리뷰가 작성되었습니다.");
			} else {
				System.out.println("리뷰 작성 실패.");
			}
		} catch (SQLException ex) {
			throw ex;
		}
	}
	
	// 리뷰 수정
	public static void updateReview(String uID, int scID) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		String showUpdate = "select * from Review where scID=" + scID + " and uID ='" + uID + "';";
		
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(showUpdate);
			while(result.next()){
				System.out.printf("%d %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
			}
		}catch(SQLException ex) {
			throw ex;
		}
		
		// 수정할 리뷰의 reviewID를 입력받음
		System.out.println("수정할 리뷰의 번호를 입력하세요.");
		int rIDToUpdate = scan.nextInt();

		// PreparedStatement를 사용하여 현재 사용자의 리뷰인지 확인
		String selectQuery = "select uID from Review where reviewID = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
			selectStatement.setInt(1, rIDToUpdate);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					String reviewUID = resultSet.getString("uID");

					// 현재 사용자의 uID와 리뷰의 uID가 일치하면 수정 수행
					if (uID.equals(reviewUID)) {
						// 수정할 내용을 입력 받음
						scan.nextLine(); // 이전에 남아있는 개행 문자 처리
						System.out.println("수정할 내용을 입력하세요.");
						String newContent = scan.nextLine();
						
						String trigger = "create or replace function updateTime() returns trigger as $$ " + 
						 		"begin NEW.r_timestamp=NOW();" + 
						 		"return New;" + 
						 		"end;" + 
						 		"$$ language 'plpgsql';" + 
						 		"create trigger updateReviewTime " + 
						 		"before update on Review " + 
						 		"for each row " + 
						 		"execute procedure updateTime();";
						statement = connection.createStatement();
						statement.executeUpdate(trigger);
						

						// PreparedStatement를 사용하여 UPDATE 쿼리 실행
						String updateQuery = "update Review set r_content = ? where reviewID = ?";
						try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
							updateStatement.setString(1, newContent);
							updateStatement.setInt(2, rIDToUpdate);

							int rowsAffected = updateStatement.executeUpdate();

							if (rowsAffected > 0) {
								System.out.println("리뷰가 수정되었습니다.");
							} else {
								System.out.println("리뷰 수정 실패. 해당 번호가 존재하지 않습니다.");
							}
						} catch (SQLException ex) {
							throw ex;
						}
					} else {
						System.out.println("현재 사용자의 리뷰가 아닙니다. 수정 권한이 없습니다.");
					}
				} else {
					System.out.println("해당 번호의 리뷰가 존재하지 않습니다.");
				}
			}
		} catch (SQLException ex) {
			throw ex;
		}
	}
	
	// 리뷰 삭제
	public static void deleteReview(String uID, int scID) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		String showDelete = "select * from Review where scID=" + scID + " and uID ='" + uID + "';";
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(showDelete);
			while (result.next()){
				System.out.printf("%d %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
			}
		}catch(SQLException ex) {
			throw ex;
		}
		
		// 삭제할 리뷰의 reviewID를 입력 받음
		System.out.println("삭제할 리뷰의 pID를 입력하세요.");
		int rIDToDelete = scan.nextInt();

		// PreparedStatement를 사용하여 현재 사용자의 리뷰인지 확인
		String selectQuery = "select uID from Review where reviewID = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
			selectStatement.setInt(1, rIDToDelete);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					String reviewUID = resultSet.getString("uID");

					// 현재 사용자의 uID와 리뷰의 uID가 일치하면 삭제 수행
					if (uID.equals(reviewUID)) {
						// PreparedStatement를 사용하여 DELETE 쿼리 실행
						String deleteQuery = "delete from Review where reviewID = ?";
						try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
							deleteStatement.setInt(1, rIDToDelete);

							int rowsAffected = deleteStatement.executeUpdate();

							if (rowsAffected > 0) {
								System.out.println("리뷰가 삭제되었습니다.");
							} else {
								System.out.println("리뷰 삭제 실패. 해당 번호가 존재하지 않습니다.");
							}
						} catch (SQLException ex) {
							throw ex;
						}
					} else {
						System.out.println("현재 사용자의 리뷰가 아닙니다. 삭제 권한이 없습니다.");
					}
				} else {
					System.out.println("해당 번호의 리뷰가 존재하지 않습니다.");
				}
			}
		}
	}
	
	// 커뮤니티
	public static void community(String uID, int scID, String scName) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		String inquiry = "select * from Post where scID=" + scID + ";";
		try {
			statement = connection.createStatement();

			boolean state = true;
			while (state) {
				System.out.printf("\n====== %s 커뮤니티 ======\n", scName);
				
				result = statement.executeQuery(inquiry);
				while (result.next()) {
					int postID = result.getInt(1);
                    String title = result.getString(2);
                    String content = result.getString(3);
                    String timestamp = result.getString(4);
                    String userID = result.getString(5);

                    String formattedTimestamp = formatTimestamp(timestamp);

                    System.out.printf("%d %s %s %s %s\n", postID, title, content, formattedTimestamp, userID);
				}
				
				System.out.println("0. 뒤로가기");
				System.out.println("1. 게시물 작성");
				System.out.println("2. 게시물 수정");
				System.out.println("3. 게시물 삭제");
				System.out.print(">> ");

				int menu = scan.nextInt();
				switch (menu) {
					case 0:
						state = false;
						break;
					case 1:
						writePost(uID, scID);
						break;
					case 2:
						updatePost(uID, scID);
						break;
					case 3:
						deletePost(uID, scID);
						break;
					default:
						System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
						break;
				}
			}
		} catch(SQLException ex){
			throw ex;
		}
	}
	
	// 게시물 작성
	public static void writePost(String uID, int scID) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		System.out.println("제목을 입력하세요.");
		String title = scan.nextLine();
		System.out.println("내용을 입력하세요.");
		String content = scan.nextLine();

		String insertPost = "insert into Post(p_title, p_content, uID, scID) values (?, ?, ?, ?)";
		try (PreparedStatement preparedStatement = connection.prepareStatement(insertPost)) {
			preparedStatement.setString(1, title);
			preparedStatement.setString(2, content);
			preparedStatement.setString(3, uID);
			preparedStatement.setInt(4, scID);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("게시물이 작성되었습니다.");
			} else {
				System.out.println("게시물 작성 실패");
			}
		} catch (SQLException ex) {
			throw ex;
		}
	}
	
	// 게시물 수정
	public static void updatePost(String uID, int scID) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		String showUpdate = "select * from Post where scID=" + scID + " and uID ='" + uID + "';";
		try {
			result = statement.executeQuery(showUpdate);
			while (result.next()){
				System.out.printf("%d %s %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
			}
		}catch(SQLException ex) {
			throw ex;
		}
		
		System.out.println("수정할 게시물의 번호를 입력하세요.");
		int pIDToUpdate = scan.nextInt();

		// PreparedStatement를 사용하여 현재 사용자의 게시물인지 확인
		String selectQuery = "select uID from Post where pID = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
			selectStatement.setInt(1, pIDToUpdate);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					String postUID = resultSet.getString("uID");

					// 현재 사용자의 uID와 게시물의 uID가 일치하면 수정 수행
					if (uID.equals(postUID)) {
						// 수정할 내용을 입력 받음
						scan.nextLine(); // 이전에 남아있는 개행 문자 처리
						System.out.println("수정할 제목을 입력하세요.");
						String newTitle = scan.nextLine();
						System.out.println("수정할 내용을 입력하세요.");
						String newContent = scan.nextLine();
						
						String trigger = "create or replace function updateTime() returns trigger as $$ " + 
						 		"begin NEW.p_timestamp=NOW();" + 
						 		"return New;" + 
						 		"end;" + 
						 		"$$ language 'plpgsql';" + 
						 		"create trigger updatePostTime " + 
						 		"before update on Post " + 
						 		"for each row " + 
						 		"execute procedure updateTime();";
						statement = connection.createStatement();
						statement.executeUpdate(trigger);
						
						// PreparedStatement를 사용하여 UPDATE 쿼리 실행
						String updateQuery = "update Post set p_title = ?, p_content = ? where pID = ?";
						try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
							updateStatement.setString(1, newTitle);
							updateStatement.setString(2, newContent);
							updateStatement.setInt(3, pIDToUpdate);

							int rowsAffected = updateStatement.executeUpdate();

							if (rowsAffected > 0) {
								System.out.println("게시물이 수정되었습니다.");
							} else {
								System.out.println("게시물 수정 실패. 해당 번호가 존재하지 않습니다.");
							}
						} catch (SQLException ex) {
							throw ex;
						}
					} else {
						System.out.println("현재 사용자의 게시물이 아닙니다. 수정 권한이 없습니다.");
					}
				} else {
					System.out.println("해당 번호의 게시물이 존재하지 않습니다.");
				}
			}
		} catch (SQLException ex) {
			throw ex;
		}
	}
	
	// 게시물 삭제
	public static void deletePost(String uID, int scID) throws SQLException {
		Scanner scan = new Scanner(System.in);
		
		String showDelete = "select * from Post where scID=" + scID + " and uID ='" + uID + "';";
		try {
			result = statement.executeQuery(showDelete);
			while (result.next()){
				System.out.printf("%d %s %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
			}
		}catch(SQLException ex) {
			throw ex;
		}
		
		// 삭제할 게시물의 pID를 입력 받음
		System.out.println("삭제할 게시물의 번호를 입력하세요.");
		int pIDToDelete = scan.nextInt();

		// PreparedStatement를 사용하여 현재 사용자의 게시물인지 확인
		String selectQuery3 = "select uID from Post where pID = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery3)) {
			selectStatement.setInt(1, pIDToDelete);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					String postUID = resultSet.getString("uID");

					// 현재 사용자의 uID와 게시물의 uID가 일치하면 삭제 수행
					if (uID.equals(postUID)) {
						// PreparedStatement를 사용하여 DELETE 쿼리 실행
						String deleteQuery = "delete from Post where pID = ?";
						try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
							deleteStatement.setInt(1, pIDToDelete);

							int rowsAffected = deleteStatement.executeUpdate();

							if (rowsAffected > 0) {
								System.out.println("게시물이 삭제되었습니다.");
							} else {
								System.out.println("게시물 삭제 실패. 해당 번호가 존재하지 않습니다.");
							}
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					} else {
						System.out.println("현재 사용자의 게시물이 아닙니다. 삭제 권한이 없습니다.");
					}
				} else {
					System.out.println("해당 번호의 게시물이 존재하지 않습니다.");
				}
			}
		}
	}

	// 근처 무료 급식소 조회
	public static void inquiryFMC(int scID, String scName) throws SQLException {
		String scAddress="";
		String query6 = "select scAddress from SeniorCenter where scID="+ scID +";";
		
		try {
			statement = connection.createStatement();
        	result = statement.executeQuery(query6);

        	if(result.next())
        		scAddress=result.getString(1);
        	
        	int index=scAddress.lastIndexOf("로");
        	String roAddress = "";
			if (index != -1) {
				int spaceIndex = scAddress.lastIndexOf(" ", index);
				if (spaceIndex != -1)
					roAddress = scAddress.substring(spaceIndex + 1, index + 1);
				else
					roAddress = scAddress.substring(0, index+1);
			}
			
			String inquiry = "select * from FreeMealCenter where fmcAddress like '%"+ roAddress +"%';";
			result = statement.executeQuery(inquiry);
			
			if(result.next())
			{
				System.out.printf("'%s' 근처에 위치한 무료급식소 정보입니다.\n", scName);
				
				String tab ="\t\t";
				
				//System.out.printf("%-20s %-20s %-15s %-20s %-30s %-20s %-15s\n", "시설명", "주소", "전화번호", "장소", "대상", "시간", "요일");
				while (result.next()) {
					System.out.printf("%-20s %-20s %-15s %-20s %-30s %-20s %-15s\n", result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7));
				}
			}
			else
				System.out.printf("'%s' 근처에 위치한 무료급식소 정보입니다.\n", scName);

		}catch(SQLException ex) {
			throw ex;
		}
	}
}
