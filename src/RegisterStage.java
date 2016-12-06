import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterStage {
	
	public RegisterStage() {
		
	}

	public void buildStage(Stage primaryStage) {

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
		Label lblEmail = new Label("Email");
		final TextField txtEmail = new TextField();
		final PasswordField pf = new PasswordField();
		Button btnRegister = new Button("Create Account");
		final Label lblMessage = new Label();


		//Adding Nodes to GridPane layout
		gridPane.add(lblUserName, 0, 0);
		gridPane.add(txtUserName, 1, 0);
		gridPane.add(lblPassword, 0, 1);
		gridPane.add(lblEmail, 0, 2);
		gridPane.add(txtEmail, 1, 2);
		gridPane.add(pf, 1, 1);
		gridPane.add(btnRegister, 1, 3);
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
		Text text = new Text("    PMR");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
		text.setEffect(dropShadow);

		//Adding text to HBox
		hb.getChildren().add(text);

		//Add ID's to Nodes
		bp.setId("bp");
		gridPane.setId("root");
		btnRegister.setId("btnRegister");
		text.setId("text");

		//Action for btnLogin
//		btnLogin.setOnAction(new EventHandler() {
//			public void handle(Event event) {
//				checkUser = txtUserName.getText().toString();
//				checkPw = pf.getText().toString();
//				//sql lookup here
//				if(checkUser.equals(user) && checkPw.equals(pw)){
//					lblMessage.setText("Congratulations!");
//					lblMessage.setTextFill(Color.GREEN);
//					Stage stage = new Stage();
//					PMRStage pmrstage = new PMRStage();
//					pmrstage.buildStage(stage);
//					primaryStage.close();					
//				} else{
//					lblMessage.setText("Incorrect user or password.");
//					lblMessage.setTextFill(Color.RED);
//
//				}
//				txtUserName.setText("");
//				pf.setText("");
//			}
//		});
//
//		//Action for btnRegister
//		btnRegister.setOnAction(new EventHandler() {
//			public void handle(Event event) {
//				checkUser = txtUserName.getText().toString();
//				checkPw = pf.getText().toString();
//				//sql lookup here
//				if(checkUser.equals(user) && checkPw.equals(pw)){
//					lblMessage.setText("Congratulations!");
//					lblMessage.setTextFill(Color.GREEN);
//					Stage stage = new Stage();
//					PMRStage pmrstage = new PMRStage();
//					pmrstage.buildStage(stage);
//					primaryStage.close();					
//				} else{
//					lblMessage.setText("Incorrect user or password.");
//					lblMessage.setTextFill(Color.RED);
//
//				}
//				txtUserName.setText("");
//				pf.setText("");
//			}
//		});

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
		
		
		
//		VBox root = new VBox(5);
//
//		Text usertext = new Text();
//		usertext.setText("Username");
//		root.getChildren().add(usertext);
//		TextField username = new TextField();
//		String name1 = "user field";
//		username.setOnAction(e -> {
//			System.out.println("Action on "+name1+": text is "+username.getText());
//		});
//		root.getChildren().add(username);
//
//
//		Text passwordtext = new Text();
//		passwordtext.setText("Password");
//		root.getChildren().add(passwordtext);
//		TextField password = new TextField();
//		String name2 = "password";
//		password.setOnAction(e -> {
//			System.out.println("Action on "+name2+": text is "+password.getText());
//		});
//		root.getChildren().add(password);
//
//
//		Text useremail = new Text();
//		useremail.setText("Email");
//		root.getChildren().add(useremail);
//		TextField email = new TextField();
//		String name3 = "Text field ";
//		email.setOnAction(e -> {
//			System.out.println("Action on "+name3+": text is "+email.getText());
//		});
//		root.getChildren().add(email);

		
	}

}