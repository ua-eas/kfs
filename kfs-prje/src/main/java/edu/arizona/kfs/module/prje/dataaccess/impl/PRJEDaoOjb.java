package edu.arizona.kfs.module.prje.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.module.prje.businessobject.PRJESet;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;
import edu.arizona.kfs.module.prje.dataaccess.PRJEDao;

public class PRJEDaoOjb extends PlatformAwareDaoBaseOjb implements PRJEDao {
    public static final String ACTV_CD = "active";
    public static final String PRJE_TYPE_ID = "typeId";
    public static final String PRJE_SET_ID = "setId";
    
    public Collection<PRJEType> getPRJETypes(PRJESet set, Boolean active) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PRJE_SET_ID, set.getSetId());
        if ( active ) {
            criteria.addEqualTo(ACTV_CD, active);
        }
        QueryByCriteria qbc = new QueryByCriteria(PRJEType.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    public PRJEType getPRJEType(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PRJE_TYPE_ID, id);
        QueryByCriteria qbc = new QueryByCriteria(PRJEType.class, criteria);
        return (PRJEType) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
    
    public Collection<PRJESet> getPRJESets(Boolean active) {
        Criteria criteria = new Criteria();
        if ( active ) {
            criteria.addEqualTo(ACTV_CD, active);
        }
        QueryByCriteria qbc = new QueryByCriteria(PRJESet.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    public PRJESet getPRJESet(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PRJE_SET_ID, id);
        QueryByCriteria qbc = new QueryByCriteria(PRJESet.class, criteria);
        return (PRJESet) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
}
