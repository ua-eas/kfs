package org.kuali.kfs.integration.ar.businessobject;

import org.kuali.kfs.integration.ar.AccountsReceivableBillingFrequency;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

public class BillingFrequency implements AccountsReceivableBillingFrequency, MutableInactivatable {
    private String frequency;
    private String frequencyDescription;
    private Integer gracePeriodDays;
    private boolean active;

    @Override
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public String getFrequencyDescription() {
        return frequencyDescription;
    }

    public void setFrequencyDescription(String frequencyDescription) {
        this.frequencyDescription = frequencyDescription;
    }

    /**
     * Gets the gracePeriodDays attribute.
     *
     * @return Returns the gracePeriodDays.
     */
    @Override
    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }

    /**
     * Sets the gracePeriodDays attribute value.
     *
     * @param gracePeriodDays The gracePeriodDays to set.
     */
    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {}
}
