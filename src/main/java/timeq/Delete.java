package timeq;
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


//delete database records - run this job at midnight
public class Delete {

	//MAKE THIS FILE INTO A JAR
	public static void main(String[] args) {
		
		delete(); //truncate table

	}
	
	
	public static void delete() {
		Connection connection = null;
		PreparedStatement statement = null;
				
		try{
			String url = "jdbc:sqlite:../server/db/timeq.db";
			connection = DriverManager.getConnection(url);
			System.nanoTime();

			String sql = "DELETE FROM Timeq;";
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
