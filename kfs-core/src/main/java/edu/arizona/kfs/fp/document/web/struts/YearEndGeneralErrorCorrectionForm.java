package edu.arizona.kfs.fp.document.web.struts;

import org.kuali.kfs.sys.KFSConstants;

import edu.arizona.kfs.sys.businessobject.defaultvalue.YearEndFiscalYearFinder;

public class YearEndGeneralErrorCorrectionForm extends GeneralErrorCorrectionForm {
    private static final long serialVersionUID = 7268818232511243734L;
    private YearEndFiscalYearFinder yearEndFiscalYearFinder;


    public YearEndGeneralErrorCorrectionForm() {
        super();
        this.yearEndFiscalYearFinder = new YearEndFiscalYearFinder();
        setUniversityFiscalYear(yearEndFiscalYearFinder.getIntegerValue());
    }


    @Override
    @SuppressWarnings("deprecation")//FinancialDocumentTypeCodes
    protected String getDefaultDocumentTypeName() {
        return KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION;
    }


    public YearEndFiscalYearFinder getYearEndFiscalYearFinder() {
        return yearEndFiscalYearFinder;
    }

}
