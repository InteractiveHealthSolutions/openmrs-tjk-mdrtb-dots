package org.openmrs.module.mdrtb.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

public class ObsEditor extends PropertyEditorSupport {

    private Log log = LogFactory.getLog(this.getClass());
    
    public ObsEditor() { }
    
    public void setAsText(String text) throws IllegalArgumentException {
        ObsService es = Context.getObsService(); 
        if (StringUtils.hasText(text)) {
            try {
                setValue(es.getObs(Integer.valueOf(text)));
            }
            catch (Exception ex) {
                log.error("Error setting text: " + text, ex);
                throw new IllegalArgumentException("Encounter not found: " + ex.getMessage());
            }
        }
        else {
            setValue(null);
        }
    }
    
    public String getAsText() {
        Obs e = (Obs) getValue();
        if (e == null) {
            return "";
        }
        else {
            return e.getObsId().toString();
        }
    }
    
}
