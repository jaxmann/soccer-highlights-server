
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class PMRStage  {
	
	public static ArrayList<String> selectedKeywords = new ArrayList<String>();

	private final Node rootIcon = 
			new ImageView(new Image(getClass().getResourceAsStream("/epl.png"))); //placeholder picture for now
	private final Image depIcon = 
			new Image(getClass().getResourceAsStream("/epl.png"));


	public CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<String>("MyCompany Human Resources", rootIcon);
	
	public PMRStage() {
		
	}
	
	
	public void buildStage(Stage stage) {
		rootNode.setExpanded(true);

		rootNode.setIndependent(true);
		//these all have to be initialized independently 
		CheckBoxTreeItem<String> bundes = new CheckBoxTreeItem<String>("Bundesliga", new ImageView(depIcon));
		bundes.setIndependent(true);
		bundes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					System.out.println("The selected item is " + bundes.valueProperty().get());
					selectedKeywords.add(bundes.valueProperty().get());
				} else {
					System.out.println("Item deselected");
					selectedKeywords.remove(bundes.valueProperty().get());
				}
			}
		});
		rootNode.getChildren().add(bundes);
		
		CheckBoxTreeItem<String> epl = new CheckBoxTreeItem<String>("EPL", new ImageView(depIcon));
		epl.setIndependent(true);
		epl.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					System.out.println("The selected item is " + epl.valueProperty().get());
					selectedKeywords.add(epl.valueProperty().get());
				} else {
					System.out.println("Item deselected");
					selectedKeywords.remove(bundes.valueProperty().get());
				}
			}
		});
		rootNode.getChildren().add(epl);
		
		CheckBoxTreeItem<String> LaLiga = new CheckBoxTreeItem<String>("La Liga", new ImageView(depIcon));
		LaLiga.setIndependent(true);
		LaLiga.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					System.out.println("The selected item is " + LaLiga.valueProperty().get());
					selectedKeywords.add(LaLiga.valueProperty().get());
				} else {
					System.out.println("Item deselected");
					selectedKeywords.remove(LaLiga.valueProperty().get());
				}
			}
		});
		rootNode.getChildren().add(LaLiga);
		
		CheckBoxTreeItem<String> l1 = new CheckBoxTreeItem<String>("Ligue 1", new ImageView(depIcon));
		l1.setIndependent(true);
		l1.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					System.out.println("The selected item is " + l1.valueProperty().get());
					selectedKeywords.add(l1.valueProperty().get());
				} else {
					System.out.println("Item deselected");
					selectedKeywords.remove(l1.valueProperty().get());
				}
			}
		});
		rootNode.getChildren().add(l1);
		
		CheckBoxTreeItem<String> serieA = new CheckBoxTreeItem<String>("Serie A", new ImageView(depIcon));
		serieA.setIndependent(true);
		serieA.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					System.out.println("The selected item is " + serieA.valueProperty().get());
					selectedKeywords.add(serieA.valueProperty().get());
				} else {
					System.out.println("Item deselected");
					selectedKeywords.remove(serieA.valueProperty().get());
				}
			}
		});
		rootNode.getChildren().add(serieA);
		


		//dont fiddle with this, it somehow actually works
		for (int i=0; i<main.players.size(); i++) {
			for (TreeItem<String> league : rootNode.getChildren()) {
				if (league.getValue().contentEquals(main.players.get(i).getLeagueName())) {
					if (league.getChildren().size() != 0) {
						for (TreeItem<String> team : league.getChildren()) {
							if (team.getChildren().size() != 0) {
								HashSet<String> hs = new HashSet<String>();
								for (TreeItem<String> player : team.getChildren()) {
									hs.add(player.getValue());
								}
								if (!hs.contains(main.players.get(i).getFullName())) {
									CheckBoxTreeItem<String> a = new CheckBoxTreeItem<String>(main.players.get(i).getFullName());
									a.setIndependent(true);
									a.selectedProperty().addListener(new ChangeListener<Boolean>() {
										public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
											if (newValue) {
												System.out.println("The selected item is " + a.valueProperty().get());
												selectedKeywords.add(a.valueProperty().get());
											} else {
												System.out.println("Item deselected");
												selectedKeywords.remove(a.valueProperty().get());
											}
										}
									});
									team.getChildren().add(a);
								}
							} else {	
								CheckBoxTreeItem<String> b = new CheckBoxTreeItem<String>(main.players.get(i).getFullName());
								b.setIndependent(true);
								b.selectedProperty().addListener(new ChangeListener<Boolean>() {
									public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
										if (newValue) {
											System.out.println("The selected item is " + b.valueProperty().get());
											selectedKeywords.add(b.valueProperty().get());
										} else {
											System.out.println("Item deselected");
											selectedKeywords.remove(b.valueProperty().get());
										}
									}
								});
								team.getChildren().add(b);
								
							}
						}
					} else {
						CheckBoxTreeItem<String> c = new CheckBoxTreeItem<String>(main.players.get(i).getFullTeamName());
						c.setIndependent(true);
						c.selectedProperty().addListener(new ChangeListener<Boolean>() {
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
								if (newValue) {
									System.out.println("The selected item is " + c.valueProperty().get());
									selectedKeywords.add(c.valueProperty().get());
								} else {
									System.out.println("Item deselected");
									selectedKeywords.remove(c.valueProperty().get());
								}
							}
						});
						league.getChildren().add(c);
						
						CheckBoxTreeItem<String> d = new CheckBoxTreeItem<String>(main.players.get(i).getFullName());
						d.setIndependent(true);
						d.selectedProperty().addListener(new ChangeListener<Boolean>() {
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
								if (newValue) {
									System.out.println("The selected item is " + d.valueProperty().get());
									selectedKeywords.add(d.valueProperty().get());
								} else {
									System.out.println("Item deselected");
									selectedKeywords.remove(d.valueProperty().get());
								}
							}
						});
						league.getChildren().get(0).getChildren().add(d);
					}
				}
			}
		}

		

		stage.setTitle("Player Selector Menu");
		VBox box = new VBox();
		final Scene scene = new Scene(box, 400, 300);
		scene.setFill(Color.LIGHTGRAY);

		TreeView<String> treeView = new TreeView<String>(rootNode);

		treeView.setEditable(true);
		treeView.setShowRoot(false);
		treeView.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				return new TextFieldTreeCellImpl();
			}
		});
		treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

		box.getChildren().add(treeView);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  //Insert selectedKeywords into keywords field of user table
	        	  String keywords = "";
	        	  for(int i = 0; i < selectedKeywords.size(); i++){
	        		  if(i == selectedKeywords.size() - 1){
	        			  keywords += selectedKeywords.get(i);
	        		  } else{
	        		  keywords += selectedKeywords.get(i) + "$";
	        		  }
	        	  }
	        	  Connection connection = null;
					try{
						String url = "jdbc:sqlite:db/pmr.db";
						connection = DriverManager.getConnection(url);
						String sql = "UPDATE User set Keywords='" + keywords + "' WHERE Username='" + main.currentUser + "';";
						PreparedStatement preparedStatement = connection.prepareStatement(sql);
						preparedStatement.executeUpdate();
						System.out.println("Connection successful");
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
	        	  System.out.println(selectedKeywords);
	              System.out.println("Stage is closing");
	          }
	      }); 
		
		
		
	}
	

	
	
	
	

	private final class TextFieldTreeCellImpl extends TreeCell<String> {

		private TextField textField;

		public TextFieldTreeCellImpl() {
		}



		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(textField.getText());
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}

}