package edu.arizona.kfs.sys.businessobject.defaultvalue;

import static edu.arizona.kfs.sys.KFSConstants.COAConstants.DEFAULT_COA_CODE;

import org.kuali.rice.krad.valuefinder.ValueFinder;

/*
 * BAs tell us "UA" is used almost always, and I'm sick of typing it. If
 * there's more interest, we could also use it in Entry.xml to go system
 * wide (currently used by GEC/YEGEC)
 */
public class DefaultCoaCodeFinder implements ValueFinder {
    @Override
    public String getValue() {
        return DEFAULT_COA_CODE;
    }
}
