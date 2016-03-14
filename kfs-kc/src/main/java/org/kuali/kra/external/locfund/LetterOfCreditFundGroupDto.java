package org.kuali.kra.external.locfund;

import java.io.Serializable;

public class LetterOfCreditFundGroupDto implements Serializable {

    private String groupCode;
    private String description;
    private boolean active;


    public String getGroupCode() {
        return groupCode;
    }
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }



}
