import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	private Connection conn; //Declare datababse Connection
	
	

	public Database() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/tablecust",
				"root",
				""
				);
	}
	
	public Connection getConn() {
		return conn;
	}
	public void close() throws SQLException{
		conn.close();
		
	}
	
	public String login(String username, String password) throws SQLException{
		String query = "SELECT NAMA FROM TABLECUST WHERE USERNAME = ? AND PASSWORD = ?";
		String nama = null;
		PreparedStatement pst = conn.prepareStatement(query);
		pst.setString(1, username);
		pst.setString(2, password);
		
		ResultSet rs = pst.executeQuery();
		int count=0;
		while(rs.next()){
			count++;
			nama = rs.getString(1);
		}
		
		if(count == 1){
			return nama;
		}
		return "NULL";
	}
	
}