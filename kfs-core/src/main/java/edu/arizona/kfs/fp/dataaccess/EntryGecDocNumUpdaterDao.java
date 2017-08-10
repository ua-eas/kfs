package edu.arizona.kfs.fp.dataaccess;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.rice.krad.dao.LookupDao;


public interface EntryGecDocNumUpdaterDao extends LookupDao {
    public void updateEntryGecDocNums(Collection<Entry> entryCollection) throws LookupException, SQLException;
}
