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
package org.kuali.kfs.kns.question;

import java.util.ArrayList;

/**
 * Recall to Action List / Recall & Cancel question for Recall functionality.
 * Note: this could possibly be generalized to a generic MultipleChoiceQuestion in combination w/ special question context
 */
public class RecallQuestion extends QuestionBase {
    public static final String RECALL_TO_ACTIONLIST = "0";
    public static final String RECALL_AND_CANCEL = "1";

    public RecallQuestion() {
        // this should be set by question form
        super("Return document to Action List or Cancel document?", new ArrayList(2));
        this.buttons.add("Recall to Action List");
        this.buttons.add("Recall and Cancel");
    }
}
