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

@WebServlet("/TransferServlet")
public class TransferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		HttpSession session = req.getSession(true);
		User user = (User) session.getAttribute("theUser");

		String firstname = req.getParameter("firstname");
		String surname = req.getParameter("surname");
		int sending_amount = Integer.parseInt(req.getParameter("amount"));
		int current_user_amount = 0;
		int new_current_user_amount = 0;

		int current_receiver_amount = 0;
		int new_current_receiver_amount = 0;

		int senderID = 0;
		int receiverID = 0;
		int transaction = 0;

		if(user!=null) {   // logged in and active session
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@ee417.c7clh2c6565n.eu-west-1.rds.amazonaws.com:1521:EE417", "ee_user", "ee_pass");
				stmt = con.createStatement();
				rs = stmt.executeQuery("select * from ejab_users where firstname='"+ user.getFirstname() + "' and surname='" + user.getSurname() + "'");

				while (rs.next()) {
					current_user_amount = rs.getInt("amount");
					new_current_user_amount = current_user_amount - sending_amount;
					senderID = rs.getInt("id");
				}

				if(new_current_user_amount >= 0) {
					String query = "update ejab_users set amount=" + new_current_user_amount + " where firstname='"+user.getFirstname() + 
							"' and surname='" + user.getSurname() + "'";
					stmt=con.createStatement();
					rs = stmt.executeQuery(query);

					stmt = con.createStatement();
					rs = stmt.executeQuery("select * from ejab_users where firstname='"+ firstname + "' and surname='" + surname + "'");

					while (rs.next()) {
						current_receiver_amount = rs.getInt("amount");
						new_current_receiver_amount = current_receiver_amount + sending_amount;
						receiverID = rs.getInt("id");
					}
					query = "update ejab_users set amount=" + new_current_receiver_amount + " where firstname='"+firstname + "' and surname='" + surname + "'";
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
							sending_amount + ", " + new_current_user_amount + ", TO_DATE('" + sdf() + "', 'YYYY/MM/DD HH:MI:SS'),'" + firstname + " " + surname +"')";
					stmt=con.createStatement();
					rs=stmt.executeQuery(query);

					String command = "insert into ejab_receivers (transactionid,receiverid,receiverfirst,receiverlast,transfer,balance,transactiontime) "
							+ "values(" + transaction + ", " + receiverID + ", '" + firstname + "','" + surname + "', " + 
							sending_amount + ", " + new_current_receiver_amount + ", TO_DATE('" + sdf() + "', 'YYYY/MM/DD HH:MI:SS'))";
					stmt = con.createStatement();
					rs = stmt.executeQuery(command);
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
							sending_amount + ", " + current_user_amount + ", TO_DATE('" + sdf() + "', 'YYYY/MM/DD HH:MI:SS'),'" + firstname + " " + 
							surname + " - INVALID - Not enough balance')";
					stmt=con.createStatement();
					rs=stmt.executeQuery(query);
				}
				res.sendRedirect("transfer.html");
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