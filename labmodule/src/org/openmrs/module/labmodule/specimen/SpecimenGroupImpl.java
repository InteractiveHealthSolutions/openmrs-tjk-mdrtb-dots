package org.openmrs.module.labmodule.specimen;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.Xpert;
import org.openmrs.module.labmodule.specimen.ScannedLabReport;

/**
 * This class is a special implementation of the Specimen interface that
 * allows a group of specimens to be viewed as a single specimen.
 * 
 * For all non-collection data, the getters simply return the value of first (sorted via Collections.sort) member
 * of the group.  For collections, the getters return all members (i.e., all smears, cultures, etc) of the underlying specimens.
 * 
 * This class is intended for viewing only... all add/set methods throw exceptions.
 * 
 * This class was designed for use with the patient chart, which groups specimens collected
 * on the same day into a single "specimen group" row on the chart
 */
public class SpecimenGroupImpl implements Specimen {

	List<Specimen> specimens;
	
	// TODO: we cache the result map; do we need to worry about resetting the cache ?
	// (right now it isn't much of an issue because in the web model a new specimen is instantiated during each request)
	Map<Integer,List<DstResult>> dstResultsMap = null; 
	
	public SpecimenGroupImpl() {
		this.specimens = new LinkedList<Specimen>();
	}
	
	public SpecimenGroupImpl(List<Specimen> specimens) {
		this.specimens = specimens;
		Collections.sort(this.specimens);
	}
	
	
	/**
	 * Getters/setters specific to SpecimenGroupImpl
	 */
	
	
    public List<Specimen> getSpecimens() {
    	return specimens;
    }

	
    public void setSpecimens(List<Specimen> specimens) {
    	this.specimens = specimens;
    }
    
    public void addSpecimen(Specimen specimen) {
    	if(!this.specimens.contains(specimen)) {
    		this.specimens.add(specimen);
    		Collections.sort(this.specimens);
    	}
    }
    
    public void removeSpecimen(Specimen specimen) {
    	this.specimens.remove(specimen);
    }

	/**
	 * Specimen Interface methods
	 */
	
