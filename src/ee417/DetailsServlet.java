package ee417;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DetailsServlet")
public class DetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		HttpSession session = req.getSession(true);
		User user = (User) session.getAttribute("theUser");

		if (user!=null) {
			String username = user.getUsername();
			String password = user.getPassword();
			try {
				System.out.println("\nConnecting to the SSD Database......");
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@ee417.c7clh2c6565n.eu-west-1.rds.amazonaws.com:1521:EE417", "ee_user", "ee_pass");
				stmt = con.createStatement();
				rs = stmt.executeQuery("select * from ejab_users where username='"
						+ username + "' and password='"
						+ password + "'");
				while (rs.next()) {
					out.println("<!DOCTYPE html>\n" + 
							"<html>\n" + 
							"<head>\n" + 
							"<meta charset=\"utf-8\">\n" + 
							"<meta name=\"viewport\" content=\"width=device-width\">\n" + 
							"<meta name=\"description\" content=\"Online Banking System Assignment\">\n" + 
							"<meta name=\"author\" content=\"Emi Janela Bumanglag\">\n" + 
							"<title>EJAB Bank | Welcome</title>\n" + 
							"<link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\">\n" + 
							"<link rel=\"stylesheet\" href=\"./css/fontawesome.min.css\">\n" + 
							"<link rel=\"stylesheet\" href=\"./css/style.css\">\n" + 
							"</head>\n" + 
							"<body>\n" + 
							"	<div class=\"w3-sidebar w3-bar-block w3-card w3-animate-left\"\n" + 
							"		style=\"display: none\" id=\"mySidebar\">\n" + 
							"		<button class=\"w3-bar-item w3-button w3-large\" onclick=\"w3_close()\">Close\n" + 
							"			&times;</button>\n" + 
							"		<a  class=\"current\"href=\"/BankingSystem/DetailsServlet\" class=\"w3-bar-item w3-button\">Account Details</a> \n" + 
							"		<a href=\"/BankingSystem/TransactionsServlet\" class=\"w3-bar-item w3-button\">Past Transactions</a> \n" + 
							"		<a href=\"transfer.html\" class=\"w3-bar-item w3-button\">Transfer to another account</a>\n" + 
							"		<a href=\"bills.html\" class=\"w3-bar-item w3-button\">Pay bills</a>\n" +
							"		<form>\n" + 
							"			<button class=\"w3-bar-item w3-button\" name=\"logout\">Log out</button>\n" + 
							"		</form>\n"+
							"	</div>\n" +
							"\n" + 
							"	<header>\n" + 
							"		<button id=\"openNav\" class=\"w3-button\" onclick=\"w3_open()\">&#9776;</button>\n" + 
							"		<div class=\"container\">\n" + 
							"			<div id=\"branding\">\n" + 
							"				<h1>\n" + 
							"					<span class=\"highlight\">EJAB</span>\n" + 
							"					Bank\n" + 
							"				</h1>\n" + 
							"			</div>\n" + 
							"			<nav>\n" + 
							"				<ul>\n" + 
							"					<li><a href=\"/BankingSystem/AccountHome\">Home</a></li>\n" + 
							"					<li><a href=\"/BankingSystem/AccountHome\">Login</a></li>\n" + 
							"				</ul>\n" + 
							"			</nav>\n" + 
							"		</div>\n" + 
							"	</header>"
							+ "<div class=\"container\"><h2>Please find below the user details:</h2>");

					out.println("	<table border=1>\n" + 
							"		<tr><th>Identifier</th><th>Information</th></tr>\n" + 
							"		<tr><td>Name</td><td>" + rs.getString("firstname") + " " + rs.getString("surname") +"</td></tr>\n" + 
							"		<tr><td>Username</td><td>" + rs.getString("username") + "</td></tr>\n" + 
							"		<tr><td>Password</td><td>" + rs.getString("password") + "</td></tr>\n" + 
							"		<tr><td>Amount</td><td>" + rs.getString("amount") + "</td></tr>\n" + 
							"		<tr><td>Email</td><td>" + rs.getString("email") + "</td></tr>\n" + 
							"		<tr><td>Phone</td><td>" + rs.getString("phone") + "</td></tr>\n" + 
							"		<tr><td>User ID</td><td>" + rs.getInt("id") + "</td></tr>\n" +
							"    </table>");

					out.print("	</div><script>\n" + 
							"		function w3_open() {\n" +  
							"			document.getElementById(\"mySidebar\").style.width = \"25%\";\n" + 
							"			document.getElementById(\"mySidebar\").style.display = \"block\";\n" + 
							"			document.getElementById(\"openNav\").style.display = 'none';\n" + 
							"		}\n" + 
							"		function w3_close() {\n" +
							"			document.getElementById(\"mySidebar\").style.display = \"none\";\n" + 
							"			document.getElementById(\"openNav\").style.display = \"inline-block\";\n" + 
							"		}\n" + 
							"	</script>\n" + 
							"\n" + 
							"	<footer>\n" + 
							"		<p>Emi Janela Bumanglag, Copyright &copy; 2018</p>\n" + 
							"	</footer>\n" + 
							"\n" + 
							"</body>\n" + 
							"</html>");
				}
				if(req.getParameter("logout") != null) {
					session.invalidate();
					res.sendRedirect("index.html");
				}
			}
			catch (Exception e) {
				out.println("An error has occurred during the connection phase! Did you upload your Oracle Drivers?"); 
			}
			finally {
				try {    
					if (rs != null) rs.close();
					if (stmt != null) stmt.close();
					if (con != null) con.close();
				}
				catch (Exception ex) {
					System.out.println("An error occurred while closing down connection/statement"); 
				}
			}
		} 
		else {
			res.sendRedirect("login.html");
		}
		out.close();
	}
}