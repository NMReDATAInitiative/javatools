package de.unikoeln.chemie.nmr.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jcamp.parser.JCAMPException;
import org.jcamp.parser.JCAMPReader;
import org.jcamp.spectrum.ArrayData;
import org.jcamp.spectrum.Assignment;
import org.jcamp.spectrum.IAssignmentTarget;
import org.jcamp.spectrum.IDataArray1D;
import org.jcamp.spectrum.IOrderedDataArray1D;
import org.jcamp.spectrum.Multiplicity;
import org.jcamp.spectrum.NMR2DSpectrum;
import org.jcamp.spectrum.NMRSpectrum;
import org.jcamp.spectrum.OrderedArrayData;
import org.jcamp.spectrum.Pattern;
import org.jcamp.spectrum.Peak;
import org.jcamp.spectrum.Peak1D;
import org.jcamp.spectrum.assignments.AtomReference;
import org.jcamp.spectrum.notes.NoteDescriptor;
import org.jcamp.units.CommonUnit;
import org.jcamp.units.Unit;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import de.unikoeln.chemie.nmr.data.NmreData;

public class NmredataReader {
	BufferedReader input = null;
	
	Map<String,String> spectra1d=new HashMap<String,String>();
	Map<String,String> spectra2d=new HashMap<String,String>();
	Map<String,Peak> signals=new HashMap<String,Peak>();
	Map<String,IAssignmentTarget[]> assignments=new HashMap<String,IAssignmentTarget[]>();
	String lineseparator="\n\r";
	
	public NmredataReader(Reader in){
        input = new BufferedReader(in);
	}
	
	public NmredataReader(InputStream in){
		this(new InputStreamReader(in));
	}
	
	public NmreData read() throws IOException, NmreDataException, JCAMPException {
		NmreData data=new NmreData();
		IteratingSDFReader mdlreader=new IteratingSDFReader(input, DefaultChemObjectBuilder.getInstance());
		IAtomContainer ac = mdlreader.next();
		data.setMolecule(ac);
		mdlreader.close();
		String signalblock=null;
		for(Object key : ac.getProperties().keySet()){
			if(ac.getProperties().get(key)!=null){
				String property=((String)ac.getProperties().get(key)).trim();
				if(property.endsWith("\\"))
					property=property.substring(0, property.length()-1).trim();
				if(((String)key).startsWith("NMREDATA_1D")){
					spectra1d.put(((String)key).substring(9),property);
				}else if(((String)key).equals("NMREDATA_ASSIGNMENT")){
					signalblock=property;
				}else if(((String)key).startsWith("NMREDATA_2D")){
					spectra2d.put(((String)key).substring(9),property);
				}else if(((String)key).equals("NMREDATA_VERSION")){
					if(!property.equals("1.0") && !property.equals("1.1"))
						throw new NmreDataException("Currently 1.0 and 1.1 are the only supported NMReDATA versions");
					data.setVersion(property);
					if(property.equals("1.1"))
						lineseparator="\\";
				}else if(((String)key).startsWith("NMREDATA_LEVEL")){
					int level=Integer.parseInt(property);
					if(level<0 || level>3)
						throw new NmreDataException("Level must be 0, 1, 2, or 3");
				}
			}
		}
		if(signalblock!=null)
			analyzeSignals(data, signalblock);
		else
			throw new NmreDataException("There is no NMREDATA_ASSIGNMENT block in this file - required");
		analyzeSpectra(data);
		return data;
	}
	

	private void analyzeSignals(NmreData data, String signalblock) throws NmreDataException {
		StringTokenizer st=new StringTokenizer(signalblock,lineseparator);
		while(st.hasMoreTokens()){
			String line = st.nextToken().trim();
			StringTokenizer st2 = new StringTokenizer(line,",");
			String label=st2.nextToken();
			double shift = Double.parseDouble(st2.nextToken().trim());
			Peak peak=new Peak1D(shift,0);
			List<AtomReference> atoms = new ArrayList<AtomReference>();
			while(st2.hasMoreTokens()){
				String atom = st2.nextToken();
				if(atom.indexOf("H")>-1){
					int atomid=Integer.parseInt(atom.trim().substring(1))-1;
					if(atomid>=data.getMolecule().getAtomCount())
						throw new NmreDataException("Atom "+atomid+" specified in MREDATA_ASSIGNMENT block, but only "+data.getMolecule().getAtomCount()+" atoms are in Molecule");
                    for(int k=0;k<data.getMolecule().getConnectedAtomsCount(data.getMolecule().getAtom(atomid));k++){
                        if(data.getMolecule().getConnectedAtomsList(data.getMolecule().getAtom(atomid)).get(k).getSymbol().equals("H")){
                        	atomid=data.getMolecule().getAtomNumber(data.getMolecule().getConnectedAtomsList(data.getMolecule().getAtom(atomid)).get(k));
                        	break;
                        }
                    }
					atoms.add(new AtomReference(null, atomid));
				}else{
					int atomid=Integer.parseInt(atom.trim())-1;
					atoms.add(new AtomReference(null, atomid));
				}
				IAssignmentTarget[] assigns = new IAssignmentTarget[atoms.size()];
				for (int i=0;i<atoms.size();i++)
					assigns[i] = (IAssignmentTarget) atoms.get(i);
				if(atoms.size()>0)
					assignments.put(label, assigns);
			}
			signals.put(label, peak);
		}
	}

