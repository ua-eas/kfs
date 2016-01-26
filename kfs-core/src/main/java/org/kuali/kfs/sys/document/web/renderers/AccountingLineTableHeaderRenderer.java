/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web.renderers;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.web.AccountingLinesTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;

/**
 * Renders the header of an accounting line table
 */
public class AccountingLineTableHeaderRenderer implements Renderer {
    private int cellCount;
    private String accountingLineImportInstructionsUrl;


    /**
     * Constructs a AccountingLineTableHeaderRenderer, updating the tags used by this renderer to keep constant properties
     */
    public AccountingLineTableHeaderRenderer() {

    }

    /**
     * Clears out the mutable, changing qualities of this renderer so it can be repooled
     */
    public void clear() {
        cellCount = 0;
        accountingLineImportInstructionsUrl = null;
    }

    /**
     * Renders the header for the accounting line table to the screen
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(buildDivStart());
            out.write(buildTableStart(decideTableClass(parentTag)));
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering AccountingLineTableHeader", ioe);
        }
    }

    protected String decideTableClass(Tag parentTag) {
        String tableClass = null;
        if (parentTag instanceof AccountingLinesTag) {
            int sourceSize = ((AccountingLinesTag) parentTag).getDocument().getSourceAccountingLines().size();
            int targetSize = ((AccountingLinesTag) parentTag).getDocument().getTargetAccountingLines().size();
            if (sourceSize > 99 || targetSize > 99) {
                tableClass = "large-seq";
            }
        }
        return tableClass;
    }
    
    /**
     * Builds the beginning of the tab-container div
     * @return the beginning of the tab-container div in HTML
     */
    protected String buildDivStart() {
        return "<div class=\"tab-container\" align=\"center\">\n";
    }

    /**
     * Builds the very start of the table
     * @return the very start of the table expressed as HTML
     */
    protected String buildTableStart() {
        return buildTableStart(null);
    }

    protected String buildTableStart(String styleClass) {
        String tableClass = "datatable standard acct-lines";
        if (StringUtils.isNotBlank(styleClass)) {
            tableClass += " " + styleClass;
        }
        return "<table class=\"" + tableClass + "\" style=\"margin:15px; width:calc(100% - 30px);\">\n";
    }
    
    /**
     * Gets the accountingLineImportInstructionsUrl attribute. 
     * @return Returns the accountingLineImportInstructionsUrl.
     */
    public String getAccountingLineImportInstructionsUrl() {
        return accountingLineImportInstructionsUrl;
    }

    /**
     * Sets the accountingLineImportInstructionsUrl attribute value.
     * @param accountingLineImportInstructionsUrl The accountingLineImportInstructionsUrl to set.
     */
    public void setAccountingLineImportInstructionsUrl(String accountingLineImportInstructionsUrl) {
        this.accountingLineImportInstructionsUrl = accountingLineImportInstructionsUrl;
    }

    /**
     * Gets the cellCount attribute. 
     * @return Returns the cellCount.
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     * Sets the cellCount attribute value.
     * @param cellCount The cellCount to set.
     */
    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }


}
