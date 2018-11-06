package ee417;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		try {
			System.out.println("\nConnecting to the SSD Database......");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@ee417.c7clh2c6565n.eu-west-1.rds.amazonaws.com:1521:EE417", "ee_user", "ee_pass");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		User user = validateUser(username, password);
		if (user!=null) {   // login succeeded
			HttpSession session = req.getSession();
			session.setAttribute("theUser", user);  // put the OBJECT on the session
			res.sendRedirect("/BankingSystem/AccountHome");
			if(req.getParameter("login") == "true") {
				res.sendRedirect("/BankingSystem/AccountHome");
			}
		}
		else {
			res.sendRedirect("login.html");   
		}
		
		out.close();
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	private User validateUser(String username, String password) {
		
		try {
			String query = "SELECT * FROM EJAB_USERS where username = '" + username + "' and password = '" + password + "'";
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			while(rs.next()) {
				User user= new User(rs.getString("firstname"), rs.getString("surname"), username, password);
				if((user.getUsername().equals(rs.getString("username")) && user.getPassword().equals(rs.getString("password")))) {
					return user;
				}
			}
			stmt.close();
			rs.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}