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
package org.kuali.kfs.sys.document.web;

import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.renderers.ActionsRenderer;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.util.List;

/**
 * A field that can join tables and also be rendered, this represents a table cell
 * that displays the actions available on an accounting line
 */
public class AccountingLineViewActionsField extends FieldTableJoiningWithHeader {
    private String name = KFSConstants.AccountingLineViewStandardBlockNames.ACTION_BLOCK;

    /**
     * Returns the name of this actions field
     *
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this actions field
     *
     * @param name the name of this block
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * We are an action block.  For real, even
     *
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#isActionBlock()
     */
    @Override
    public boolean isActionBlock() {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.FieldTableJoiningWithHeader#joinTable(java.util.List)
     */
    @Override
    public void joinTable(List<AccountingLineTableRow> rows) {
        AccountingLineTableCell headerCell = createHeaderLabelTableCell();
        rows.get(0).addCell(headerCell);

        final int cellRowSpan = rows.size() - 1;
        if (cellRowSpan > 0) {
            AccountingLineTableCell cell = createTableCell();
            cell.setRowSpan(cellRowSpan);
            if (cellRowSpan > 1) {
                cell.setStyleClassOverride("multi-row");
            }
            rows.get(1).addCell(cell);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        ActionsRenderer renderer = new ActionsRenderer();
        renderer.setActions(renderingContext.getActionsForLine());
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return new LiteralHeaderLabel(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_ACTIONS_LABEL));
    }

    /**
     * This doesn't hold a field, so this implementation does nothing
     *
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFields(java.util.List)
     * <p>
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
    }

    /**
     * Doesn't do anything
     *
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
    }

}
