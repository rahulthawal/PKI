package server_gui;

/**
 *
 * @author Sriteja Nallamilli
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
public class DBConn
{
    private static Connection con;
    
    /**
     * This is the program used to create a JDBC connection to MYSQL and perform the operations.
     * @return
     * @throws Exception 
     */
    
public static Connection getCon()throws Exception 
{
    Class.forName("com.mysql.jdbc.Driver");
    con = DriverManager.getConnection("jdbc:mysql://localhost/networksecurity","root","happy");
    return con;
}

// code to handle whenever there is a NEW USER i.e registeration.
public static String register(String[] input)throws Exception
{
    String msg="fail";
    boolean flag=false;
    boolean flag1=false;
	int acc = 1001;
    con = getCon();
    Statement stmt=con.createStatement();
    
    ResultSet rs=stmt.executeQuery("select username from user where username='"+input[0]+"'");
    // code to handle if username is already existed.
    if(rs.next())
    {
        flag=true;
        msg = "uexist";
    }
    stmt=con.createStatement();
    rs=stmt.executeQuery("select password from user where password='"+input[1]+"'");
    
    // code to handle if password is already present.
    
    if(rs.next() && !flag)
    {
        flag1=true;
        msg = "pexist";
    }
	stmt=con.createStatement();
    rs=stmt.executeQuery("select count(*) from user");
    
    while(rs.next())
    {
        acc = rs.getInt(1);
    }
	acc = acc + 100;
	if(!flag && !flag1)
        {
		java.util.Date d1 = new java.util.Date();
		java.sql.Date d2 = new java.sql.Date(d1.getTime());
                // Insert the data into newuser
		PreparedStatement stat=con.prepareStatement("insert into user values(?,?,?,?,?,?)");
                
		stat.setString(1,input[0]);
		stat.setString(2,input[1]);
		stat.setString(3,input[2]);
		stat.setDate(4,d2);
		stat.setString(5,Integer.toString(acc));
		stat.setString(6,"accept");
		int i=stat.executeUpdate();
		stat.close();
		if(i > 0)
                {
			msg = "success,"+acc;
		}
        }
        
	rs.close();stmt.close();con.close();
    return msg;
}

// code to handle login whenever an unknown username tries to enter it sends an error.
public static String login(String input[])throws Exception
{
    String msg="fail";
    con = getCon();
    Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select username,password from user where status='accept' and username='"+input[0]+"' and password='"+input[1]+"'");
    if(rs.next())
    {
        msg = "success";
    }
	rs.close();stmt.close();con.close();
    return msg;
}

/**
 * This is the code to deposit amount on request from the client.
 * @param acc
 * @param amount
 * @return
 * @throws Exception 
 */
public static String deposit(String acc,double amount)throws Exception{
	java.util.Date d1 = new java.util.Date();
	java.sql.Date d2 = new java.sql.Date(d1.getTime());
	double amt = 0;
	con = getCon();
	String msg = "fail";
	Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select account_no from user where account_no='"+acc+"'");
	if(rs.next()){
		stmt=con.createStatement();
		rs=stmt.executeQuery("select amount from transaction where account_no='"+acc+"'");
		if(rs.next()){
			amt = rs.getDouble(1);
			amt = amt + amount;
			PreparedStatement stat = con.prepareStatement("update transaction set amount = ?,transaction_date=?,transaction_type=? where account_no = ?");
			stat.setDouble(1,amt);
			stat.setDate(2,d2);
			stat.setString(3,"Deposit");
			stat.setString(4,acc);
			int i = stat.executeUpdate();
			stat.close();
			if(i > 0){
				msg = "success,"+amt;
			}
		}else{
			PreparedStatement stat = con.prepareStatement("insert into transaction values(?,?,?,?)");
			stat.setString(1,acc);
			stat.setDouble(2,amount);
			stat.setDate(3,d2);
			stat.setString(4,"Deposit");
			int i = stat.executeUpdate();
			stat.close();
			if(i > 0){
				msg = "success,"+amount;
			}
		}
	}else{
		msg = "invalid";
	}
	rs.close();stmt.close();con.close();
	return msg;
}

/**
 * If the message sent is balance, perform the below code of operation.
 * @param acc
 * @return
 * @throws Exception 
 */
public static String balance(String acc)throws Exception{
	java.util.Date d1 = new java.util.Date();
	java.sql.Date d2 = new java.sql.Date(d1.getTime());
	double amt = 0;
	con = getCon();
	String msg = "fail";
	Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select account_no from user where account_no='"+acc+"'");
	if(rs.next()){
		stmt=con.createStatement();
		rs=stmt.executeQuery("select amount from transaction where account_no='"+acc+"'");
		if(rs.next()){
			amt = rs.getDouble(1);
			msg = "success,"+amt;
		}else
			msg = "invalid";
	}else{
		msg = "invalid";
	}
	rs.close();stmt.close();con.close();
	return msg;
}



/**
 * This is the code to check if the message sent is Withdraw and access the database if the keyword matches
 * and desired amount is debited from the account. Here we are using account as primary key.
 * @param acc
 * @param amount
 * @return
 * @throws Exception 
 */
public static String withdraw(String acc,double amount)throws Exception{
	java.util.Date d1 = new java.util.Date();
	java.sql.Date d2 = new java.sql.Date(d1.getTime());
	double amt = 0;
	con = getCon();
	String msg = "fail";
	Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select account_no from user where account_no='"+acc+"'");
	if(rs.next()){
		stmt=con.createStatement();
		rs=stmt.executeQuery("select amount from transaction where account_no='"+acc+"'");
		if(rs.next()){
			amt = rs.getDouble(1);
			if(amt > amount){
				amt = amt - amount;
				PreparedStatement stat = con.prepareStatement("update transaction set amount = ?,transaction_date=?,transaction_type=? where account_no = ?");
				stat.setDouble(1,amt);
				stat.setDate(2,d2);
				stat.setString(3,"withdrawl");
				stat.setString(4,acc);
				int i = stat.executeUpdate();
				stat.close();
				if(i > 0){
					msg = "success,"+amt;
				}
			}else{
				msg = "insufficient";
			}
		}else{
			msg = "invalid";
		}
	}else{
		msg = "invalid";
	}
	rs.close();stmt.close();con.close();
	return msg;
}
}