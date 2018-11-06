package ee417;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AccountHome")
public class AccountHome extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		HttpSession session = req.getSession(true);
		User user = (User) session.getAttribute("theUser");
		if(user!=null) {   // logged in and active session
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
					"					<li class=\"current\"><a href=\"/BankingSystem/AccountHome\">Home</a></li>\n" + 
					"					<li><a href=\"#\">Login</a></li>\n" + 
					"				</ul>\n" + 
					"			</nav>\n" + 
					"		</div>\n" + 
					"	</header>");

			out.println("	<section id=\"showcase\">\n" + 
					"		<div class=\"container\">\n" + 
					"<h1> Welcome " + user.getFirstname() + " " + user.getSurname() + " to EJAB Bank online banking system for "
							+ "your accessibility and\n" + "efficiency</h1>\n" + 
					"			<p>.</p>\n" + 
					"		</div>\n" + 
					"	</section>\n");

			out.println("<section id=\"boxes\">\n" + 
					"		<div class=\"container\">\n" + 
					"			<div class=\"box\">\n" + 
					"				<h3>View account details and past transactions</h3>\n" + 
					"				<p>Users have the ability to view their account details, change\n" + 
					"					their current address and contact details, and view their past\n" + 
					"					transactions.</p>\n" + 
					"			</div>\n" + 
					"			<div class=\"box\">\n" + 
					"				<h3>Transfer money between accounts</h3>\n" + 
					"				<p>Users have the ability to transfer money between their\n" + 
					"					accounts, and other saved users.</p>\n" + 
					"			</div>\n" + 
					"			<div class=\"box\">\n" + 
					"				<h3>Pay off bills</h3>\n" + 
					"				<p>Users have the ability to pay off bills, whether it be\n" + 
					"					electricity, mortgage, or any other outstanding loans.</p>\n" + 
					"			</div>\n" + 
					"		</div>\n" + 
					"	</section>\n" + 
					"<script>\n" + 
					"		function w3_open() {\n" + 
					"			document.getElementById(\"showcase\").style.marginLeft = \"25%\";\n" + 
					"			document.getElementById(\"boxes\").style.marginLeft = \"25%\";\n" + 
					"			document.getElementById(\"mySidebar\").style.width = \"25%\";\n" + 
					"			document.getElementById(\"mySidebar\").style.display = \"block\";\n" + 
					"			document.getElementById(\"openNav\").style.display = 'none';\n" + 
					"		}\n" + 
					"		function w3_close() {\n" + 
					"			document.getElementById(\"showcase\").style.marginLeft = \"0%\";\n" + 
					"			document.getElementById(\"boxes\").style.marginLeft = \"0%\";\n" + 
					"			document.getElementById(\"mySidebar\").style.display = \"none\";\n" + 
					"			document.getElementById(\"openNav\").style.display = \"inline-block\";\n" + 
					"		}\n" + 
					"	</script>" +
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
		else {   // not logged in or timed out
			res.sendRedirect("index.html");
		} 
		out.close();
	}
}