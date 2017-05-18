package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEPropertyConstants;

public class NextPRJEBaseObjectIdFinder extends NextSequenceIdFinder {

    @Override
    public String getSequenceName() {
        return PRJEPropertyConstants.PRJE_BASE_OBJECT_ID_SEQ;
    }

}
