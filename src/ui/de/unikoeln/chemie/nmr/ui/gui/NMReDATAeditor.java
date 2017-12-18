package de.unikoeln.chemie.nmr.ui.gui;

import java.io.File;
import java.io.FileInputStream;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NMReDATAeditor extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NMReData editor");
        VBox root=new VBox();
        Scene scene = new Scene(root, 300, 250);
        MenuBar menuBar = new MenuBar();
        Menu menuFile=new Menu("File");
        MenuItem open = new MenuItem("Open File...");
        menuFile.getItems().addAll(open);
        open.setOnAction(new EventHandler<ActionEvent>(){
        	public void handle(ActionEvent t){
        		FileChooser chooser = new FileChooser();
        		chooser.setTitle("Open NMReDATA file");
        		File file = chooser.showOpenDialog(primaryStage);
        		if(file!=null){
        			openFile(file);
        		}
        	}
        });
        menuBar.getMenus().addAll(menuFile);
        root.getChildren().addAll(menuBar);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	private void openFile(File file) {
		try{
			NmredataReader reader = new NmredataReader(new FileInputStream(file));
			NmreData data = reader.read();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("File successfully read");
			alert.setContentText("File was read and seems to be in correct format");
			alert.showAndWait();
		}
		catch(Exception ex){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error reading file");
			alert.setContentText("File could not be read. Reason: "+ex.getMessage());
			alert.showAndWait();
		}		
	}
}