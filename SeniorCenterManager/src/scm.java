import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.PreparedStatement;

public class SqlTest3 {

	static String url = "jdbc:postgresql://127.0.0.1:5432/SENIORDB";
	static String username = "mambasa";
	static String password = "tkandpf0@";

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
					scan.nextLine();
					System.out.println("예약 날짜를 입력하세요.(ex: YYYY-MM-DD).");
					String reserveDate = scan.nextLine();
					// 예약 가능한 시간대 확인
					System.out.println("예약 가능한 시간대:");

					for (int hour = 9; hour <= 17; hour++) {
						String reserveTime = hour + ":00";

						if (!isReserved(connection, scID, reserveDate, reserveTime)) {
							System.out.println(reserveTime);
						}
					}

					System.out.println("예약할 시간대를 입력하세요.(ex: HH:00)");
					String reserveTime = scan.nextLine();

					// 예약 수행
					if (!isReserved(connection, scID, reserveDate, reserveTime)) {
						performReservation(connection, reserveDate, reserveTime, scID, uID);
						System.out.println("예약이 완료되었습니다.");
					} else {
						System.out.println("이미 예약된 시간대입니다. 다른 시간을 선택하세요.");
					}
					break;
				case 4:
					// 리뷰()
					review(uID, scID, scName);
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
	private static boolean isReserved(Connection connection, int scID, String reserveDate, String reserveTime) throws SQLException {
		String selectQuery = "SELECT * FROM Reservation WHERE scID = ? AND reserveDate = ? AND reserveTime = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
			selectStatement.setInt(1, scID);
			selectStatement.setString(2, reserveDate);
			selectStatement.setString(3, reserveTime);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				return resultSet.next(); // 결과가 있으면 이미 예약이 되어 있는 것
			}
		}
	}
	private static void performReservation(Connection connection, String reserveDate, String reserveTime, int scID, String uID) throws SQLException {
		String insertQuery = "INSERT INTO Reservation(reserveDate, reserveTime, uID, scID) VALUES (?, ?, ?, ?)";
		try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
			insertStatement.setString(1, reserveDate);
			insertStatement.setString(2, reserveTime);
			insertStatement.setString(3, uID);
			insertStatement.setInt(4, scID);

			insertStatement.executeUpdate();
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
	// 리뷰
	public static void review(String uID, int scID, String scName) throws SQLException {


		String query5 = "select * from Review where scID='" + scID + "';";
		try {
			statement = connection.createStatement();
			Scanner scan = new Scanner(System.in);
			Scanner scan2 = new Scanner(System.in);

			boolean state = true;
			while (state) {
				System.out.printf("\n====== %s 리뷰 ======\n", scName);
				result = statement.executeQuery(query5);
				while (result.next())
					System.out.printf("%d %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
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
						System.out.println("리뷰 내용을 입력하세요.");
						String content = scan2.nextLine();

						String insertPost = "INSERT INTO Review(r_content, uID, scID) VALUES (?, ?, ?)";
						try (PreparedStatement preparedStatement = connection.prepareStatement(insertPost)) {
							preparedStatement.setString(1, content);
							preparedStatement.setString(2, uID);
							preparedStatement.setInt(3, scID);

							int rowsAffected = preparedStatement.executeUpdate();

							if (rowsAffected > 0) {
								System.out.println("리뷰가 작성되었습니다.");
							} else {
								System.out.println("리뷰 작성 실패");
							}
						} catch (SQLException ex) {
							throw ex;
						}
						break;
					case 2:
						String showUpdate = "select * from Review where scID = '" + scID + "' and uID ='" + uID + "';";
						result = statement.executeQuery(showUpdate);
						while(result.next()){
							System.out.printf("%d %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
						}
						// 수정할 게시물의 pID를 입력 받음
						System.out.println("수정할 리뷰의 번호를 입력하세요.");
						int rIDToUpdate = scan.nextInt();

						// PreparedStatement를 사용하여 현재 사용자의 게시물인지 확인
						String selectQuery = "SELECT uID FROM Review WHERE reviewID = ?";
						try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
							selectStatement.setInt(1, rIDToUpdate);

							try (ResultSet resultSet = selectStatement.executeQuery()) {
								if (resultSet.next()) {
									String reviewUID = resultSet.getString("uID");

									// 현재 사용자의 uID와 게시물의 uID가 일치하면 수정 수행
									if (uID.equals(reviewUID)) {
										// 수정할 내용을 입력 받음
										scan.nextLine(); // 이전에 남아있는 개행 문자 처리
										System.out.println("수정할 내용을 입력하세요.");
										String newContent = scan.nextLine();

										// PreparedStatement를 사용하여 UPDATE 쿼리 실행
										String updateQuery = "UPDATE Review SET r_content = ? WHERE reviewID = ?";
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
						break;

					case 3:
						String showDelete = "select * from Review where scID = '" + scID + "' and uID ='" + uID + "';";
						result = statement.executeQuery(showDelete);
						while (result.next()){
							System.out.printf("%d %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
						}
						// 삭제할 게시물의 pID를 입력 받음
						System.out.println("삭제할 리뷰의 pID를 입력하세요.");
						int pIDToDelete = scan.nextInt();

						// PreparedStatement를 사용하여 현재 사용자의 리뷰인지 확인
						String selectQuery2 = "SELECT uID FROM Review WHERE reviewID = ?";
						try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery2)) {
							selectStatement.setInt(1, pIDToDelete);

							try (ResultSet resultSet = selectStatement.executeQuery()) {
								if (resultSet.next()) {
									String postUID = resultSet.getString("uID");

									// 현재 사용자의 uID와 리뷰의 uID가 일치하면 삭제 수행
									if (uID.equals(postUID)) {
										// PreparedStatement를 사용하여 DELETE 쿼리 실행
										String deleteQuery = "DELETE FROM Review WHERE reviewID = ?";
										try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
											deleteStatement.setInt(1, pIDToDelete);

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

	// 커뮤니티
	public static void community(String uID, int scID, String scName) throws SQLException {

		String query5 = "select * from Post where scID='" + scID + "';";
		try {
			Scanner scan = new Scanner(System.in);
			Scanner scan2 = new Scanner(System.in);
			statement = connection.createStatement();

			boolean state = true;
			while (state) {
				System.out.printf("\n====== %s 커뮤니티 ======\n", scName);
				result = statement.executeQuery(query5);
				while (result.next())
					System.out.printf("%d %s %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
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
						//게시물 작성
						System.out.println("제목을 입력하세요.");
						String title = scan2.nextLine();
						System.out.println("내용을 입력하세요.");
						String content = scan2.nextLine();

						String insertPost = "INSERT INTO Post(p_title, p_content, uID, scID) VALUES (?, ?, ?, ?)";
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
						break;
					case 2:
						String showUpdate = "select * from Post where scID = '" + scID + "' and uID ='" + uID + "';";
						result = statement.executeQuery(showUpdate);
						while (result.next()){
							System.out.printf("%d %s %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
						}
						System.out.println("수정할 게시물의 번호를 입력하세요.");
						int pIDToUpdate = scan.nextInt();

						// PreparedStatement를 사용하여 현재 사용자의 게시물인지 확인
						String selectQuery = "SELECT uID FROM Post WHERE pID = ?";
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

										// PreparedStatement를 사용하여 UPDATE 쿼리 실행
										String updateQuery = "UPDATE Post SET p_title = ?, p_content = ? WHERE pID = ?";
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
						break;
					case 3:
						String showDelete = "select * from Post where scID = '" + scID + "' and uID ='" + uID + "';";
						result = statement.executeQuery(showDelete);
						while (result.next()){
							System.out.printf("%d %s %s %s %s\n", result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
						}
						// 삭제할 게시물의 pID를 입력 받음
						System.out.println("삭제할 게시물의 번호를 입력하세요.");
						int pIDToDelete = scan.nextInt();

						// PreparedStatement를 사용하여 현재 사용자의 게시물인지 확인
						String selectQuery3 = "SELECT uID FROM Post WHERE pID = ?";
						try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery3)) {
							selectStatement.setInt(1, pIDToDelete);

							try (ResultSet resultSet = selectStatement.executeQuery()) {
								if (resultSet.next()) {
									String postUID = resultSet.getString("uID");

									// 현재 사용자의 uID와 게시물의 uID가 일치하면 삭제 수행
									if (uID.equals(postUID)) {
										// PreparedStatement를 사용하여 DELETE 쿼리 실행
										String deleteQuery = "DELETE FROM Post WHERE pID = ?";
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
// 0. 뒤로 가기ㄴ
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
