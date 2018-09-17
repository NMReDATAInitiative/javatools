package de.unikoeln.chemie.nmr.ui.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jcamp.spectrum.NMRSpectrum;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.NmreData.NmredataVersion;
import de.unikoeln.chemie.nmr.io.LSDWriter;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import de.unikoeln.chemie.nmr.io.NmredataWriter;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class NMReDATAeditor extends Application {
	Label label;
	NmreData data;
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(final Stage primaryStage) {
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
        MenuItem saveas = new MenuItem("Save as...");
        menuFile.getItems().addAll(saveas);
        saveas.setOnAction(new EventHandler<ActionEvent>(){
        	public void handle(ActionEvent t){
        		if(data==null){
        			Alert alert = new Alert(AlertType.INFORMATION);
        			alert.setTitle("No file open");
        			alert.setHeaderText("No file open");
        			alert.setContentText("There is no file open to save");
        			alert.showAndWait();
        			return;
        		}
        		FileChooser chooser = new FileChooser();
        		FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("NMReDATA 1.0 file (*.nmredata.sdf)", "*.nmredata.sdf");
        		FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("NMReDATA 1.1 file (*.nmredata.sdf)", "*.nmredata.sdf");
        		FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("LSD file (*.lsd)", "*.lsd");
        		chooser.getExtensionFilters().addAll(extFilter2, extFilter1, extFilter3);
        		chooser.setTitle("Save spectrum file");
        		File file = chooser.showSaveDialog(primaryStage);
        		if(file!=null){
        			try{
        				saveas(file, chooser.getSelectedExtensionFilter());
        			}catch(Exception ex){
            			Alert alert = new Alert(AlertType.ERROR);
            			alert.setTitle("Error writing file");
            			alert.setHeaderText("Error writing file");
            			alert.setContentText(ex.getMessage());
            			alert.showAndWait();
        				
        			}
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

	protected void saveas(File file, ExtensionFilter selectedExtensionFilter) throws IOException, CloneNotSupportedException, CDKException {
		if(selectedExtensionFilter.getExtensions().get(0).equals("*.lsd")){
			if(!file.getName().endsWith(".lsd"))
				file=new File(file.getPath()+".lsd");
	        FileOutputStream pw = new FileOutputStream(file);
	        LSDWriter lsdwrtier=new LSDWriter(pw);
	        lsdwrtier.write(data);
	        lsdwrtier.close();
		}else if(selectedExtensionFilter.getDescription().equals("NMReDATA 1.0 file (*.nmredata.sdf)")){
			if(!file.getName().endsWith(".nmredata.sdf"))
				file=new File(file.getPath()+".nmredata.sdf");
	        FileOutputStream fos=new FileOutputStream(file);
	        NmredataWriter writer=new NmredataWriter(fos);
	        writer.write(data, NmredataVersion.ONE);
	        writer.close();
		}else{
			if(!file.getName().endsWith(".nmredata.sdf"))
				file=new File(file.getPath()+".nmredata.sdf");
	        FileOutputStream fos=new FileOutputStream(file);
	        NmredataWriter writer=new NmredataWriter(fos);
	        writer.write(data, NmredataVersion.ONEPOINTONE);
	        writer.close();
		}
	}

	private void openFile(File file) {
		try{
			NmredataReader reader = new NmredataReader(new FileInputStream(file));
			data = reader.read();
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