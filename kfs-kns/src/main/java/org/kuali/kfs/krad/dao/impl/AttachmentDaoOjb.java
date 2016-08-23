/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.krad.dao.impl;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.krad.bo.Attachment;
import org.kuali.kfs.krad.dao.AttachmentDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class is the OJB implementation of the NoteDao interface.
 */
public class AttachmentDaoOjb extends PlatformAwareDaoBaseOjb implements AttachmentDao {
    private static Logger LOG = Logger.getLogger(AttachmentDaoOjb.class);

    /**
     * Default constructor.
     */
    public AttachmentDaoOjb() {
        super();
    }

    public Attachment getAttachmentByNoteId(Long noteId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("noteIdentifier", noteId);
        return (Attachment) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Attachment.class, crit));
    }

}
