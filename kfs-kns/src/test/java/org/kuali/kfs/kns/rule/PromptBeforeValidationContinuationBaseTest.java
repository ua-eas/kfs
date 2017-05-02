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
package org.kuali.kfs.kns.rule;

import org.junit.Test;
import org.kuali.kfs.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.kfs.kns.rules.PromptBeforeValidationBase;
import org.kuali.kfs.kns.rules.PromptBeforeValidationBase.ContextSession;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;

import static org.junit.Assert.assertEquals;

public class PromptBeforeValidationContinuationBaseTest {

    private class TestPreRules extends PromptBeforeValidationBase {
        @Override
        public boolean doPrompts(Document document) {
            MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
            return false;
        }

    }

    @Test
    public void test() {

        TestPreRules preRules = new TestPreRules();

        PromptBeforeValidationEvent event = new PromptBeforeValidationEvent("", "", null);

        ContextSession contextSession = preRules.new ContextSession("test", event);

        contextSession.askQuestion("q1", "this is q1");
        contextSession.setAttribute("t1", "test1");
        contextSession.setAttribute("t2", "test2");
        contextSession.setAttribute("t3", "test3");

        assertEquals("testing retrieve", "test1", contextSession.getAttribute("t1"));
        assertEquals("testing retrieve", "test2", contextSession.getAttribute("t2"));
        assertEquals("testing retrieve", "test3", contextSession.getAttribute("t3"));

    }

}
