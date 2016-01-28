package org.kuali.kfs.pdp.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class AutoCheckFormat extends PersistableBusinessObjectBase {
	
	private String campus;
    private String paymentDate;
    private String paymentTypes;

    private FormatProcessSummary formatProcessSummary;
    
    private List<CustomerProfile> customers;
    private List<DisbursementNumberRange> ranges;
    
    public AutoCheckFormat(){
    	this.campus = "IR";
    	this.paymentTypes = "all";
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

	@Override
	protected LinkedHashMap toStringMapper() {
		// TODO Auto-generated method stub
		return null;
	}

}
