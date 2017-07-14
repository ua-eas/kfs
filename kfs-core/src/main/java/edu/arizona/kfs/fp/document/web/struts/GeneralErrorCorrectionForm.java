package edu.arizona.kfs.fp.document.web.struts;


import edu.arizona.kfs.sys.businessobject.defaultvalue.FiscalYearFinder;



public class GeneralErrorCorrectionForm extends org.kuali.kfs.fp.document.web.struts.GeneralErrorCorrectionForm {
    private static final long serialVersionUID = 2589170663827583574L;

    private Integer universityFiscalYear;
    private String glDocId;
    private FiscalYearFinder fiscalYearFinder;
    private final String universityFiscalPeriodCodeLookupOverride = "*";


    public GeneralErrorCorrectionForm() {
        super();
        this.fiscalYearFinder = new FiscalYearFinder();
        this.universityFiscalYear = new Integer(getFiscalYearFinder().getValue());
    }


    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }


    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    public String getGlDocId() {
        return glDocId;
    }


    public void setGlDocId(String glDocId) {
        this.glDocId = glDocId;
    }


    public String getUniversityFiscalPeriodCodeLookupOverride() {
        return universityFiscalPeriodCodeLookupOverride;
    }


    public FiscalYearFinder getFiscalYearFinder() {
        return fiscalYearFinder;
    }

}
