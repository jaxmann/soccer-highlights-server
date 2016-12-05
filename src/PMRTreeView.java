
import java.util.HashSet;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
 
public class PMRTreeView extends Application {
 
    private final Node rootIcon = 
        new ImageView(new Image(getClass().getResourceAsStream("/epl.png"))); //placeholder picture for now
    private final Image depIcon = 
        new Image(getClass().getResourceAsStream("/epl.png"));
   
    
    TreeItem<String> rootNode = new TreeItem<String>("MyCompany Human Resources", rootIcon);
 
    public static void main(String[] args) {
        Application.launch(args);
    }
 
    
    //for reference http://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
    @Override
    public void start(Stage stage) {
        rootNode.setExpanded(true);
                
        rootNode.getChildren().add(new TreeItem<String>("Bundesliga", new ImageView(depIcon)));
        rootNode.getChildren().add(new TreeItem<String>("EPL", new ImageView(depIcon)));
        rootNode.getChildren().add(new TreeItem<String>("La Liga", new ImageView(depIcon)));
        rootNode.getChildren().add(new TreeItem<String>("Ligue 1", new ImageView(depIcon)));
        rootNode.getChildren().add(new TreeItem<String>("Serie A", new ImageView(depIcon)));
        
        
     
        //dont fiddle with this, it somewhow actually works
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
                					team.getChildren().add(new TreeItem<String>(main.players.get(i).getFullName()));
                				}
                			} else {
                				team.getChildren().add(new TreeItem<String>(main.players.get(i).getFullName()));
                			}
                		}
            		} else {
            			league.getChildren().add(new TreeItem<String>(main.players.get(i).getFullTeamName()));
            			league.getChildren().get(0).getChildren().add(new TreeItem<String>(main.players.get(i).getFullName()));
            		}
            	}
        	}
        }
        
    
       
        		
//        for (Player player : main.players) {
//            TreeItem<String> empLeaf = new TreeItem<String>(player.getFullName());
//            
//            for (TreeItem<String> depNode : rootNode.getChildren()) {
//                if (depNode.getValue().contentEquals(player.getFullTeamName())){
//                    depNode.getChildren().add(empLeaf);
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                TreeItem<String> depNode = new TreeItem<String>(
//                    player.getFullTeamName(), 
//                    new ImageView(depIcon)
//                );
//                rootNode.getChildren().add(depNode);
//                depNode.getChildren().add(empLeaf);
//            }
//        }
 
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
 
        box.getChildren().add(treeView);
        stage.setScene(scene);
        stage.show();
    }
 
    private final class TextFieldTreeCellImpl extends TreeCell<String> {
 
        private TextField textField;
 
        public TextFieldTreeCellImpl() {
        }
 
        @Override
        public void startEdit() {
            super.startEdit();
 
            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
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