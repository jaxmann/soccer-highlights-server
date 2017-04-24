package serv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//CREATE TABLE Timeq
//(
//Email varchar(50),
//Player varchar(50),
//Timestamp integer
//);


//delete database records older than 2 mins - run this job every 1 min
public class Delete {

	//MAKE THIS FILE INTO A JAR
	public static void main(String[] args) {
		
		delete(); //delete older than 2 mins

	}
	
	
	public static void delete() {
		Connection connection = null;
		PreparedStatement statement = null;
		
		long twoMinsAgo = System.nanoTime() - 120000000000L; //2 mins in nanoseconds
		
		try{
			String url = "jdbc:sqlite:/home/pi/server/db/pmr.db";
			connection = DriverManager.getConnection(url);
			System.nanoTime();

			String sql = "Delete from Timeq where Timestamp<" + twoMinsAgo+ ";";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
			
			
		
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					statement.close();
					connection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			} catch (NullPointerException n) {
				System.out.println("No more elements found");
			}
		}
		
		
	}
	

}
