package de.unikoeln.chemie.nmr.ui.gui;

import java.io.File;
import java.io.FileInputStream;

import org.jcamp.spectrum.NMRSpectrum;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NMReDATAeditor extends Application {
	Label label;
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NMReData editor");
        Scene scene = new Scene(new VBox(), 500, 500);
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
        VBox root=new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        menuBar.getMenus().addAll(menuFile);
        label=new Label();
        label.setWrapText(true);
        root.getChildren().addAll(label);
        ((VBox)scene.getRoot()).getChildren().addAll(menuBar,root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	private void openFile(File file) {
		try{
			NmredataReader reader = new NmredataReader(new FileInputStream(file));
			NmreData data = reader.read();
			StringBuffer text=new StringBuffer();
			IMolecularFormula mfa = MolecularFormulaManipulator.getMolecularFormula(data.getMolecule());
	        text.append("The molecule in your file has formula "+MolecularFormulaManipulator.getString(mfa)+"\n");
	        text.append("Your file contains "+data.getSpectra().size()+" spectra\n");
			for(int i=0; i<data.getSpectra().size(); i++){
				if(data.getSpectra().get(i) instanceof NMRSpectrum)
					text.append("Spectrum "+i+" has "+((NMRSpectrum)data.getSpectra().get(i)).getPeakTable().length+" peaks\n");
			}
			label.setText(text.toString());
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("File successfully read");
			alert.setHeaderText("Success");
			alert.setContentText("File was read and seems to be in correct format");
			alert.showAndWait();
		}
		catch(Exception ex){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error reading file");
			alert.setHeaderText("Failure");
			alert.setContentText("File could not be read. Reason: "+ex.getMessage());
			alert.showAndWait();
		}		
	}
}