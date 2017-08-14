package edu.arizona.kfs.sys.businessobject.defaultvalue;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

/*
 * Extended for integer method
 */
public class FiscalYearFinder extends org.kuali.kfs.sys.businessobject.defaultvalue.FiscalYearFinder {


    public Integer getIntegerValue() {
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }


    @Override
    public String getValue() {
        return getIntegerValue().toString();
    }

}
