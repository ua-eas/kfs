package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEKeyConstants;

public class NextPRJEBaseObjectIdFinder extends NextSequenceIdFinder {

    @Override
    public String getSequenceName() {
        return PRJEKeyConstants.PRJE_BASE_OBJECT_ID_SEQ;
    }

}
