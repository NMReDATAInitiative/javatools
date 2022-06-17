package de.unikoeln.chemie.nmr.ui.cl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jcamp.parser.JCAMPException;
import org.jcamp.parser.JCAMPReader;
import org.jcamp.parser.JCAMPWriter;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.Spectrum;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.openscience.cdk.exception.CDKException;

import de.unikoeln.chemie.nmr.data.NmreData;
import de.unikoeln.chemie.nmr.data.NmreData.NmredataVersion;
import de.unikoeln.chemie.nmr.io.LSDWriter;
import de.unikoeln.chemie.nmr.io.NmreDataException;
import de.unikoeln.chemie.nmr.io.NmredataReader;
import de.unikoeln.chemie.nmr.io.NmredataWriter;

public class Convert {

	public static void main(String[] args) throws IOException, NmreDataException, JCAMPException, CDKException, CloneNotSupportedException {
		if(args.length!=2){
			System.out.println("Please provide two file names");
			return;
		}			
		String input=args[0];
		String output=args[1];
		NmreData data=null;
		if(input.endsWith("sdf")) {
	        InputStream ins = new FileInputStream(input);
	        NmredataReader reader = new NmredataReader(ins);
	        data = reader.read();
		}else if(input.endsWith("jdx")) {
	        JCAMPReader reader = JCAMPReader.getInstance();
	        Path filePath = Path.of(input);
	        String content = Files.readString(filePath);
	        Spectrum spectrum = reader.createSpectrum(content);
	        data=new NmreData();
	        data.setVersion(NmredataVersion.ONE);
	        data.addSpectrum(spectrum);
	        NoteDescriptor noteDescriptor=new NoteDescriptor("Spectrum_Location");
	        spectrum.setNote(noteDescriptor, "unknwon");
		}
		if(output.endsWith("jdx")) {
	        JCAMPWriter jwriter=JCAMPWriter.getInstance();
	        String output1=output.substring(0,output.lastIndexOf('.'));
	        String output2=output.substring(output.lastIndexOf('.')+1);
	        int i=0;
	        for(Spectrum spectrum : data.getSpectra()) {
	        	if(spectrum instanceof NMRSpectrum) {
			        FileWriter writer = new FileWriter(output1+"."+i+"."+output2);
			        writer.write(jwriter.toJCAMP(spectrum));
			        writer.close();
			        i++;
	        	}
	        }
		}
		if(output.endsWith("sdf")) {
	        FileOutputStream fos=new FileOutputStream(output);
	        NmredataWriter writer=new NmredataWriter(fos);
	        writer.write(data, NmredataVersion.ONE);
	        writer.close();
		}
		if(output.endsWith("lsd")) {
		    FileOutputStream pw = new FileOutputStream(output);
	        LSDWriter lsdwrtier=new LSDWriter(pw);
	        lsdwrtier.write(data);
	        lsdwrtier.close();
		
		}
	}

}
