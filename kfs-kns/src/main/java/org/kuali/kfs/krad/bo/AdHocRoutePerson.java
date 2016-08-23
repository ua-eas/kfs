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
package org.kuali.kfs.krad.bo;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Ad Hoc Route Person Business Object
 */
@IdClass(AdHocRoutePersonId.class)
@Entity
@Table(name = "KRNS_ADHOC_RTE_ACTN_RECIP_T")
public class AdHocRoutePerson extends AdHocRouteRecipient {
    private static final long serialVersionUID = 1L;

    @Transient
    private transient Person person;

    public AdHocRoutePerson() {
        setType(PERSON_TYPE);

        try {
            person = (Person) KimApiServiceLocator.getPersonService().getPersonImplementationClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setType(Integer type) {
        if (!PERSON_TYPE.equals(type)) {
            throw new IllegalArgumentException("cannot change type to " + type);
        }
        super.setType(type);
    }

    @Override
    public void setId(String id) {
        super.setId(id);

        if (StringUtils.isNotBlank(id)) {
            person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(id);
            setPerson(person);
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);

        if (StringUtils.isNotBlank(name) && getId() != null &&
            ((person != null) && !StringUtils.equals(person.getName(), name))) {
            person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(getId());
            setPerson(person);
        }
    }

    public Person getPerson() {
        if ((person == null) || !StringUtils.equals(person.getPrincipalName(), getId())) {
            person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(getId());

            if (person == null) {
                try {
                    person = (Person) KimApiServiceLocator.getPersonService().getPersonImplementationClass()
                        .newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        if (person != null) {
            this.id = person.getPrincipalName();
            this.name = person.getName();
        }
    }
}

