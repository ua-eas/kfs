package edu.arizona.kfs.fp.document.service;

import java.util.Collection;

import org.kuali.kfs.gl.businessobject.Entry;


public interface EntryGecDocNumUpdaterService {
    public void updateEntryGecDocNums(Collection<Entry> entryCollection);
}
