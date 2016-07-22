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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Must occur constraints are constraints that indicate some range of acceptable valid results. So a must occur constraint
 * might indicate that between 1 and 3 prequisite constraints must be valid. For example, on a person object, it might be
 * that one of three fields must be filled in:
 * 
 * 1. username
 * 2. email
 * 3. phone number
 * 
 * By imposing a must occur constraint on the person object iself, and setting three prequisite constraints below it, with a min of 1 
 * and a max of 3, this requirement can be enforced. 
 * 
 * A more complicated example might be that a US address is only valid if it provides either:
 * (a) a city and state, or
 * (b) a postal code
 * 
 * To enforce this, a single must occur constraint would have two children: (1) a prequisite constraint on postal code, and (2) a must occur constraint
 * with two child prequisite constraints, on city and state, respectively. By setting min=1/max=2 at the top must occur constraint, 
 * and min=2/max=2 at the leaf constraint, this requirement can be enforced.
 * 
 * 
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MustOccurConstraint extends BaseConstraint {
	
    @XmlElement
    private List<PrerequisiteConstraint> prerequisiteConstraints;
	@XmlElement
    private List<MustOccurConstraint> mustOccurConstraints;
	@XmlElement
	private Integer min;
	@XmlElement
	private Integer max;

	public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
		return prerequisiteConstraints;
	}

	public void setPrerequisiteConstraints(List<PrerequisiteConstraint> prerequisiteConstraints) {
		this.prerequisiteConstraints = prerequisiteConstraints;
	}

	public List<MustOccurConstraint> getMustOccurConstraints() {
		return mustOccurConstraints;
	}

	public void setMustOccurConstraints(List<MustOccurConstraint> occurs) {
		this.mustOccurConstraints = occurs;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

    @Override
    public String getLabelKey(){
        if(StringUtils.isBlank(this.labelKey)){
            return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "mustoccursFallback";
        }
        else{
            return super.getLabelKey();
        }
    }
}
