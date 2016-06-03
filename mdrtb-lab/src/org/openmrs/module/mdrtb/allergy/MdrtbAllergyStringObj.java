package org.openmrs.module.mdrtb.allergy;

import java.util.Date;

public class MdrtbAllergyStringObj {
    
    private String medication;
    private String effect;
    private Date date;
    private String supportingTreatment;
    
    public String getMedication() {
        return medication;
    }
    public void setMedication(String medication) {
        this.medication = medication;
    }
    public String getEffect() {
        return effect;
    }
    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getSupportingTreatment() {
        return supportingTreatment;
    }
    public void setSupportingTreatment(String supportingTreatment) {
        this.supportingTreatment = supportingTreatment;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
    
    
}
