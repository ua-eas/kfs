package edu.arizona.kfs.gl.web.struts;

import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.context.SpringContext;

public class YegecEntryLookupAction extends GecEntryLookupAction {

    @Override
    protected boolean isFiscalYearValid(Entry entry) {
        Integer lastYear = SpringContext.getBean(YearEndPendingEntryService.class).getPreviousFiscalYear();
        Integer entryYear = entry.getUniversityFiscalYear();

        // No null check on lastYear, as we want to fail fast if the service serves us garbage
        return entryYear != null && lastYear.equals(entryYear);
    }

}
