package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Location;
import org.springframework.web.multipart.MultipartFile;


public interface ScannedLabReport {

		/**
		 * Data this interface provides access get/set access to:
		 * 
		 * lab: the lab where the test was performed
		 * 
		 * Data this interface provides get access to:
		 * 
		 * scannedLabReport: the actual object that stores the data this interface is providing access to (i.e., in our current implementation, the scanned lab report obs)
		 * id: the id used to reference the scanned lab report (i.e., in our current implementation, the obs_id of the scanned lab report obs)
		 * filename: the name of the scanned lab report filename
		 * 
		 * Data this interface provides set access to:
		 * 
		 * file: the file that contains scanned lab report
		 * 
		 * TODO: provide get access to the underlying file
		 */
		
		public Object getScannedLabReport();
		public String getId();
		public String getFilename();
		
		public Object getFile();
		public void setFile(MultipartFile file);
		
		public Location getLab();
		public void setLab(Location location);
}
