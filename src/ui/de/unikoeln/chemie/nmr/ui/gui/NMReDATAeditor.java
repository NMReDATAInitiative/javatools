package de.unikoeln.chemie.nmr.ui.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.jcamp.spectrum.assignments.AtomReference;
import org.jcamp.spectrum.notes.Note;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.CouplingGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;
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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class NMReDATAeditor extends Application {
	SplitPane splitPane;
	ImageView imageView;
	TabPane tabPane;
	NmreData data;
	ObservableList<Peak2D> selection2d=null;
	ObservableList<Peak1D> selection1d=null;
	List<Assignment> assignments;
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("NMReData editor");
        Scene scene = new Scene(new VBox(), 800, 800);
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
        VBox vbox=new VBox(imageView);
        splitPane.getItems().add(vbox);
        vbox.heightProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
            	try {
            		if(data!=null)
            			updateMolImage();
				} catch (UnsupportedEncodingException | SVGGraphics2DIOException | TranscoderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        vbox.widthProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
            	try {
            		if(data!=null)
            			updateMolImage();
				} catch (UnsupportedEncodingException | SVGGraphics2DIOException | TranscoderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        tabPane=new TabPane();
        splitPane.getItems().add(new VBox(tabPane));
        splitPane.prefWidthProperty().bind(scene.widthProperty());
        splitPane.prefHeightProperty().bind(scene.heightProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());
        tabPane.prefHeightProperty().bind(scene.heightProperty());
        vbox.prefWidthProperty().bind(scene.widthProperty().multiply(splitPane.getDividers().get(0).positionProperty().subtract(30)));
        imageView.fitWidthProperty().bind(scene.widthProperty().multiply(splitPane.getDividers().get(0).positionProperty().subtract(30)));
        vbox.prefHeightProperty().bind(scene.heightProperty());
        vbox.setMinWidth(10);
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
	        assignments=new ArrayList<>();
			for(int i=0; i<data.getSpectra().size(); i++){
				if(data.getSpectra().get(i) instanceof NMRSpectrum){
					if(((NMRSpectrum)data.getSpectra().get(i)).getAssignments()!=null)
						assignments.addAll(Arrays.asList(((NMRSpectrum)data.getSpectra().get(i)).getAssignments()));
				}
			}
			for(int i=0; i<data.getSpectra().size(); i++){
				SplitPane splitPaneSpectrum=new SplitPane();
		        splitPaneSpectrum.prefWidthProperty().bind(tabPane.widthProperty());
		        splitPaneSpectrum.prefHeightProperty().bind(tabPane.heightProperty());
		        Tab tab;
		        if(data.getSpectra().get(i) instanceof NMR2DSpectrum){
			        NoteDescriptor noteDescriptor=new NoteDescriptor("CorType");
			        if(data.getSpectra().get(i).getNotes(noteDescriptor)==null)
			        	tab=new Tab("2D - Unknown Type");
			        else
			        	tab=new Tab("2D "+((Note)data.getSpectra().get(i).getNotes(noteDescriptor).get(0)).getValue());
			        TableView<Peak2D> table=new TableView<Peak2D>();
			        TableColumn<Peak2D, String> firstAtomCol = new TableColumn<Peak2D, String>("Assignment");
			        firstAtomCol.setCellValueFactory(new PropertyValueFactory<>("atoms1"));
			        TableColumn<Peak2D, Double> firstShiftCol = new TableColumn<Peak2D, Double>("Shift");
			        firstShiftCol.setCellValueFactory(new PropertyValueFactory<>("firstShift"));
			        TableColumn<Peak2D, String> secondAtomCol = new TableColumn<Peak2D, String>("Assignment");
			        secondAtomCol.setCellValueFactory(new PropertyValueFactory<>("atoms2"));
			        TableColumn<Peak2D, Double> secondShiftCol = new TableColumn<Peak2D, Double>("Shift");
			        secondShiftCol.setCellValueFactory(new PropertyValueFactory<>("secondShift"));
			        table.getColumns().add(firstShiftCol);
			        table.getColumns().add(firstAtomCol);
			        table.getColumns().add(secondShiftCol);
			        table.getColumns().add(secondAtomCol);
			        ArrayList<Peak2D> al=new ArrayList<Peak2D>();
			        for(Peak2D peak : ((NMR2DSpectrum)data.getSpectra().get(i)).getPeakTable()){
			        	((Peak2D)peak).assignments=assignments;
			        	al.add(peak);
			        }
			        table.setItems(FXCollections.observableArrayList(al));
			        splitPaneSpectrum.getItems().add(table);
			        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Peak2D> c)  -> {
			            selection2d=table.getSelectionModel().getSelectedItems();
			            selection1d=null;
			            try {
							updateMolImage();
						} catch (UnsupportedEncodingException | SVGGraphics2DIOException | TranscoderException e) {
							// TODO Auto-generated catch bloc(((AtomReference)atom).getAtomNumber()k
							e.printStackTrace();
						}
			        });
			        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		        }else{
					tab = new Tab("1D "+((NMRSpectrum)data.getSpectra().get(i)).getNucleus());
			        TableView<Peak1D> table=new TableView<Peak1D>();
			        TableColumn<Peak1D, Double> shiftCol = new TableColumn<Peak1D, Double>("Shift");
			        shiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));
			        TableColumn<Peak1D, Double> intensityCol = new TableColumn<Peak1D, Double>("Intensity");
			        intensityCol.setCellValueFactory(new PropertyValueFactory<>("height"));
			        TableColumn<Peak1D, String> atomCol = new TableColumn<Peak1D, String>("Assignment");
			        atomCol.setCellValueFactory(new PropertyValueFactory<>("atoms"));
			        table.getColumns().add(shiftCol);
			        table.getColumns().add(intensityCol);
			        table.getColumns().add(atomCol);
			        ArrayList<Peak1D> al=new ArrayList<Peak1D>();
			        for(org.jcamp.spectrum.Peak1D peak : ((NMRSpectrum)data.getSpectra().get(i)).getPeakTable()){
			        	((Peak1D)peak).assignments=((NMRSpectrum)data.getSpectra().get(i)).getAssignments();
			        	al.add((Peak1D)peak);
			        }
			        table.setItems(FXCollections.observableArrayList(al));
			        splitPaneSpectrum.getItems().add(table);
			        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Peak1D> c) -> {
			            selection1d=table.getSelectionModel().getSelectedItems();
			            selection2d=null;
			            try {
							updateMolImage();
						} catch (UnsupportedEncodingException | SVGGraphics2DIOException | TranscoderException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        });
			        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				}
		        GridPane gridPane = new GridPane();
		        gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		        gridPane.setVgap(5); 
		        gridPane.setHgap(5);       
		        gridPane.setAlignment(Pos.CENTER); 
		        Text text1 = new Text("Frequency");
		        TextField text2;
		        if(data.getSpectra().get(i) instanceof NMR2DSpectrum)
		        	text2 = new TextField(Double.toString(((NMR2DSpectrum)data.getSpectra().get(i)).getYFrequency()));
		        else
		        	text2 = new TextField(Double.toString(((NMRSpectrum)data.getSpectra().get(i)).getFrequency()));
		        text2.setMaxWidth(100);
		        gridPane.add(text1, 0, 0); 
		        gridPane.add(text2, 1, 0);
		        int k=1;
		        for(Object descriptor : ((Spectrum)data.getSpectra().get(i)).getNotes()){
					Note note = (Note)descriptor;
			        text1 = new Text(note.getDescriptor().getKey());
			        text2 = new TextField((String)note.getValue());
			        text2.setMaxWidth(100);
			        gridPane.add(text1, 0, k); 
			        gridPane.add(text2, 1, k);
			        k++;
		        }
		        splitPaneSpectrum.getItems().add(gridPane);
		        tab.setContent(splitPaneSpectrum);
				tabPane.getTabs().add(tab);
			}
			updateMolImage();
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

	private void updateMolImage() throws UnsupportedEncodingException, SVGGraphics2DIOException, TranscoderException {
		//we generate mol image
		for(IAtom atom : data.getMolecule().atoms()){
			String label = Integer.toString(1 + data.getMolecule().getAtomNumber(atom));
		    atom.setProperty(StandardGenerator.ANNOTATION_LABEL, label);
		}
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    Font font = new Font("Verdana", Font.PLAIN, 18);
	    List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
	    generators.add(new StandardGenerator(font));
	    generators.add(new BasicSceneGenerator());
	    generators.add(new CouplingGenerator());
	    AtomContainerRenderer renderer = new AtomContainerRenderer(generators,new AWTFontManager());
	    RendererModel r2dm = renderer.getRenderer2DModel();
	    r2dm.registerParameters(new AtomNumberGenerator());
	    r2dm.set(BasicSceneGenerator.BackgroundColor.class,Color.WHITE);
	    r2dm.set(RendererModel.SelectionColor.class, Color.BLUE);
	    if(selection1d!=null){
	    	IAtomContainer hightlightcontainer=data.getMolecule().getBuilder().newAtomContainer();
	    	for(Peak1D peak : selection1d){
	    		for(Assignment assignment : assignments){
	    			if(assignment.getPattern().getPosition()[0]==peak.getShift()){
	    				for(IAssignmentTarget target : assignment.getTargets()){
	    					hightlightcontainer.addAtom(data.getMolecule().getAtom((((AtomReference)target).getAtomNumber())));
	    				}
	    			}
	    		}
	    	}
	    	Selection selection=new Selection();
	    	selection.ac=hightlightcontainer;
	    	r2dm.setSelection(selection);
	    }else{
	    	r2dm.setSelection(null);
	    }
	    if(selection2d!=null){
	    	Map<Color,List<IBond>> couplings=new HashMap<>();
	        couplings.put(Color.BLUE, new ArrayList<IBond>());
	        IAtomContainer hightlightcontainer=data.getMolecule().getBuilder().newAtomContainer();
	    	for(Peak2D peak : selection2d){
    			IBond bond=data.getMolecule().getBuilder().newBond();
	    		for(Assignment assignment : assignments){
	    			if(assignment.getPattern().getPosition()[0]==peak.getFirstShift() || assignment.getPattern().getPosition()[1]==peak.getFirstShift()){
	    				for(IAssignmentTarget target : assignment.getTargets()){
	    					bond.setAtom(data.getMolecule().getAtom((((AtomReference)target).getAtomNumber())),0);
	    				}
	    			}
	    			if(assignment.getPattern().getPosition()[0]==peak.getSecondShift() || assignment.getPattern().getPosition()[1]==peak.getSecondShift()){
	    				for(IAssignmentTarget target : assignment.getTargets()){
	    					bond.setAtom(data.getMolecule().getAtom((((AtomReference)target).getAtomNumber())),1);
	    				}
	    			}
	    		}
	    		if(bond.getAtom(0)==bond.getAtom(1)){
	    			hightlightcontainer.addAtom(bond.getAtom(0));
	    		}else{
	    			couplings.get(Color.BLUE).add(bond);
	    		}
	    	}
	    	if(couplings.get(Color.BLUE).size()>0)
	    		data.getMolecule().setProperty("couplings", couplings);
	    	else
	    		data.getMolecule().setProperty("couplings", null);
	    	if(hightlightcontainer.getAtomCount()>0){
		    	Selection selection=new Selection();
		    	selection.ac=hightlightcontainer;
		    	r2dm.setSelection(selection);	    		
	    	}else{
	    		r2dm.setSelection(new Selection());
	    	}
	    }else{
	    	data.getMolecule().setProperty("couplings", null);
	    }
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
	    //and set it on the image view
	    BufferedImageTranscoder trans = new BufferedImageTranscoder();
	    TranscoderInput transIn = new TranscoderInput(new StringReader(sb.toString()));
	    trans.transcode(transIn, null);
	    Image img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
	    imageView.setImage(img);
	}
}