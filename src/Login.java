import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Login extends Application {


	String user = "a";
	String pw = "a";
	String checkUser, checkPw;

	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("PMR");

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,50,50,50));
		

		//Adding HBox
		HBox hb = new HBox();
		hb.setPadding(new Insets(20,20,20,30));
		
		

		//Adding GridPane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20,20,20,20));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		//Implementing Nodes for GridPane
		Label lblUserName = new Label("Username");
		final TextField txtUserName = new TextField();
		Label lblPassword = new Label("Password");
		final PasswordField pf = new PasswordField();
		Button btnRegister = new Button("Register");
		Button btnLogin = new Button("  Login  ");
		final Label lblMessage = new Label();

		
		
		
		
		//Adding Nodes to GridPane layout
		gridPane.add(lblUserName, 0, 0);
		gridPane.add(txtUserName, 1, 0);
		gridPane.add(lblPassword, 0, 1);
		gridPane.add(pf, 1, 1);
		gridPane.add(btnLogin, 2, 1);
		gridPane.add(btnRegister, 2, 0);
		gridPane.add(lblMessage, 1, 2);


		//Reflection for gridPane
		//		Reflection r = new Reflection();
		//		r.setFraction(0.5f);
		//		gridPane.setEffect(r);

		//DropShadow effect 
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(5);
		dropShadow.setOffsetY(5);

		//Adding text and DropShadow effect to it
		Text text = new Text("      PMR");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
		text.setEffect(dropShadow);

		//Adding text to HBox
		hb.getChildren().add(text);

		//Add ID's to Nodes
		bp.setId("bp");
		gridPane.setId("root");
		btnLogin.setId("btnLogin");
		btnRegister.setId("btnRegister");
		text.setId("text");

		//Action for btnLogin
		btnLogin.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Connection connection = null;
				ResultSet resultSet = null;
				Statement statement = null;
				try{
					String url = "jdbc:sqlite:db/pmr.db";
					connection = DriverManager.getConnection(url);
					String sql = "Select * from User WHERE Username='" + txtUserName.getText() + "' AND Password='" + pf.getText() + "';";
					System.out.println(sql);
					//PreparedStatement preparedStatement = connection.prepareStatement(sql);
					//resultSet = preparedStatement.executeQuery(sql);
					statement = connection.createStatement();
					resultSet = statement.executeQuery(sql);
					if(resultSet.next()){
						main.currentUser = txtUserName.getText();
						lblMessage.setText("Congratulations!");
						lblMessage.setTextFill(Color.GREEN);
						Stage stage = new Stage();
						PMRStage pmrstage = new PMRStage();
						pmrstage.buildStage(stage);
						primaryStage.close();
					} else{
						lblMessage.setText("Incorrect user or password.");
						lblMessage.setTextFill(Color.RED);		
					}
					System.out.println("Connection successful");
				} catch (SQLException e){
					System.out.println(e.getMessage());
				} finally {
					try{
						if (connection != null){
							resultSet.close();
							statement.close();
							connection.close();
							txtUserName.setText("");
							pf.setText("");
						}
					} catch (SQLException ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		});

		
		
		
		
		
		
		
		//Action for btnRegister
		btnRegister.setOnAction(new EventHandler() {
			public void handle(Event event) {

				Stage stage = new Stage();
				RegisterStage pmrstage = new RegisterStage();
				pmrstage.buildStage(stage);
				primaryStage.close();					

				txtUserName.setText("");
				pf.setText("");
			}
		});

		//Add HBox and GridPane layout to BorderPane Layout
		bp.setTop(hb);
		bp.setCenter(gridPane);  
		

		//Adding BorderPane to the scene and loading CSS
		Scene scene = new Scene(bp);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("login.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.titleProperty().bind(
				scene.widthProperty().asString().
				concat(" : ").
				concat(scene.heightProperty().asString()));
		//primaryStage.setResizable(false);
		primaryStage.show();
	}
}