package edu.arizona.kfs.module.prje.lookup;

import org.kuali.kfs.sys.businessobject.options.ParameterValuesFinder;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.ProrateJournalEntry;

/** 
 * Return the list of Object Types listed in the PRJE_OBJECT_TYPE parameter
 */
public class ObjectType extends ParameterValuesFinder {
	public ObjectType() {
		super(ProrateJournalEntry.class, PRJEConstants.OBJECT_TYPE);
	}

}