	private void analyzeSpectra(NmreData data) throws NmreDataException, JCAMPException {
		for(String spectrum : spectra1d.keySet()){
			String nucleus = spectrum.substring(spectrum.indexOf("_")+1);
			if(nucleus.contains("#"))
				nucleus=nucleus.substring(0,nucleus.indexOf('#'));
			analyze1DSpectrum(spectra1d.get(spectrum), nucleus, data);
		}
		for(String spectrum : spectra2d.keySet()){
			String[] nucleus = new String[2];
			nucleus[0] = spectrum.substring(spectrum.indexOf("_")+1);
			nucleus[1] = nucleus[0].substring(spectrum.indexOf("_")+1);
			nucleus[0] = nucleus[0].substring(0, nucleus[0].indexOf("_"));
			nucleus[1] = nucleus[1].substring(spectrum.indexOf("_")+1);
			if(nucleus[1].contains("#"))
				nucleus[1]=nucleus[1].substring(0,nucleus[1].indexOf('#'));
			analyze2DSpectrum(spectra2d.get(spectrum), nucleus, data);
		}		
	}

	private void analyze2DSpectrum(String spectrumblock, String[] nucleus, NmreData data) throws NmreDataException {
		StringTokenizer st=new StringTokenizer(spectrumblock,lineseparator);
		double[] freq=null;
		String location=null;
		int peakcount=0;
		while(st.hasMoreTokens()){
			String line = st.nextToken().trim();
			if(line.indexOf(";")>-1)
				line=line.substring(0, line.indexOf(";"));
			if(line.startsWith("Larmor=")){
				freq=new double[]{Double.parseDouble(line.substring(7)),Double.parseDouble(line.substring(7))};
			}else if(line.startsWith("Spectrum_Location=")){
				location=line.substring(line.indexOf("=")+1);
			}else if(line.matches("^H*[0-9]*/H*[0-9]*")){
				peakcount++;
			}
		}
		double[] xdata=new double[peakcount];
		double[] ydata=new double[peakcount];
		st=new StringTokenizer(spectrumblock,lineseparator);
		int i=0;
		while(st.hasMoreTokens()){
			String line = st.nextToken().trim();
			if(line.matches("^H*[0-9]*/H*[0-9]*")){
				StringTokenizer st2=new StringTokenizer(line,"/");
				xdata[i]=signals.get(st2.nextToken()).getPosition()[0];
				ydata[i]=signals.get(st2.nextToken()).getPosition()[0];
			}
		}
		if(freq==null)
			throw new NmreDataException("No Larmor= line, this is mandatory");
		if(location==null)
			throw new NmreDataException("No Spectrum_Location= line, this is mandatory");
		NMR2DSpectrum spectrum = null;
        Unit xUnit =  CommonUnit.hertz;
        Unit yUnit = CommonUnit.hertz;
        Unit zUnit = CommonUnit.intensity;
		OrderedArrayData arraydatax=new OrderedArrayData(xdata, xUnit);
		OrderedArrayData arraydatay=new OrderedArrayData(ydata, yUnit);
		IDataArray1D arraydataz=new ArrayData(new double[xdata.length], zUnit);
        double[] reference = new double[2];
        spectrum = new NMR2DSpectrum(arraydatax, arraydatay, arraydataz, nucleus, freq, reference);
        NoteDescriptor noteDescriptor=new NoteDescriptor("Spectrum_Location");
        spectrum.setNote(noteDescriptor, location);
        //spectrum.setAssignments((Assignment[]) tables[2]);
        data.addSpectrum(spectrum);
	}

