package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEKeyConstants;

public class NextPRJEBaseAccountIdFinder extends NextSequenceIdFinder {

    @Override
    public String getSequenceName() {
        return PRJEKeyConstants.PRJE_BASE_ACCOUNT_ID_SEQ;
    }

}
