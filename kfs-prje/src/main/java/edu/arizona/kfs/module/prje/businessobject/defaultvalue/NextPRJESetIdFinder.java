package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEKeyConstants;



/**
 * A class which lets the PRJE set maintenance document pre-populate the ID for the PRJE set before routing
 */
public class NextPRJESetIdFinder extends NextSequenceIdFinder {

    /**
     * The name of PRJE Set's ID sequence, "PRJE_SET_ID_SEQ"
     * @see edu.arizona.kfs.module.prje.businessobject.defaultvalue.NextSequenceIdFinder#getSequenceName()
     */
    @Override
    public String getSequenceName() {
        return PRJEKeyConstants.PRJE_SET_ID_SEQ;
    }


}
