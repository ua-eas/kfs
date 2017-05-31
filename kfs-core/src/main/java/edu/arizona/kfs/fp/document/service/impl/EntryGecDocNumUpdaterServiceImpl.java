package edu.arizona.kfs.fp.document.service.impl;

import java.util.Collection;

import org.kuali.kfs.gl.businessobject.Entry;

import edu.arizona.kfs.fp.dataaccess.EntryGecDocNumUpdaterDao;
import edu.arizona.kfs.fp.document.service.EntryGecDocNumUpdaterService;


public class EntryGecDocNumUpdaterServiceImpl implements EntryGecDocNumUpdaterService {

    private EntryGecDocNumUpdaterDao entryGecDocNumUpdaterDao;


    @Override
    public void updateEntryGecDocNums(Collection<Entry> entryCollection) {
        try {
            getEntryGecDocNumUpdaterDao().updateEntryGecDocNums(entryCollection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private EntryGecDocNumUpdaterDao getEntryGecDocNumUpdaterDao() {
        return entryGecDocNumUpdaterDao;
    }


    public void setEntryGecDocNumUpdaterDao(EntryGecDocNumUpdaterDao entryGecDocNumUpdaterDao) {
        this.entryGecDocNumUpdaterDao = entryGecDocNumUpdaterDao;
    }

}
