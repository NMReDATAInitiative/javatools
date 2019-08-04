package de.unikoeln.chemie.nmr.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Peak;
import org.jcamp.spectrum.notes.Note;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.CouplingGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.unikoeln.chemie.nmr.data.NMR2DSpectrum;
import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.NmreData.NmredataVersion;
import de.unikoeln.chemie.nmr.data.Peak1D;
import de.unikoeln.chemie.nmr.data.Peak2D;
import de.unikoeln.chemie.nmr.io.LSDWriter;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import de.unikoeln.chemie.nmr.io.NmredataWriter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class NMReDATAeditor extends Application {
	SplitPane splitPane;
	ImageView imageView;
	TabPane tabPane;
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
        				System.out.println(ex.getMessage());
            			Alert alert = new Alert(AlertType.ERROR);
            			alert.setTitle("Error writing file");
            			alert.setHeaderText("Error writing file");
            			alert.setContentText(ex.getMessage());
            			alert.showAndWait();
        				
        			}
        		}
        	}
        });
        menuBar.getMenus().addAll(menuFile);
        imageView=new ImageView();
        splitPane = new SplitPane(); 
        splitPane.getItems().add(new VBox(imageView));
        tabPane=new TabPane();
        splitPane.getItems().add(new VBox(tabPane));
        splitPane.prefWidthProperty().bind(scene.widthProperty());
        splitPane.prefHeightProperty().bind(scene.heightProperty());
        ((VBox)scene.getRoot()).getChildren().addAll(menuBar,splitPane);
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
				if(data.getSpectra().get(i) instanceof NMR2DSpectrum){
			        NoteDescriptor noteDescriptor=new NoteDescriptor("CorType");
			        Tab tab=new Tab("2D "+((Note)data.getSpectra().get(i).getNotes(noteDescriptor).get(0)).getValue());
			        TableView<Peak2D> table=new TableView<Peak2D>();
			        //TableColumn<Peak2D, Integer> firstAtomCol = new TableColumn<Peak2D, Integer>("Atom Number");
			        TableColumn<Peak2D, Double> firstShiftCol = new TableColumn<Peak2D, Double>("Shift");
			        firstShiftCol.setCellValueFactory(new PropertyValueFactory<>("firstShift"));
			        //TableColumn<Peak2D, Integer> secondAtomCol = new TableColumn<Peak2D, Integer>("Atom Number");
			        TableColumn<Peak2D, Double> secondShiftCol = new TableColumn<Peak2D, Double>("Shift");
			        secondShiftCol.setCellValueFactory(new PropertyValueFactory<>("secondShift"));
			        table.getColumns().add(firstShiftCol);
			        table.getColumns().add(secondShiftCol);
			        ArrayList<Peak2D> al=new ArrayList<Peak2D>();
			        for(Peak2D peak : ((NMR2DSpectrum)data.getSpectra().get(i)).getPeakTable())
			        	al.add(peak);
			        table.setItems(FXCollections.observableArrayList(al));
			        tab.setContent(table);
					tabPane.getTabs().add(tab);
				}else{
					Tab tab = new Tab("1D "+((NMRSpectrum)data.getSpectra().get(i)).getNucleus());
			        TableView<Peak1D> table=new TableView<Peak1D>();
			        TableColumn<Peak1D, Double> shiftCol = new TableColumn<Peak1D, Double>("Shift");
			        shiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));
			        TableColumn<Peak1D, Double> intensityCol = new TableColumn<Peak1D, Double>("Intensity");
			        intensityCol.setCellValueFactory(new PropertyValueFactory<>("height"));
			        table.getColumns().add(shiftCol);
			        table.getColumns().add(intensityCol);
			        ArrayList<Peak1D> al=new ArrayList<Peak1D>();
			        for(org.jcamp.spectrum.Peak1D peak : ((NMRSpectrum)data.getSpectra().get(i)).getPeakTable())
			        	al.add((Peak1D)peak);
			        table.setItems(FXCollections.observableArrayList(al));
			        tab.setContent(table);
					tabPane.getTabs().add(tab);
				}
			}
			//we generate mol image
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
		    generators.add(new BasicBondGenerator());
		    generators.add(new BasicAtomGenerator());
		    generators.add(new CouplingGenerator());
		    generators.add(new BasicSceneGenerator());
		    AtomContainerRenderer renderer = new AtomContainerRenderer(generators,new AWTFontManager());
		    RendererModel r2dm = renderer.getRenderer2DModel();
		    r2dm.registerParameters(new AtomNumberGenerator());
		    r2dm.set(BasicSceneGenerator.BackgroundColor.class,Color.WHITE);
		    
		    Rectangle drawArea = new Rectangle((int)((VBox)splitPane.getItems().get(0)).getWidth(),(int)((VBox)splitPane.getItems().get(0)).getHeight());
		    renderer.setup(data.getMolecule(), drawArea);
		    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		    Document document = domImpl.createDocument(null, "svg", null);
		    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		    svgGenerator.setBackground(Color.WHITE);
		    svgGenerator.setColor(Color.WHITE);
		    svgGenerator.fill(new Rectangle(0, 0, drawArea.width, drawArea.height));
		    renderer.paint(data.getMolecule(), new AWTDrawVisitor(svgGenerator), drawArea, false);
		    boolean useCSS = false;
		    baos = new ByteArrayOutputStream();
		    Writer outwriter = new OutputStreamWriter(baos, "UTF-8");
		    StringBuffer sb = new StringBuffer();
		    svgGenerator.stream(outwriter, useCSS);
		    StringTokenizer tokenizer = new StringTokenizer(baos.toString(), "\n");
		    while (tokenizer.hasMoreTokens()) {
		      String name = tokenizer.nextToken();
		      if (name.length() > 4 && name.substring(0, 5).equals("<svg ")) {
		        sb.append(name.substring(0, name.length())).append(" width=\"" + drawArea.width + "\" height=\"" + drawArea.height + "\"" + "\n\r");
		      } else {
		        sb.append(name + "\n\r");
		      }
		    }
		    BufferedImageTranscoder trans = new BufferedImageTranscoder();

		 // file may be an InputStream.
		 // Consult Batik's documentation for more possibilities!
		 TranscoderInput transIn = new TranscoderInput(new StringReader(sb.toString()));

		 trans.transcode(transIn, null);
		 Image img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
		    
			imageView.setImage(img);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("File successfully read");
			alert.setHeaderText(text.toString());
			alert.setContentText("File was read and seems to be in correct format");
			alert.showAndWait();
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
			Alert alert = new Alert(AlertType.ERROR);
			alert.getDialogPane().setMinWidth(300);
			alert.setTitle("Error reading file");
			alert.setHeaderText("Failure");
			alert.setContentText("File could not be read. Reason: "+ex.getMessage());
			alert.showAndWait();
		}		
	}
}