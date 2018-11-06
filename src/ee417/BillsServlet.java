package ee417;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/BillsServlet")
public class BillsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		HttpSession session = req.getSession(true);
		User user = (User) session.getAttribute("theUser");

		String type = req.getParameter("pay");
		int cost = Integer.parseInt(req.getParameter("billamount"));

		int current_user_amount = 0;
		int new_current_user_amount = 0;

		int senderID = 0;
		int transaction = 0;

		if(user!=null) {   // logged in and active session
			try {
				System.out.println("\nConnecting to the SSD Database......");
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@ee417.c7clh2c6565n.eu-west-1.rds.amazonaws.com:1521:EE417", "ee_user", "ee_pass");
				stmt = con.createStatement();
				rs = stmt.executeQuery("select * from ejab_users where firstname='"+ user.getFirstname() + "' and surname='" + user.getSurname() + "'");

				while (rs.next()) {
					current_user_amount = rs.getInt("amount");
					new_current_user_amount = current_user_amount - cost;
					senderID = rs.getInt("id");
				}
				if(new_current_user_amount >=0) {
					String query = "update ejab_users set amount=" + new_current_user_amount + " where firstname='"+user.getFirstname() + 
							"' and surname='" + user.getSurname() + "'";
					stmt=con.createStatement();
					rs = stmt.executeQuery(query);
					stmt = con.createStatement();
					rs = stmt.executeQuery("select max(transactionid) from ejab_senders");

					while (rs.next()) {
						transaction = rs.getInt(1);
					}
					transaction++;
					query = "insert into ejab_senders (transactionid, senderid,senderfirst,senderlast,transfer,balance,transactiontime,towards) "
							+ "values(" + transaction + ", " + senderID + ", '" + user.getFirstname() + "','" + user.getSurname() + "', " + 
							cost + ", " + new_current_user_amount + ", TO_DATE('" + sdf() + "', 'YYYY/MM/DD HH:MI:SS'),'" + type + "')";
					stmt=con.createStatement();
					rs=stmt.executeQuery(query);
					res.sendRedirect("bills.html");
				}
				else {
					stmt = con.createStatement();
					rs = stmt.executeQuery("select max(transactionid) from ejab_senders");

					while (rs.next()) {
						transaction = rs.getInt(1);
					}
					transaction++;
					String query = "insert into ejab_senders (transactionid, senderid,senderfirst,senderlast,transfer,balance,transactiontime,towards) "
							+ "values(" + transaction + ", " + senderID + ", '" + user.getFirstname() + "','" + user.getSurname() + "', " + 
							cost + ", " + current_user_amount + ", TO_DATE('" + sdf() + "', 'YYYY/MM/DD HH:MI:SS'), '" + type + " - INVALID - Not enough balance')";
					stmt=con.createStatement();
					rs=stmt.executeQuery(query);
					res.sendRedirect("bills.html");
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
			if(req.getParameter("logout") != null) {
				session.invalidate();
				res.sendRedirect("index.html");
			}

		}
		else {   // not logged in or timed out
			res.sendRedirect("login.html");
		} 
	}

	public String sdf() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println( sdf.format(cal.getTime()) );
		return sdf.format(cal.getTime());
	}
}