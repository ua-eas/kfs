package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import edu.arizona.kfs.module.prje.PRJEKeyConstants;


/**
 * A class which lets the PRJE type maintenance document pre-populate the ID for the PRJE type before routing
 */
public class NextPRJETypeIdFinder extends NextSequenceIdFinder {

    /**
     * The name of PRJE Type's sequence, PRJE_TYPE_ID_SEQ
     * @see edu.arizona.kfs.prje.businessobject.defaultvalue.NextSequenceIdFinder#getSequenceName()
     */
    @Override
    public String getSequenceName() {
        return PRJEKeyConstants.PRJE_TYPE_ID_SEQ;
    }

    
}
