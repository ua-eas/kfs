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
package org.kuali.kfs.sys.document.web.renderers;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLinesTag;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.List;

public class AccountingLineTableHeaderRendererTest {
    public AccountingLineTableHeaderRenderer accountingLineTableHeaderRenderer;
    private PageContext pageContext;
    private AccountingLinesTag parentTag;
    private JspWriter jspWriter;
    private AccountingDocument accountingDocument;

    @Before
    public void setUp() {
        pageContext = EasyMock.createMock(PageContext.class);
        parentTag = EasyMock.createMock(AccountingLinesTag.class);
        jspWriter = EasyMock.createMock(JspWriter.class);
        accountingDocument = EasyMock.createMock(AccountingDocument.class);

        accountingLineTableHeaderRenderer = new AccountingLineTableHeaderRenderer();
    }

    private void replayAll() {
        EasyMock.replay(pageContext);
        EasyMock.replay(parentTag);
        EasyMock.replay(jspWriter);
        EasyMock.replay(accountingDocument);
    }

    @Test
    public void testGoodSequenceNumber() throws Exception {
        EasyMock.expect(parentTag.getDocument()).andReturn(accountingDocument).times(2);
        EasyMock.expect(accountingDocument.getSourceAccountingLines()).andReturn(getLines(3));
        EasyMock.expect(accountingDocument.getTargetAccountingLines()).andReturn(getLines(3));
        EasyMock.expect(pageContext.getOut()).andReturn(jspWriter);
        jspWriter.write("<div class=\"tab-container\" align=\"center\">\n");
        jspWriter.write("<table class=\"datatable standard acct-lines\">\n");

        replayAll();

        accountingLineTableHeaderRenderer.render(pageContext, parentTag);
    }

    @Test
    public void testBigSequenceNumber() throws Exception {
        EasyMock.expect(parentTag.getDocument()).andReturn(accountingDocument).times(2);
        EasyMock.expect(accountingDocument.getSourceAccountingLines()).andReturn(getLines(3));
        EasyMock.expect(accountingDocument.getTargetAccountingLines()).andReturn(getLines(3000));
        EasyMock.expect(pageContext.getOut()).andReturn(jspWriter);
        jspWriter.write("<div class=\"tab-container\" align=\"center\">\n");
        jspWriter.write("<table class=\"datatable standard acct-lines large-seq\">\n");

        replayAll();

        accountingLineTableHeaderRenderer.render(pageContext, parentTag);
    }

    @Test
    public void testNoLines() throws Exception {
        EasyMock.expect(parentTag.getDocument()).andReturn(accountingDocument).times(2);
        EasyMock.expect(accountingDocument.getSourceAccountingLines()).andReturn(new ArrayList());
        EasyMock.expect(accountingDocument.getTargetAccountingLines()).andReturn(new ArrayList());
        EasyMock.expect(pageContext.getOut()).andReturn(jspWriter);
        jspWriter.write("<div class=\"tab-container\" align=\"center\">\n");
        jspWriter.write("<table class=\"datatable standard acct-lines\">\n");

        replayAll();

        accountingLineTableHeaderRenderer.render(pageContext, parentTag);
    }

    @Test
    public void testNullSequenceNumber() throws Exception {
        EasyMock.expect(parentTag.getDocument()).andReturn(accountingDocument).times(2);
        EasyMock.expect(accountingDocument.getSourceAccountingLines()).andReturn(getLines(3));
        EasyMock.expect(accountingDocument.getTargetAccountingLines()).andReturn(getLines(null));
        EasyMock.expect(pageContext.getOut()).andReturn(jspWriter);
        jspWriter.write("<div class=\"tab-container\" align=\"center\">\n");
        jspWriter.write("<table class=\"datatable standard acct-lines\">\n");

        replayAll();

        try {
            accountingLineTableHeaderRenderer.render(pageContext, parentTag);
        } catch (NullPointerException e) {
            Assert.fail("Error handling null sequence numbers in decideTableClass()");
            e.printStackTrace();
        }
    }

    private List<AccountingLine> getLines(Integer maxSequence) {
        List<AccountingLine> targetLines = new ArrayList<>();
        targetLines.add(getSequenceAccountingLine(1));
        targetLines.add(getSequenceAccountingLine(2));
        targetLines.add(getSequenceAccountingLine(maxSequence));


        return targetLines;
    }

    private AccountingLine getSequenceAccountingLine(Integer sequenceNumber) {
        AccountingLine accountingLine = new AccountingLineBase() {
        };
        accountingLine.setSequenceNumber(sequenceNumber);
        return accountingLine;
    }
}
