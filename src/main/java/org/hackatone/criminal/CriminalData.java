package org.hackatone.criminal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hackatone.utils.DistanceCalculator;

public class CriminalData {
	private static Logger LOGGER = Logger.getLogger(CriminalData.class.getPackage().getName());
	private static Set<CriminalRecord> criminalRecords = new HashSet<CriminalRecord>();
	
	private static void readDataFiles() {
		String path = "/home/ubuntu/PoliceIncidents";
//		String path = "/Users/yankopy/warehouse/to/hackatone/2015.09.Stamford/data";
//		readDataFile(path + "/Police_Incidents_01012005_to_12312005.csv");
//		readDataFile(path + "/Police_Incidents_01012006_to_12312006.csv");
//		readDataFile(path + "/Police_Incidents_01012007_to_12312007.csv");
//		readDataFile(path + "/Police_Incidents_01012008_to_12312008.csv");
//		readDataFile(path + "/Police_Incidents_01012009_to_12312009.csv");
//		readDataFile(path + "/Police_Incidents_01012010_to_12312010.csv");
//		readDataFile(path + "/Police_Incidents_01012011_to_12312011.csv");
//		readDataFile(path + "/Police_Incidents_01012012_to_12312012.csv");
		readDataFile(path + "/Police_Incidents_01012013_to_12312013.csv");
		readDataFile(path + "/Police_Incidents_01012014_to_12312014.csv");
		readDataFile(path + "/Police_Incidents_01012015_to_12312015.csv");
	}
	
	private static void readDataFile(String fileName) {
		String line = null;

//		File file1 = new File(fileName);
//		LOGGER.info("file1.exists()" + file1.exists());
		
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				String[] lineSplitted = line.split(",");
//				LOGGER.info("lineSplitted.length = " + lineSplitted.length);
				
				if (lineSplitted.length == 13) {
					CriminalRecord cr = new CriminalRecord();
					cr.setCase_Number(lineSplitted[0]);
					cr.setDate(lineSplitted[1]);
					cr.setTime_24HR(lineSplitted[2]);
					cr.setAddress(lineSplitted[3]);
					cr.setUCR_1_Category(lineSplitted[4]);
					cr.setUCR_1_Description(lineSplitted[5]);
					cr.setUCR_1_Code(lineSplitted[6]);
					cr.setUCR_2_Category(lineSplitted[7]);
					cr.setUCR_2_Description(lineSplitted[8]);
					cr.setUCR_2_Code(lineSplitted[9]);
					cr.setNeighborhood(lineSplitted[10]);
					cr.setGeom(lineSplitted[11].substring(2) + "," + lineSplitted[12].substring(0, lineSplitted[12].length() - 2));
					criminalRecords.add(cr);
//					LOGGER.info("We have " + criminalRecords.size() + " records!..");
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			LOGGER.error("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			LOGGER.error("Error reading file '" + fileName + "'");
		}
	}

	public Set<CriminalRecord> getNearEvents(double lat, double lng, int maxDistance) {
		Set<CriminalRecord> results = new HashSet<CriminalRecord>(); 
		if (criminalRecords == null || criminalRecords.size() == 0) {
			readDataFiles();
			
			LOGGER.info("---------------------------------------------------------------------------");
			LOGGER.info("All criminalRecords is loaded! We have " + criminalRecords.size() + " records!..");
			LOGGER.info("---------------------------------------------------------------------------");
		}
		
//		LOGGER.info("We have " + results.size() + " criminal records!..");
		
		for (CriminalRecord cr : criminalRecords) {
			String geom = cr.getGeom();
			String[] geomSplitted = geom.split(",");
			
			if (geomSplitted != null && geomSplitted.length == 2) {
				double dist = DistanceCalculator.distance(Double.valueOf(geomSplitted[0]), Double.valueOf(geomSplitted[1]), lat, lng);
				
//				LOGGER.info("dist = " + dist);
				if (dist <= maxDistance) {
					results.add(cr);
				}	
			}
		}
		return results;
	}
}
