package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEKeyConstants;

public class NextPRJEAccountLineIdFinder extends NextSequenceIdFinder {

    @Override
    public String getSequenceName() {
        return PRJEKeyConstants.PRJE_ACCT_LINE_ID_SEQ;
    }

}
