package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEPropertyConstants;

public class NextPRJEAccountLineIdFinder extends NextSequenceIdFinder {

    @Override
    public String getSequenceName() {
        return PRJEPropertyConstants.PRJE_ACCT_LINE_ID_SEQ;
    }

}
