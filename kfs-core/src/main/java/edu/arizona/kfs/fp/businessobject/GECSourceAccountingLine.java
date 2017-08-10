    package edu.arizona.kfs.fp.businessobject;


import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.businessobject.AccountingLine;

public class GECSourceAccountingLine extends org.kuali.kfs.fp.businessobject.GECSourceAccountingLine {

    public void setObjectTypeCode(@SuppressWarnings("unused") String objectTypeCode) {
        // no-op, since super's getter is a sub-field of a composite object; this
        // satisfies PojoPropertyUtilsBean from complaining
    }


    /*
     * This will be used in GECD#generateEvents(), and for source lines in this method, will
     * result in the following:
     * true: A review event is created
     * false: An update event is created
     *
     * We only ever want a review created, as we never update source lines at all, only add
     * new ones.
     */
    @Override
    public boolean isLike(AccountingLine other) {
        // Source lines are read only, so they should always be considered alike
        // if their objectId's are the same
        return other != null && getObjectId().equals(other.getObjectId());
    }


    /*
     * Overridden to *not* set objectId to null as default: this line's objectId is
     * created when the GLE is converted to a line, and both the GLE's entryId and
     * the lines objectId are used in the new GecEntryRelationship table. We need
     * to know the objectId *before* it's saved in the DB, so we can't wait.
     */
    @Override
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        prePersist();
    }

}
