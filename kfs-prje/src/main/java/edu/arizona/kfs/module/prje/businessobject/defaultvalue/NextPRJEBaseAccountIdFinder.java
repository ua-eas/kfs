package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEPropertyConstants;

public class NextPRJEBaseAccountIdFinder extends NextSequenceIdFinder {

    @Override
    public String getSequenceName() {
        return PRJEPropertyConstants.PRJE_BASE_ACCOUNT_ID_SEQ;
    }

}
