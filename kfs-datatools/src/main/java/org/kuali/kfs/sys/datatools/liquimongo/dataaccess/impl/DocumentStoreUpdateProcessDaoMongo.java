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
package org.kuali.kfs.sys.datatools.liquimongo.dataaccess.impl;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.kuali.kfs.sys.datatools.liquimongo.businessobject.DocumentStoreChange;
import org.kuali.kfs.sys.datatools.liquimongo.dataaccess.DocumentStoreUpdateProcessDao;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DocumentStoreUpdateProcessDaoMongo implements DocumentStoreUpdateProcessDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentStoreUpdateProcessDaoMongo.class);

    private MongoTemplate mongoTemplate;

    @Override
    public boolean isSchemaChangeLocked() {
        LOG.debug("isSchemaChangeLocked() started");

        Query q = new Query();
        q.addCriteria(Criteria.where("locked").is(true));

        return (mongoTemplate.findOne(q, DBObject.class, CHANGE_SCHEMA) != null);
    }

    @Override
    public void lockSchemaChange() {
        LOG.debug("lockSchemaChange() started");

        DBObject lock = (DBObject) JSON.parse("{ \"locked\": true }");
        mongoTemplate.save(lock, CHANGE_SCHEMA);
    }

    @Override
    public void unlockSchemaChange() {
        LOG.debug("unlockSchemaChange() started");

        Query q = new Query();
        q.addCriteria(Criteria.where("locked").is(true));

        mongoTemplate.remove(q, CHANGE_SCHEMA);
    }

    @Override
    public boolean hasSchemaChangeHappened(DocumentStoreChange change) {
        LOG.debug("hasSchemaChangeHappened() started");

        Query q = new Query();
        q.addCriteria(Criteria.where("fileName").is(change.getFileName()));
        q.addCriteria(Criteria.where("changeId").is(change.getChangeId()));
        q.addCriteria(Criteria.where("hash").is(change.getHash()));

        return (mongoTemplate.findOne(q, DocumentStoreChange.class, CHANGE_SCHEMA) != null);
    }

    @Override
    public void saveSchemaChange(DocumentStoreChange change) {
        LOG.debug("saveSchemaChange() started");

        mongoTemplate.save(change, CHANGE_SCHEMA);
    }

    @Override
    public void removeSchemaChange(DocumentStoreChange change) {
        LOG.debug("removeSchemaChange() started");

        Query q = new Query();
        q.addCriteria(Criteria.where("fileName").is(change.getFileName()));
        q.addCriteria(Criteria.where("changeId").is(change.getChangeId()));
        q.addCriteria(Criteria.where("hash").is(change.getHash()));

        mongoTemplate.remove(q, CHANGE_SCHEMA);
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