	private void analyze1DSpectrum(String spectrumblock, String nucleus, NmreData data) throws NmreDataException, JCAMPException {
		StringTokenizer st=new StringTokenizer(spectrumblock,lineseparator);
		List<Peak> peaks=new ArrayList<>();
		List<String> labels=new ArrayList<>();
		double freq=Double.NaN;
		String location=null;
		while(st.hasMoreTokens()){
			String line = st.nextToken().trim();
			if(line.indexOf(";")>-1)
				line=line.substring(0, line.indexOf(";"));
			if(line.startsWith("Larmor=")){
				freq=Double.parseDouble(line.substring(7));
			}else if(line.startsWith("Spectrum_Location=")){
				location=line.substring(line.indexOf("=")+1);
			}else if(line.matches("^[0-9]*\\.[0-9]*")){
				peaks.add(new Peak1D(Double.parseDouble(line),0));
			}else if(line.matches("^[0-9]*\\.[0-9].*")){
				StringTokenizer st2 = new StringTokenizer(line,",");
				Peak peak=null;
				String multiplicity;
				while(st2.hasMoreTokens()){
					String token=st2.nextToken().trim();
					if(token.indexOf("=")==-1){
						//TODO use information - for now we use the assignment block via L
					}else if(token.startsWith("L")){
						peak=signals.get(token.substring(2).trim());
						labels.add(token.substring(2).trim());
					}else if(token.startsWith("S")){
						multiplicity=token.substring(2).trim();
					}
				}
				//TODO multiplicity
				peaks.add(new Peak1D(peak.getPosition()[0],0));
			}
		}
		if(Double.isNaN(freq))
			throw new NmreDataException("No Larmor= line, this is mandatory");
		if(location==null)
			throw new NmreDataException("No Spectrum_Location= line, this is mandatory");
        NMRSpectrum spectrum = null;
        Unit xUnit =  CommonUnit.hertz;
        Unit yUnit = CommonUnit.intensity;
        double reference = 0;
        Peak1D[] peaks1d = new Peak1D[peaks.size()];
		Assignment[] assignmentslocal=new Assignment[peaks.size()];
        int i=0;
        for(Peak peak : peaks){
        	peaks1d[i]=(Peak1D)peak;
        	if(labels.size()==peaks.size())
        		assignmentslocal[i]=new Assignment(new Pattern(peaks1d[i].getPosition()[0], Multiplicity.UNKNOWN), assignments.get(labels.get(i)));
        	i++;
        }
        double[][] xy=new double[0][];
        IOrderedDataArray1D x = new OrderedArrayData(new double[0], xUnit);
        IDataArray1D y = new ArrayData(new double[0], yUnit);
        if(peaks1d.length>0){
	        xy = peakTableToPeakSpectrum(peaks1d);
	        x = new OrderedArrayData(xy[0], xUnit);
	        y = new ArrayData(xy[1], yUnit);
        }
        spectrum = new NMRSpectrum(x, y, nucleus, freq, reference, false, JCAMPReader.RELAXED);
        spectrum.setPeakTable(peaks1d);
        NoteDescriptor noteDescriptor=new NoteDescriptor("Spectrum_Location");
        spectrum.setNote(noteDescriptor, location);
        if(labels.size()==peaks.size())
        	spectrum.setAssignments(assignmentslocal);
        data.addSpectrum(spectrum);
	}

	
	/**
	 * create peak spectrum from peak table.
	 * adds all intensities belonging to the same x-position up
	 * @param peaks Peak1D[]
	 * @return double[][] array of {x,  y}
	 */
	public static double[][] peakTableToPeakSpectrum(Peak1D[] peaks)
	    throws JCAMPException {
	    int n = peaks.length;
	    if (n == 0)
	        throw new JCAMPException("empty peak table");
	    Arrays.sort(peaks);    
	    ArrayList<Double> px = new ArrayList<>(n);
	    ArrayList<Double> py = new ArrayList<>(n);
	    double x0 = peaks[0].getPosition()[0];
	    double y0 = peaks[0].getHeight();
	    for (int i = 1; i < n; i++) {
	        double x = peaks[i].getPosition()[0];
	        double y = peaks[i].getHeight();
	        if (x - x0 > Double.MIN_VALUE) {
	            px.add(new Double(x0));
	            py.add(new Double(y0));
	            x0 = x;
	            y0 = y;
	        } else {
	            y0 += y;
	        }
	    }
	    px.add(new Double(x0));
	    py.add(new Double(y0));
	    double[][] xy = new double[2][px.size()];
	    for (int i = 0; i < px.size(); i++) {
	        xy[0][i] = ((Double) px.get(i)).doubleValue();
	        xy[1][i] = ((Double) py.get(i)).doubleValue();
	    }
	    return xy;
	}
}
