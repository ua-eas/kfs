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
package org.kuali.kfs.krad.uif.container;

/**
 * Special <code>Group</code> that presents a grouping on links, which can
 * also include nested groupings of links
 *
 * <p>
 * Generally this group outputs a list of <code>LinkField</code> instances, however
 * it can be configured to place separates between the fields and also delimiters
 * for the grouping
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LinkGroup extends Group {
    private static final long serialVersionUID = -4173031543626881250L;

    private String groupBeginDelimiter;
    private String groupEndDelimiter;
    private String linkSeparator;
    private String emptyLinkGroupString;

    public LinkGroup() {
        super();
    }

    /**
     * String that will be rendered before the group of links are rendered
     *
     * <p>
     * If the list of links is empty, the start delimiter will not be
     * rendered but instead the #emptyLinkGroupString will be outputted
     * </p>
     *
     * e.g. '['
     *
     * @return String group begin delimiter
     */
    public String getGroupBeginDelimiter() {
        return groupBeginDelimiter;
    }

    /**
     * Setter for the group begin delimiter
     *
     * @param groupBeginDelimiter
     */
    public void setGroupBeginDelimiter(String groupBeginDelimiter) {
        this.groupBeginDelimiter = groupBeginDelimiter;
    }

    /**
     * String that will be rendered after the group of links are rendered
     *
     * <p>
     * If the list of links is empty, the end delimiter will not be
     * rendered but instead the #emptyLinkGroupString will be outputted
     * </p>
     *
     * e.g. ']'
     *
     * @return String group end delimiter
     */
    public String getGroupEndDelimiter() {
        return groupEndDelimiter;
    }

    /**
     * Setter for the group end delimiter
     *
     * @param groupEndDelimiter
     */
    public void setGroupEndDelimiter(String groupEndDelimiter) {
        this.groupEndDelimiter = groupEndDelimiter;
    }

    /**
     * String that will be rendered between each rendered link
     *
     * e.g. '|'
     *
     * @return String link separator
     */
    public String getLinkSeparator() {
        return linkSeparator;
    }

    /**
     * Setter for the link separator
     *
     * @param linkSeparator
     */
    public void setLinkSeparator(String linkSeparator) {
        this.linkSeparator = linkSeparator;
    }

    /**
     * String that will be outputted when the list backing the
     * link group is empty
     *
     * @return String empty group string
     */
    public String getEmptyLinkGroupString() {
        return emptyLinkGroupString;
    }

    /**
     * Setter for the empty group string
     *
     * @param emptyLinkGroupString
     */
    public void setEmptyLinkGroupString(String emptyLinkGroupString) {
        this.emptyLinkGroupString = emptyLinkGroupString;
    }
}
