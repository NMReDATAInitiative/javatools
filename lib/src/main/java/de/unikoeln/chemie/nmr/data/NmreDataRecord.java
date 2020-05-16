package de.unikoeln.chemie.nmr.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class NmreDataRecord{
	NmreData data=null;
	List<File> files=null;
	
	/**
	 * Constructuor for the NMReDATA record.
	 * 
	 * @param files The raw data files for the spectra. Must be unpacked directories containingg the raw data according to the manufacturers specification.
	 * @param data The NMReData file for the record.
	 */
	public NmreDataRecord(List<File> files, NmreData data){
		this.data=data;
		this.files=files;
	}
	
	public NmreData getData() {
		return data;
	}

	public List<File> getFiles() {
		return files;
	}
}