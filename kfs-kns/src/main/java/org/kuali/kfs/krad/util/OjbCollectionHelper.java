/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.krad.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Helper object to deal with persisting collections.
 */
public class OjbCollectionHelper {
	private static final Logger LOG = Logger.getLogger(OjbCollectionHelper.class);
    /**
     * OJB RemovalAwareLists do not survive through the response/request lifecycle. This method is a work-around to forcibly remove
     * business objects that are found in Collections stored in the database but not in memory.
     * 
     * @param orig
     * @param id
     * @param template
     */
    public void processCollections(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy) {
        if (copy == null) {
            return;
        }
        
        List<Collection<PersistableBusinessObject>> originalCollections = orig.buildListOfDeletionAwareLists();

        if (originalCollections != null && !originalCollections.isEmpty()) {
            /*
             * Prior to being saved, the version in the database will not yet reflect any deleted collections. So, a freshly
             * retrieved version will contain objects that need to be removed:
             */
            try {
                List<Collection<PersistableBusinessObject>> copyCollections = copy.buildListOfDeletionAwareLists();
                int size = originalCollections.size();

                if (copyCollections.size() != size) {
                    throw new RuntimeException("size mismatch while attempting to process list of Collections to manage");
                }

                for (int i = 0; i < size; i++) {
                    Collection<PersistableBusinessObject> origSource = originalCollections.get(i);
                    Collection<PersistableBusinessObject> copySource = copyCollections.get(i);
                    List<PersistableBusinessObject> list = findUnwantedElements(copySource, origSource);
                    cleanse(template, origSource, list);
                }
            }
            catch (ObjectRetrievalFailureException orfe) {
                // object wasn't found, must be pre-save
            }
        }
    }
    
    /**
     * OJB RemovalAwareLists do not survive through the response/request lifecycle. This method is a work-around to forcibly remove
     * business objects that are found in Collections stored in the database but not in memory.
     * 
     * @param orig
     * @param id
     * @param template
     */
    public void processCollections2(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy) {
        // if copy is null this is the first time we are saving the object, don't have to worry about updating collections
        if (copy == null) {
            return;
        }
        
        List<Collection<PersistableBusinessObject>> originalCollections = orig.buildListOfDeletionAwareLists();

        if (originalCollections != null && !originalCollections.isEmpty()) {
            /*
             * Prior to being saved, the version in the database will not yet reflect any deleted collections. So, a freshly
             * retrieved version will contain objects that need to be removed:
             */
            try {
                List<Collection<PersistableBusinessObject>> copyCollections = copy.buildListOfDeletionAwareLists();
                int size = originalCollections.size();

                if (copyCollections.size() != size) {
                    throw new RuntimeException("size mismatch while attempting to process list of Collections to manage");
                }

                for (int i = 0; i < size; i++) {
                    Collection<PersistableBusinessObject> origSource = originalCollections.get(i);
                    Collection<PersistableBusinessObject> copySource = copyCollections.get(i);
                    List<PersistableBusinessObject> list = findUnwantedElements(copySource, origSource);
                    cleanse(template, origSource, list);
                }
            }
            catch (ObjectRetrievalFailureException orfe) {
                // object wasn't found, must be pre-save
            }
        }
    }

    /**
     * This method deletes unwanted objects from the database as well as from the given input List
     * 
     * @param origSource - list containing unwanted business objects
     * @param unwantedItems - business objects to be permanently removed
     * @param template
     */
    private void cleanse(OjbCollectionAware template, Collection<PersistableBusinessObject> origSource, List<PersistableBusinessObject> unwantedItems) {
        if (unwantedItems.size() > 0) {
        	for (PersistableBusinessObject unwantedItem : unwantedItems) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug( "cleansing " + unwantedItem);
            	}
                template.getPersistenceBrokerTemplate().delete(unwantedItem);
            }
        }

    }

    /**
     * This method identifies items in the first List that are not contained in the second List. It is similar to the (optional)
     * java.util.List retainAll method.
     * 
     * @param fromList
     * @param controlList
     * @return true iff one or more items were removed
     */
    private List<PersistableBusinessObject> findUnwantedElements(Collection<PersistableBusinessObject> fromList, Collection<PersistableBusinessObject> controlList) {
        List<PersistableBusinessObject> toRemove = new ArrayList<PersistableBusinessObject>();

        for (PersistableBusinessObject fromObject : fromList) {
        	if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(controlList, fromObject)) {
                toRemove.add(fromObject);
            }
        }
        return toRemove;
    }
}
