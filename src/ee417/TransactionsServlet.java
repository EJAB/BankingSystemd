package ee417;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/TransactionsServlet")
public class TransactionsServlet extends HttpServlet {
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
			String firstname = user.getFirstname();
			String surname = user.getSurname();
			try {
				System.out.println("\nConnecting to the SSD Database......");
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@ee417.c7clh2c6565n.eu-west-1.rds.amazonaws.com:1521:EE417", "ee_user", "ee_pass");
				stmt = con.createStatement();
				rs = stmt.executeQuery("select * from ejab_senders where senderfirst='"
						+ firstname + "' and senderlast='"
						+ surname + "' order by transactionid desc");
				
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
							"		<a href=\"/BankingSystem/DetailsServlet\" class=\"w3-bar-item w3-button\">Account Details</a> \n" + 
							"		<a  class=\"current\"href=\"/BankingSystem/TransactionsServlet\" class=\"w3-bar-item w3-button\">Past Transactions</a> \n" + 
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
							+ "<div class=\"container\">");
					
					out.print("<h2>FROM: " + user.getFirstname() + " " + user.getSurname() + "</h2><table border=1>\n");
					out.print("<tr><th>Transfer Amount</th><th>Balance</th><th>Date and Time</th><th>Receiver</th><tr>");             

					while(rs.next()) {
						out.println("<tr><td>" + rs.getString("transfer") + "</td>" +
						"<td>" + rs.getString("balance") + "</td>" +
						"<td>" + rs.getString("transactiontime") + "</td>" +
						"<td>" + rs.getString("towards") + "</td></tr>");
	                }
					out.println("</table>");
					
					stmt = con.createStatement();
					rs = stmt.executeQuery("select * from ejab_receivers where receiverfirst='"
							+ firstname + "' and receiverlast='"
							+ surname + "' order by transactionid desc");
					
					out.print("<h2>TO: " + user.getFirstname() + " " + user.getSurname() + "</h2><table border=1r>\n");
					out.print("<tr><th>Transfer Amount</th><th>Balance</th><th>Date and Time</th><tr>");             

					while(rs.next()) {
						out.println("<tr><td>" + rs.getString("transfer") + "</td>" +
						"<td>" + rs.getString("balance") + "</td>" +
						"<td>" + rs.getString("transactiontime") + "</td></tr>");
	                }
					out.println("</table>");
	                
	                out.println("	\n" + 
					"	</div><script>\n" + 
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
	                if(req.getParameter("logout") != null) {
	    				session.invalidate();
	    				res.sendRedirect("index.html");
	    			}
			}
			catch (Exception e) {
				e.printStackTrace();
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