package org.kuali.kra.external.locfund;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "letterOfCreditFundDto", propOrder = {
        "fundCode",
        "description",
        "startDate",
        "expirationDate",
        "amount",
        "groupCode",
        "fundGroup",
        "active"
})
public class LetterOfCreditFundDto implements Serializable {

    private String fundCode;
    private String description;
    private Date startDate;
    private Date expirationDate;
    private boolean active;
    private BigDecimal amount;
    private String groupCode;
    private LetterOfCreditFundGroupDto fundGroup;


    public String getFundCode() {
        return fundCode;
    }
    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getGroupCode() {
        return groupCode;
    }
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    public LetterOfCreditFundGroupDto getFundGroup() {
        return fundGroup;
    }
    public void setFundGroup(LetterOfCreditFundGroupDto fundGroup) {
        this.fundGroup = fundGroup;
    }

}
