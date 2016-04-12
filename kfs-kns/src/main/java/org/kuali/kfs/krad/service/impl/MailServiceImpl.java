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
package org.kuali.kfs.krad.service.impl;

import javax.mail.MessagingException;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.kfs.krad.util.KRADConstants;

public class MailServiceImpl implements MailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MailServiceImpl.class);

    private String batchMailingList;
    private Mailer mailer;

    private String nonProductionNotificationMailingList;
    private boolean realNotificationsEnabled = true;

    /**
     * The injected Mailer.
     */
    public void setMailer(Mailer mailer) {
    	this.mailer = mailer;
    }
    
    /**
     * 
     */
    public MailServiceImpl() {
        super();
    }

    /**
     * Sets the batchMailingList attribute value.
     * @param batchMailingList The batchMailingList to set.
     */
    public void setBatchMailingList(String batchMailingList) {
        this.batchMailingList = batchMailingList;
    }

    /**
     * @see MailService#getBatchMailingList()
     */
    public String getBatchMailingList() {
        return batchMailingList;
    }

	/**
	 * This overridden method ...
	 * @throws MessagingException 
	 * 
	 * @see MailService#sendMessage(org.kuali.rice.core.api.mail.MailMessage)
	 */
	@Override
	public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException {
		mailer.sendEmail(composeMessage(message));		
	}
	
    protected MailMessage composeMessage(MailMessage message){

        MailMessage mm = new MailMessage();

        // If realNotificationsEnabled is false, mails will be sent to nonProductionNotificationMailingList
        if(!isRealNotificationsEnabled()){
            getNonProductionMessage(message);
        }

        String app = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID);
        String env = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
        
        mm.setToAddresses(message.getToAddresses());
        mm.setBccAddresses(message.getBccAddresses());
        mm.setCcAddresses(message.getCcAddresses());
        mm.setSubject(app + " " + env + ": " + message.getSubject());
        mm.setMessage(message.getMessage());
        mm.setFromAddress(message.getFromAddress());
        return mm;
    }

    public String getNonProductionNotificationMailingList() {
        return this.nonProductionNotificationMailingList;
    }

    /**
     * @param nonProductionNotificationMailingList the nonProductionNotificationMailingList to set
     */
    public void setNonProductionNotificationMailingList(
            String nonProductionNotificationMailingList) {
        this.nonProductionNotificationMailingList = nonProductionNotificationMailingList;
    }

    /**
     * @return the realNotificationsEnabled
     */
    public boolean isRealNotificationsEnabled() {
        return this.realNotificationsEnabled;
    }

    /**
     * @param realNotificationsEnabled the realNotificationsEnabled to set
     */
    public void setRealNotificationsEnabled(boolean realNotificationsEnabled) {
        this.realNotificationsEnabled = realNotificationsEnabled;
    }

    protected MailMessage getNonProductionMessage(MailMessage message){
        StringBuilder buf = new StringBuilder();
        buf.append("Email To: ").append(message.getToAddresses()).append("\n");
        buf.append("Email CC: ").append(message.getCcAddresses()).append("\n");
        buf.append("Email BCC: ").append(message.getBccAddresses()).append("\n\n");
        buf.append(message.getMessage());

        message.getToAddresses().clear();
        //Note: If the non production notification mailing list is blank, sending this message will throw an exception
        message.addToAddress(getNonProductionNotificationMailingList());
        message.getBccAddresses().clear();
        message.getCcAddresses().clear();
        message.setMessage(buf.toString());

        return message;
    }
}
