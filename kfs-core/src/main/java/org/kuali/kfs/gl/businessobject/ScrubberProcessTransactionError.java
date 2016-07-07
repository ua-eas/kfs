package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.sys.Message;

/**
 * A class to hold errors encountered by the scrubber
 */
public class ScrubberProcessTransactionError {

    protected Transaction transaction;
    protected Message message;

    /**
     * Constructs a ScrubberProcessTransactionError instance
     * @param t the transaction that had the error
     * @param m a message about the error
     */
    public ScrubberProcessTransactionError(Transaction t, Message m) {
        transaction = t;
        message = m;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
