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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LaborEncumbranceJobReportData {

    public int errorCount = 0;
    public int inputLinesProcessed = 0;
    public int balanceLinesProcessed = 0;
    public int newBalanceCount = 0;
    public int removedBalanceCount = 0;
    public int increasedBalanceCount = 0;
    public int decreasedBalanceCount = 0;
    public int matchingBalanceCount = 0;
    public int outputCount = 0;
    
    public LinkedList<String> errorMessages = new LinkedList<String>();    
}
