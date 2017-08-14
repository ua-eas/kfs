package edu.arizona.kfs.sys.businessobject.defaultvalue;

import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/*
 * Supply the previous fiscal year as a String, helpful in YE lookup
 */
public class YearEndFiscalYearFinder implements ValueFinder {

    // Used to set default on java forms, if needed
    public Integer getIntegerValue() {
        return SpringContext.getBean(YearEndPendingEntryService.class).getPreviousFiscalYear();
    }


    // Used by DD to default values in lookup
    public String getValue() {
        return getIntegerValue().toString();
    }

}
