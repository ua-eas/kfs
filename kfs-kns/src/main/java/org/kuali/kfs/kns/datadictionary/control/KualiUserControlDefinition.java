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
package org.kuali.kfs.kns.datadictionary.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
 * The kualiUser element defines a control that identifies
 * a Kuali user. As an example, consider a person with the
 * following:
 * User ID = JPJONES
 * Universal User ID = 3583663872
 * Employee ID = 0000123456
 * Name = JONES,JOHN p
 * This control defines a field in which the user can enter the User Id or choose a
 * user using the magnifying glass lookup.  After a user is selected, user name
 * will be displayed under the User ID.
 * <p>
 * When using this control, the names of other attributes must be specified
 * to allow the control to function:
 * universalIdAttributeName  -
 * attribute that provides the Universal User Id - e.g. 3583663872
 * userIdAttributeName -
 * attribute that provides the User Id - e.g. JPJONES
 * personNameAttributeName -
 * attribute that provides the User Name - e.g. JONES,JOHN P
 */
@Deprecated
public class KualiUserControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 4749994521411547705L;

    protected String universalIdAttributeName;
    protected String userIdAttributeName;
    protected String personNameAttributeName;

    public KualiUserControlDefinition() {
    }

    /**
     * @see ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "KualiUserControlDefinition";
    }

    /**
     * Gets the personNameAttributeName attribute.
     *
     * @return Returns the personNameAttributeName.
     */
    public String getPersonNameAttributeName() {
        return personNameAttributeName;
    }

    /**
     * personNameAttributeName -
     * attribute that provides the User Name - e.g. JONES,JOHN P
     */
    public void setPersonNameAttributeName(String personNameAttributeName) {
        if (StringUtils.isBlank(personNameAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) personNameAttributeName");
        }
        this.personNameAttributeName = personNameAttributeName;
    }

    /**
     * Gets the universalIdAttributeName attribute.
     *
     * @return Returns the universalIdAttributeName.
     */
    public String getUniversalIdAttributeName() {
        return universalIdAttributeName;
    }

    /**
     * universalIdAttributeName  -
     * attribute that provides the Universal User Id - e.g. 3583663872
     */
    public void setUniversalIdAttributeName(String universalIdAttributeName) {
        if (StringUtils.isBlank(universalIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) universalIdAttributeName");
        }
        this.universalIdAttributeName = universalIdAttributeName;
    }

    /**
     * Gets the userIdAttributeName attribute.
     *
     * @return Returns the userIdAttributeName.
     */
    public String getUserIdAttributeName() {
        return userIdAttributeName;
    }

    /**
     * userIdAttributeName -
     * attribute that provides the User Id - e.g. JPJONES
     */
    public void setUserIdAttributeName(String userIdAttributeName) {
        if (StringUtils.isBlank(userIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) userIdAttributeName");
        }
        this.userIdAttributeName = userIdAttributeName;
    }

}
