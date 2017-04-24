package edu.arizona.kfs.fp.batch.dataaccess.impl;

import java.sql.Timestamp;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.businessobject.CreditCardVendor;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao;
import edu.arizona.kfs.sys.businessobject.BatchFileUploads;

@Transactional
public class TransactionPostingDaoOjb extends PlatformAwareDaoBaseOjb implements TransactionPostingDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(edu.arizona.kfs.fp.batch.dataaccess.impl.TransactionPostingDaoOjb.class);


    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentFileLoadDao#isDuplicateBatch(org.kuali.kfs.pdp.businessobject.CustomerProfile,
     * java.lang.Integer, java.math.BigDecimal, java.sql.Timestamp)
     */
    public boolean isDuplicateBatch(String tfileName, Timestamp batchDate, String bankTransactionBatchName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.BATCH_FILE_UPLOADS_BATCH_FILE_NAME, tfileName);
        criteria.addEqualTo(KFSPropertyConstants.BATCH_FILE_UPLOADS_BATCH_DATE, batchDate);
        criteria.addEqualTo(KFSPropertyConstants.BATCH_FILE_UPLOADS_BATCH_NAME, bankTransactionBatchName);
        return getPersistenceBrokerTemplate().getCount(new QueryByCriteria(BatchFileUploads.class, criteria)) > 0;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao#getCreditCardVendorObject(java.lang.String)
     */
    public CreditCardVendor getCreditCardVendorObject(String vendorNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER, vendorNumber);
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, "Y");
        return (CreditCardVendor) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(CreditCardVendor.class, criteria));
    }

    /**
     * @see edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao#getBankObjectByAccountNumber(java.lang.String)
     */
    public Bank getBankObjectByAccountNumber(String accountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.BANK_ACCOUNT_NUMBER, accountNumber);
        return (Bank) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Bank.class, criteria));
    }

    /**
     * @see edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao#isExisting(java.lang.String)
     */
    public boolean isExisting(String accountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.BANK_ACCOUNT_NUMBER, accountNumber);
        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(Bank.class, criteria)) > 0;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao#getObjectTpeCodeByObjectCode(java.lang.String)
     */
    public ObjectCode getObjectTpeCodeByObjectCode(String financialObjectTypeCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectTypeCode);
        ObjectCode objectCode = (ObjectCode) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(ObjectCode.class, criteria));
        return objectCode;

    }

    /**
     * @see edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao#getBankObjectByBankCode(java.lang.String)
     */
    public Bank getBankObjectByBankCode(String bankCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.BANK_CODE, bankCode);
        return (Bank) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Bank.class, criteria));
    }


}
