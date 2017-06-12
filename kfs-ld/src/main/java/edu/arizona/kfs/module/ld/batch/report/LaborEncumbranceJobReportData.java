package edu.arizona.kfs.module.ld.batch.report;

import java.util.LinkedList;

public class LaborEncumbranceJobReportData {

    private int errorCount = 0;
    private int inputLinesProcessed = 0;
    private int balanceLinesProcessed = 0;
    private int newBalanceCount = 0;
    private int removedBalanceCount = 0;
    private int increasedBalanceCount = 0;
    private int decreasedBalanceCount = 0;
    private int matchingBalanceCount = 0;
    private int outputCount = 0;
    
    private LinkedList<String> errorMessages = new LinkedList<String>();

    public void incrementErrorCount() {
		errorCount++;
	}

	public void incrementInputLinesProcessed() {
		inputLinesProcessed++;
	}

	public void incrementBalanceLinesProcessed() {
		 balanceLinesProcessed++;
	}

	public void incrementNewBalanceCount() {
		 newBalanceCount++;
	}

	public void incrementRemovedBalanceCount() {
		 removedBalanceCount++;
	}

	public void incrementIncreasedBalanceCount() {
		 increasedBalanceCount++;
	}

	public void incrementDecreasedBalanceCount() {
		 decreasedBalanceCount++;
	}

	public void incrementMatchingBalanceCount() {
		 matchingBalanceCount++;
	}

	public void incrementOutputCount() {
		 outputCount++;
	} 
	
	public int getErrorCount() {
		return errorCount;
	}

	public int getInputLinesProcessed() {
		return inputLinesProcessed;
	}

	public int getBalanceLinesProcessed() {
		return balanceLinesProcessed;
	}

	public int getNewBalanceCount() {
		return newBalanceCount;
	}

	public int getRemovedBalanceCount() {
		return removedBalanceCount;
	}

	public int getIncreasedBalanceCount() {
		return increasedBalanceCount;
	}

	public int getDecreasedBalanceCount() {
		return decreasedBalanceCount;
	}

	public int getMatchingBalanceCount() {
		return matchingBalanceCount;
	}

	public int getOutputCount() {
		return outputCount;
	}

	public LinkedList<String> getErrorMessages() {
		return errorMessages;
	}   
	
	public void addErrorMessage(String errorMsg) {
		errorMessages.add(errorMsg);
	}  
}
