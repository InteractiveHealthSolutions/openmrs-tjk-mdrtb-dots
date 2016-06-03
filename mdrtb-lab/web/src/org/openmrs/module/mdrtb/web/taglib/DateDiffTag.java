package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateDiffTag extends TagSupport {
    public static final long serialVersionUID = 121341222L;
    private final Log log = LogFactory.getLog(getClass());
    private Date fromDate;
    private Date toDate;
    private String format;

    
    public int doStartTag() {
     
        if (fromDate != null){   
            
            try{   
               if (toDate == null)
                   toDate = new Date();
               if (format == null)
                   format = "D";
               
       //        long diff = toDate.getTime() - fromDate.getTime(); //milliseconds
       //           
       //            int numerator = (1000*60*60*24); //days
       //            
       //                if (format.equals("M"))
       //                    numerator = numerator * 30;
       //                if (format.equals("Y"))
       //                    numerator = numerator * 365;
       //                if (format.equals("h"))
       //                    numerator = numerator / 24;
       //                if (format.equals("m"))
       //                    numerator = numerator / (24*60);
       //            
                   //float diffF = (float) diff / numerator;
                   //int ret = (int) diffF;
                   
                   
                       
                   Calendar calFrom = Calendar.getInstance();
                   Calendar calTo = Calendar.getInstance();
                   
                   calFrom.setTime(fromDate);
                   calTo.setTime(toDate);
                   
                   int calFromDaysOfYear = calFrom.get(Calendar.DAY_OF_YEAR);
                   int calFromYear = calFrom.get(Calendar.YEAR);
                   int calToDaysOfYear = calTo.get(Calendar.DAY_OF_YEAR);
                   int calToYear = calTo.get(Calendar.YEAR);
                   int ret = calToDaysOfYear - calFromDaysOfYear;
                   int yearDif = calToYear - calFromYear;
                   
                      
                   int yearDifTotal = 0;
                       for (int i = 0; i < yearDif; i++){
                           int yearThisIteration = calFrom.get(Calendar.YEAR) + i;
                           Calendar yearTest = Calendar.getInstance();
                           yearTest.set(Calendar.YEAR, yearThisIteration);
                           int daysInStartYear = calFrom.getMaximum(Calendar.DAY_OF_YEAR);
                           yearDifTotal += daysInStartYear;
                       }
                   ret = ret + yearDifTotal;
                   
                   
               try {
                   pageContext.getOut().write(String.valueOf(ret));
               }
               catch (IOException e) {
                   log.error("Could not write to pageContext", e);
               }
            } catch (Exception ex){
                log.error("Failure in dateDiffTag class", ex);
            }
        }
           
           // reset the objects to null because taglibs are reused
           release();
           
           return SKIP_BODY;
    }
    
    public int doEndTag() {
       fromDate = null;
        toDate = null;
        format = null;

        return EVAL_PAGE;
    }
    
    public void release() {
        super.release();
        this.fromDate = null;
        this.toDate = null;
        this.format = null;
    }
    
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date date) {
        this.fromDate = date;
    }
    
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date date) {
        this.toDate = date;
    }
    
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
