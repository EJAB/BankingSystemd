package ee417;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;
import oracle.jdbc.driver.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String surname = request.getParameter("surname");
		String firstName = request.getParameter("firstname");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int amount = Integer.parseInt(request.getParameter("amount"));
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int id = 0;

		try {
			System.out.println("\nConnecting to the SSD Database......");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@ee417.c7clh2c6565n.eu-west-1.rds.amazonaws.com:1521:EE417", "ee_user", "ee_pass");
		}
		catch (Exception e) {
			out.println("An error has occurred during the connection phase! Did you upload your Oracle Drivers?"); 
		}   

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("select max(id) from ejab_users");

			while (rs.next()) {
				id = rs.getInt(1);
			}
			
			id++;

			User user = validateUser(username, password);
			if(user != null) { // if username and password already exist, user is prompted to login
				response.sendRedirect("login.html");
			}
			else {
				stmt = con.createStatement();
				rs = stmt.executeQuery("INSERT INTO EJAB_USERS (ID, SURNAME, FIRSTNAME, USERNAME, PASSWORD, AMOUNT, EMAIL, PHONE) VALUES ("
						+ id + ", '" 
						+ surname + "', '"
						+ firstName + "', '" 
						+ username + "', '" 
						+ password + "', " 
						+ amount + ", '" 
						+ email + "', '"
						+ phone
						+ "')");
				response.sendRedirect("login.html");
			}
		}
		catch (Exception e) {
			out.println("<BR>An error has occurred during the Statement/ResultSet phase.  Please check the syntax and study the Exception details!");
			e.printStackTrace();
		}   

		finally {
			try {    
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			}
			catch (Exception ex) {
				out.println("<BR>An error occurred while closing down connection/statement"); 
			}
		}
		out.close();
	}
	
	private User validateUser(String username, String password) {
		try {
			String query = "SELECT * FROM EJAB_USERS where username = '" + username + "' and password = '" + password + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);

			while(rs.next()) {
				User user= new User(rs.getString("firstname"), rs.getString("surname"), username, password);
				if((user.getUsername().equals(rs.getString("username")) && user.getPassword().equals(rs.getString("password")))) {
					return null;
				}
			}
			st.close();
			rs.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}