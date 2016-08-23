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
package org.kuali.kfs.krad.datadictionary.validation;

import java.util.List;

/**
 *
 *
 */
public class Company {

    private String id;
    private String name;
    private List<Address> locations;

    private Employee mainContact;
    private Address mainAddress;
    private List<Employee> employees;
    private List<String> slogans;


    public Company() {
    }


    /**
     * @return the id
     */

    public String getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    public Company(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the addresses
     */
    public List<Address> getLocations() {
        return this.locations;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setLocations(List<Address> addresses) {
        this.locations = addresses;
    }

    /**
     * @return the address
     */
    public Address getMainAddress() {
        return this.mainAddress;
    }

    /**
     * @param address the address to set
     */
    public void setMainAddress(Address address) {
        this.mainAddress = address;
    }

    /**
     * @return the employee
     */
    public List<Employee> getEmployees() {
        return this.employees;
    }

    /**
     * @param employee the employee to set
     */
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }


    /**
     * @return the mainContact
     */
    public Employee getMainContact() {
        return this.mainContact;
    }


    /**
     * @param mainContact the mainContact to set
     */
    public void setMainContact(Employee mainContact) {
        this.mainContact = mainContact;
    }


    /**
     * @return the slogans
     */
    public List<String> getSlogans() {
        return this.slogans;
    }


    /**
     * @param slogans the slogans to set
     */
    public void setSlogans(List<String> slogans) {
        this.slogans = slogans;
    }

}
