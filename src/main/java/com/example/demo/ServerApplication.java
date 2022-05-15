package com.example.demo;

import com.example.demo.dao.ServerDataAccessService;
import com.example.demo.p4p.util.P4PParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

@SpringBootApplication
public class ServerApplication {
	private final static String url = "jdbc:postgresql://localhost:5432/server3";
	private final static String user = "server3";
	private final static String password = "password";
	public static Connection connect() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
	public static void main(String[] args) {
		truncateUPerson();
		P4PParameters.initialize(512,true);
		ServerDataAccessService.server.init();
		SpringApplication.run(ServerApplication.class, args);
	}

	public static void truncateUPerson(){
		String SQL = "TRUNCATE TABLE U_PERSON_DATA; ";
		try (Connection conn = connect();
			 PreparedStatement pstmt = conn.prepareStatement(SQL,
					 Statement.RETURN_GENERATED_KEYS)) {
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					System.out.println(rs);
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
