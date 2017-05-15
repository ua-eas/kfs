package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

import edu.arizona.kfs.module.prje.PRJEKeyConstants;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;

/**
 * An abstract sequence id finder
 */
public abstract class NextSequenceIdFinder implements ValueFinder {

    /**
     * Returns the value of the next ID in the sequence associated with the concrete class
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }
    
    /**
     * @return the next sequence number value as a Long.
     */
    public Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber(PRJEKeyConstants.PRJE_TYPE_ID_SEQ, PRJEType.class);
    }
    
    /**
     * @return The name of the sequence to get the next value from
     */
    public abstract String getSequenceName();

}
