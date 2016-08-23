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
package org.kuali.kfs.pdp.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.pdp.PdpConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AutoCheckFormat extends PersistableBusinessObjectBase {


    private static final long serialVersionUID = 6222944228281142059L;
    private String campus;
    private String paymentDate;
    private String paymentTypes;

    private FormatProcessSummary formatProcessSummary;

    private List<CustomerProfile> customers;
    private List<DisbursementNumberRange> ranges;

    public AutoCheckFormat() {
        this.paymentTypes = PdpConstants.PaymentTypes.ALL;
        customers = new ArrayList<CustomerProfile>();
        ranges = new ArrayList<DisbursementNumberRange>();
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(String paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public List<CustomerProfile> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerProfile> customers) {
        this.customers = customers;
    }

    public List<DisbursementNumberRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<DisbursementNumberRange> ranges) {
        this.ranges = ranges;
    }

    public FormatProcessSummary getFormatProcessSummary() {
        return formatProcessSummary;
    }

    public void setFormatProcessSummary(FormatProcessSummary formatProcessSummary) {
        this.formatProcessSummary = formatProcessSummary;
    }

    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

}
