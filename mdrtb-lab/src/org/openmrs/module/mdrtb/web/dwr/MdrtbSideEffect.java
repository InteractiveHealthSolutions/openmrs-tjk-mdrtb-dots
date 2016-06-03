package org.openmrs.module.mdrtb.web.dwr;

@Deprecated
public class MdrtbSideEffect {

        private String type;
        private String agent;
        private String date;
        private String traitmentSupportif;
        
        
        public void setType(String type) {
            this.type = type;
        }
        public void setAgent(String agent) {
            this.agent = agent;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public void setTraitmentSupportif(String traitmentSupportif) {
            this.traitmentSupportif = traitmentSupportif;
        }
        
        
        
}
