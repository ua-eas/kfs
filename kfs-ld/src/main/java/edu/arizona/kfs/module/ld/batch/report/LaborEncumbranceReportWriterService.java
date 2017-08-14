/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kfs.module.ld.batch.report;

import org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl;

public class LaborEncumbranceReportWriterService extends ReportWriterTextServiceImpl {

    protected boolean modeError = false; 
    
    public void writeErrorLine(String message) {
        // header is only written if it hasn't been written before
        if (!modeError) {
            modeError = true;

            // If nothing has been written to the report we don't want to page break
            if (!(page == initialPageNumber && line == INITIAL_LINE_NUMBER + 2)) {
                pageBreak();
            }

            writeFormattedMessageLine("*********************************************************************************************************************************");
            writeFormattedMessageLine("*********************************************************************************************************************************");
            writeFormattedMessageLine("*******************" + " E R R O R   M E S S A G E S " + "*******************");
            writeFormattedMessageLine("*********************************************************************************************************************************");
            writeFormattedMessageLine("*********************************************************************************************************************************");
        }

        // these sometimes have linefeeds in them, split so as to not mess up the line count
        for ( String line : message.split("\n") ) {
            writeFormattedMessageLine(line);
        }
        writeNewLines(1); // separator
    }
    
}
