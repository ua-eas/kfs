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
package org.kuali.kfs.sys.datatools.mock;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MockMongoTemplate implements MongoOperations {
    public int saveCalled = 0;
    public int removeCalled = 0;

    public MockMongoTemplate() {

    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> typedAggregation, String s, Class<O> aClass) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> typedAggregation, Class<O> aClass) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> aClass, Class<O> aClass1) {
        return null;
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, String s, Class<O> aClass) {
        return null;
    }

    @Override
    public boolean exists(Query query, String s) {
        return false;
    }

    @Override
    public boolean exists(Query query, Class<?> aClass) {
        return false;
    }

    @Override
    public boolean exists(Query query, Class<?> aClass, String s) {
        return false;
    }

    @Override
    public WriteResult upsert(Query query, Update update, Class<?> aClass, String s) {
        return null;
    }

    @Override
    public WriteResult updateFirst(Query query, Update update, Class<?> aClass, String s) {
        return null;
    }

    @Override
    public WriteResult updateMulti(Query query, Update update, Class<?> aClass, String s) {
        return null;
    }

    @Override
    public void remove(Query query, Class<?> aClass) {
        removeCalled++;
    }

    @Override
    public void remove(Query query, Class<?> aClass, String s) {
        removeCalled++;
    }

    @Override
    public String getCollectionName(Class<?> aClass) {
        return null;
    }

    @Override
    public CommandResult executeCommand(String s) {
        return null;
    }

    @Override
    public CommandResult executeCommand(DBObject dbObject) {
        return null;
    }

    @Override
    public CommandResult executeCommand(DBObject dbObject, int i) {
        return null;
    }

    @Override
    public void executeQuery(Query query, String s, DocumentCallbackHandler documentCallbackHandler) {

    }

    @Override
    public <T> T execute(DbCallback<T> dbCallback) {
        return null;
    }

    @Override
    public <T> T execute(Class<?> aClass, CollectionCallback<T> collectionCallback) {
        return null;
    }

    @Override
    public <T> T execute(String s, CollectionCallback<T> collectionCallback) {
        return null;
    }

    @Override
    public <T> T executeInSession(DbCallback<T> dbCallback) {
        return null;
    }

    @Override
    public <T> DBCollection createCollection(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> DBCollection createCollection(Class<T> aClass, CollectionOptions collectionOptions) {
        return null;
    }

    @Override
    public DBCollection createCollection(String s) {
        return null;
    }

    @Override
    public DBCollection createCollection(String s, CollectionOptions collectionOptions) {
        return null;
    }

    @Override
    public Set<String> getCollectionNames() {
        return null;
    }

    @Override
    public DBCollection getCollection(String s) {
        return null;
    }

    @Override
    public <T> boolean collectionExists(Class<T> aClass) {
        return false;
    }

    @Override
    public boolean collectionExists(String s) {
        return false;
    }

    @Override
    public <T> void dropCollection(Class<T> aClass) {

    }

    @Override
    public void dropCollection(String s) {

    }

    @Override
    public IndexOperations indexOps(String s) {
        return null;
    }

    @Override
    public IndexOperations indexOps(Class<?> aClass) {
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> GroupByResults<T> group(String s, GroupBy groupBy, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> GroupByResults<T> group(Criteria criteria, String s, GroupBy groupBy, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String s, String s1, String s2, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String s, String s1, String s2, MapReduceOptions mapReduceOptions, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String s, String s1, String s2, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String s, String s1, String s2, MapReduceOptions mapReduceOptions, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery nearQuery, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery nearQuery, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> T findOne(Query query, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T findOne(Query query, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> List<T> find(Query query, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> List<T> find(Query query, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> T findById(Object o, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T findById(Object o, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, Update update, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, Update update, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, Update update, FindAndModifyOptions findAndModifyOptions, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T findAndModify(Query query, Update update, FindAndModifyOptions findAndModifyOptions, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public long count(Query query, Class<?> aClass) {
        return 0;
    }

    @Override
    public long count(Query query, String s) {
        return 0;
    }

    @Override
    public void insert(Object o) {

    }

    @Override
    public void insert(Object o, String s) {

    }

    @Override
    public void insert(Collection<? extends Object> collection, Class<?> aClass) {

    }

    @Override
    public void insert(Collection<? extends Object> collection, String s) {

    }

    @Override
    public void insertAll(Collection<? extends Object> collection) {

    }

    @Override
    public void save(Object o) {
        saveCalled++;
    }

    @Override
    public void save(Object o, String s) {
        saveCalled++;
    }

    @Override
    public WriteResult upsert(Query query, Update update, Class<?> aClass) {
        return null;
    }

    @Override
    public WriteResult upsert(Query query, Update update, String s) {
        return null;
    }

    @Override
    public WriteResult updateFirst(Query query, Update update, Class<?> aClass) {
        return null;
    }

    @Override
    public WriteResult updateFirst(Query query, Update update, String s) {
        return null;
    }

    @Override
    public WriteResult updateMulti(Query query, Update update, Class<?> aClass) {
        return null;
    }

    @Override
    public WriteResult updateMulti(Query query, Update update, String s) {
        return null;
    }

    @Override
    public void remove(Object o) {
        removeCalled++;
    }

    @Override
    public void remove(Object o, String s) {
        removeCalled++;
    }

    @Override
    public void remove(Query query, String s) {
        removeCalled++;
    }

    @Override
    public MongoConverter getConverter() {
        return null;
    }
}
