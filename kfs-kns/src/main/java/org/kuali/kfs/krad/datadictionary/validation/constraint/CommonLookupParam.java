/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.kuali.rice.core.api.data.DataType;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class is a direct copy of one that was in Kuali Student. Look up constraints are currently not implemented.
 *
 * @since 1.1
 */
public class CommonLookupParam implements Serializable {


    private static final long serialVersionUID = 1L;
    private String key;
    private CommonLookup childLookup;
    private WriteAccess writeAccess;
    private DataType dataType;
    private boolean optional;

    private ArrayList<String> defaultValueList;
    private String defaultValueString;

    private String name;
    private String desc;
    private boolean caseSensitive;
    private Usage usage;
    protected String fieldPath;


    public enum Widget {
        SUGGEST_BOX, DROPDOWN_LIST, RADIO_BUTTONS, CHECK_BOXES, TEXT_BOX, CALENDAR, PICKER
    }

    public enum WriteAccess {
        ON_CREATE, /* must also be required */
        ALWAYS, NEVER, WHEN_NULL, REQUIRED
    }

    public enum Usage {
        DEFAULT, ADVANCED, CUSTOM, ADVANCED_CUSTOM
    }

    private Widget widget;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CommonLookup getChildLookup() {
        return childLookup;
    }

    public void setChildLookup(CommonLookup childLookup) {
        this.childLookup = childLookup;
    }

    public WriteAccess getWriteAccess() {
        return writeAccess;
    }

    public void setWriteAccess(WriteAccess writeAccess) {
        this.writeAccess = writeAccess;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public ArrayList<String> getDefaultValueList() {
        return defaultValueList;
    }

    public void setDefaultValueList(ArrayList<String> defaultValueList) {
        this.defaultValueList = defaultValueList;
    }

    public String getDefaultValueString() {
        return defaultValueString;
    }

    public void setDefaultValueString(String defaultValueString) {
        this.defaultValueString = defaultValueString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