    public Culture addCulture() {
    	throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public Dst addDst() {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }
    
    public Xpert addXpert() {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public HAIN addHAIN() {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

   /* public ScannedLabReport addScannedLabReport() {
    	throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }*/

    public Smear addSmear() {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public Concept getAppearance() {
	    return specimens.get(0).getAppearance();
    }

    public String getComments() {
	    return specimens.get(0).getComments();
    }

    public List<Culture> getCultures() {
    	List<Culture> cultures = new LinkedList<Culture>();
    	
    	for(Specimen specimen : specimens) {
    		cultures.addAll(specimen.getCultures());
    	}
    	
    	Collections.sort(cultures);
    	
    	return cultures;
    }

    public Date getDateCollected() {
	    return specimens.get(0).getDateCollected();
    }

    // this is identical to the Specimen getDstResultsMap implementation...
    // but since it calls the local getDsts() method it will create a map of dsts across all specimens in this group
    public Map<Integer, List<DstResult>> getDstResultsMap() {
		List<Dst> dsts = getDsts();
		
	    if (dstResultsMap == null && dsts.size() > 0) {  
	    	dstResultsMap = new HashMap<Integer,List<DstResult>>();
	    	
	    	for(Dst dst : dsts) {
	    		for(DstResult result : dst.getResults()) {
	    		
	    			Integer drug = result.getDrug().getId();
    			
	    			// if a result for this drug already exists in the map, attach this result to that list
	    			if(dstResultsMap.containsKey(drug)) {
	    				dstResultsMap.get(drug).add(result);
	    				// re-sort, so that the concentrations are in order
	    				Collections.sort(dstResultsMap.get(drug));
	    			}
	    			// otherwise, create a new entry for this drug
	    			else {
	    				List<DstResult> drugResults = new LinkedList<DstResult>();
	    				drugResults.add(result);
	    				dstResultsMap.put(drug, drugResults);
	    			}
    			}
	    	}
	    }	
		return dstResultsMap;
    }

    public List<Dst> getDsts() {
    	List<Dst> dsts = new LinkedList<Dst>();
    	
    	for(Specimen specimen : specimens) {
    		dsts.addAll(specimen.getDsts());
    	}
    	
    	Collections.sort(dsts);
    	
    	return dsts;
    }
    
    public List<Xpert> getXperts() {
    	List<Xpert> xperts = new LinkedList<Xpert>();
    	
    	for(Specimen specimen : specimens) {
    		xperts.addAll(specimen.getXperts());
    	}
    	
    	Collections.sort(xperts);
    	
    	return xperts;
    }
    
    public List<HAIN> getHAINs() {
    	List<HAIN> hains = new LinkedList<HAIN>();
    	
    	for(Specimen specimen : specimens) {
    		hains.addAll(specimen.getHAINs());
    	}
    	
    	Collections.sort(hains);
    	
    	return hains;
    }

    public String getId() {
	   return this.specimens.get(0).getId();
    }

    public String getIdentifier() {
	    return this.specimens.get(0).getIdentifier();
    }

    public Location getLocation() {
	   return this.specimens.get(0).getLocation();
    }

    public Patient getPatient() {
	   return this.specimens.get(0).getPatient();
    }

    public Person getProvider() {
	   return this.specimens.get(0).getProvider();
    }
/*
    public List<ScannedLabReport> getScannedLabReports() {
    	List<ScannedLabReport> scannedLabReports = new LinkedList<ScannedLabReport>();
    	
    	for(Specimen specimen : specimens) {
    		scannedLabReports.addAll(specimen.getScannedLabReports());
    	}
    	
    	return scannedLabReports;
    }*/

    public List<Smear> getSmears() {
    	List<Smear> smears = new LinkedList<Smear>();
    	
    	for(Specimen specimen : specimens) {
    		smears.addAll(specimen.getSmears());
    	}
    	
    	Collections.sort(smears);
    	
    	return smears;
    }

    public Object getSpecimen() {
    	return this.specimens.get(0).getSpecimen();
    }

    public List<Test> getTests() {
    	List<Test> tests = new LinkedList<Test>();
    	
    	for(Specimen specimen : specimens) {
    		tests.addAll(specimen.getTests());
    	}
    	
    	Collections.sort(tests);
    	
    	return tests;
    }

    public Concept getType() {
	    return this.specimens.get(0).getType();
    }

    public Double getMonthOfTreatment() {
    	return this.specimens.get(0).getMonthOfTreatment();
    }
    
    /*public void removeScannedLabReport(ScannedLabReport report) {
    	throw new RuntimeException("Illegal attempt to access removeScannedLabReport method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }*/
    
    public void setAppearance(Concept appearance) {
    	throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setComments(String comments) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setDateCollected(Date dateCollected) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setIdentifier(String id) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setLocation(Location location) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setPatient(Patient patient) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setProvider(Person provider) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

    public void setType(Concept type) {
		throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
    }

	
    /**
	 * Implementation of comparable method
	 */
	public int compareTo(Specimen specimenToCompare) {
		return this.getDateCollected().compareTo(specimenToCompare.getDateCollected());
	}

	@Override
	public ScannedLabReport addScannedLabReport() {
    	throw new RuntimeException("Illegal attempt to access add or set method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
	}

	@Override
	public void removeScannedLabReport(ScannedLabReport report) {
    	throw new RuntimeException("Illegal attempt to access removeScannedLabReport method of SpecimenGroupImpl. SpecimenGroupImpl should be used for get access only.");
	}

	@Override
	public List<ScannedLabReport> getScannedLabReports() {
		List<ScannedLabReport> scannedLabReports = new LinkedList<ScannedLabReport>();
    	
    	for(Specimen specimen : specimens) {
    		scannedLabReports.addAll(specimen.getScannedLabReports());
    	}
    	
    	return scannedLabReports;
	}
	
	
}
