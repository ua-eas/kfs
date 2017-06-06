package edu.arizona.kfs.fp.dataaccess.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springmodules.orm.ojb.OjbFactoryUtils;

import edu.arizona.kfs.fp.dataaccess.EntryGecDocNumUpdaterDao;



public class EntryGecDocNumUpdaterDaoOjb extends LookupDaoOjb implements EntryGecDocNumUpdaterDao {
    private static final Logger LOG = LoggerFactory.getLogger(EntryGecDocNumUpdaterDaoOjb.class);
    private static final String GEC_DOC_NUM_UPDAT_SQL = "update GL_ENTRY_T set GEC_FDOC_NBR = ? where ENTRY_ID = ?";


    /*
     * Update an Entry's gecDocumentNumber only, as we want to be super sure the GEC process
     * only has the ability to mutate this one Entry column, and nothing else.
     */
    @Override
    @Transactional
    public void updateEntryGecDocNums(Collection<Entry> entryCollection) {

        if (entryCollection == null || entryCollection.size() < 1) {
            // no work, short circuit
            LOG.debug("Exiting, with no work passed in: " + entryCollection);
            return;
        } else {
            LOG.debug("updateEntryGecDocNums entryCollection.size():" + entryCollection.size());
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PersistenceBroker broker = null;
        List<Entry> updatedEntries = new ArrayList<>();
        try {
            broker = getPersistenceBroker(false);
            conn = broker.serviceConnectionManager().getConnection();
            ps = conn.prepareStatement(GEC_DOC_NUM_UPDAT_SQL);

            for (Entry entry : entryCollection) {

                Long entryId = entry.getEntryId();
                String gecDocNum = entry.getGecDocumentNumber(); // This can be null, it's how to dissociate Entry from GEC

                if (entryId == null) {
                    // We should always have this in GEC, as our Entry should always come from the DB, or
                    // logic to associate this from a file import which represents an existing GLE in the DB
                    LOG.warn("Found null on non-null ENTRY_ID value on Entry, skipping: (objectId): " + entry.getObjectId());
                    continue;
                }

                LOG.debug("Adding update statement for (entryId, gecDocNum): (" + entryId + ", " + gecDocNum + ")");
                updatedEntries.add(entry);

                ps.setString(1, gecDocNum);
                ps.setLong(2, entryId);
                ps.addBatch();
            }

            if (updatedEntries.size() > 0) {
                ps.executeBatch();
                LOG.info("Updated Entry.gecDocumentNumber's to: " + buildUpdatedLogString(updatedEntries));
            } else {
                LOG.info("Did not update any Entry gecDocNums.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }  finally {
            closeDatabaseObjects(rs, ps, conn, broker);
        }
        LOG.debug("updateEntryGecDocNums: Finished.");
    }


    private void closeDatabaseObjects(ResultSet rs, PreparedStatement ps, Connection conn, PersistenceBroker broker) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                LOG.warn("Failed to close ResultSet.", e);
            }
        }
        if ( ps != null ){
            try {
                ps.close();
            } catch (Exception e) {
                LOG.warn("Failed to close PreparedStatement.", e);
            }
        }
        if ( conn != null ){
            try {
                conn.close();
            } catch (Exception e) {
                LOG.warn("Failed to close Connection.", e);
            }
        }
        if (broker != null) {
            try {
                OjbFactoryUtils.releasePersistenceBroker(broker, getPersistenceBrokerTemplate().getPbKey());
            } catch (Exception e) {
                LOG.error("Failed closing connection: " + e.getMessage(), e);
            }
        }
    }


    private String buildUpdatedLogString(Collection<Entry> entryCollection) {
        StringBuilder sb = new StringBuilder();
        sb.append("(entryId, gecDocNum): ");

        if (entryCollection == null || entryCollection.size() < 1) {
            sb.append("NONE");
        } else {
            String comma = ", ";
            for (Entry entry : entryCollection) {
                sb.append("(").append(entry.getEntryId()).append(", ").append(entry.getGecDocumentNumber()).append(")");
                sb.append(comma);
            }
            sb.setLength(sb.length() - comma.length()); // fencepost hack
        }

        return sb.toString();
    }

}
