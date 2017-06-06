package depr;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Insert {

	public static void main(String[] args) {
		
		insert("something@yahoo.com", "Messi");

	}
	
public static void insert(String email, String player) {
		
		Connection connection = null;
		long currentTime = System.nanoTime();  //current time
		try{
			String url = "jdbc:sqlite:../server/db/pmr.db";
			connection = DriverManager.getConnection(url);
			String sql = "INSERT INTO Timeq(Email, Player, Timestamp)"
					+ " VALUES(?,?,?)";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, player);
			preparedStatement.setLong(3, currentTime);
			
			preparedStatement.executeUpdate(); 
			System.out.println("Item inserted successfully");
		} catch (SQLException e){
			System.out.println(e.getMessage());
		} finally {
			try{
				if (connection != null){
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		
	}

}
