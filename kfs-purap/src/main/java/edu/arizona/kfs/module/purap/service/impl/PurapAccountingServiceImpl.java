package edu.arizona.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.util.ObjectPopulationUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.service.PurapAccountingHelperService;
import edu.arizona.kfs.module.purap.service.PurapAccountingService;

public class PurapAccountingServiceImpl extends org.kuali.kfs.module.purap.service.impl.PurapAccountingServiceImpl implements PurapAccountingService {

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceImpl.class);
	
	protected PurapAccountingHelperService purapAccountingHelperService;
	
	public void setPurapAccountingHelperService(PurapAccountingHelperService purapAccountingHelperService) {
		this.purapAccountingHelperService = purapAccountingHelperService;
	}
    
    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz, List<PurApItem> purApItems) {
        String methodName = "generateAccountDistributionForProration()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
                
        if(totalAmount.isZero() || isAllSummaryAccoountsAmountZero(accounts)) {
            return getFirstItemAccountsForDistribution(purApItems, clazz);
        } else {
            return super.generateAccountDistributionForProration(accounts, totalAmount, percentScale, clazz);
        }
    }

    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz) {
        String methodName = "generateAccountDistributionForProration()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " started");
        }
        List<PurApAccountingLine> newAccounts = new ArrayList();

        if (totalAmount.isZero()) {
            throwRuntimeException(methodName, "Purchasing/Accounts Payable account distribution for proration does not allow zero dollar total.");
        }

        BigDecimal percentTotal = BigDecimal.ZERO;
        BigDecimal totalAmountBigDecimal = totalAmount.bigDecimalValue();
        for (SourceAccountingLine accountingLine : accounts) {
            KualiDecimal amt = KualiDecimal.ZERO;
            if (ObjectUtils.isNotNull(accountingLine.getAmount())) {
                amt = accountingLine.getAmount();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " " + accountingLine.getAccountNumber() + " " + amt + "/" + totalAmountBigDecimal);
            }
            BigDecimal pct = amt.bigDecimalValue().divide(totalAmountBigDecimal, percentScale, BIG_DECIMAL_ROUNDING_MODE);
            pct = pct.stripTrailingZeros().multiply(ONE_HUNDRED);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " pct = " + pct + "  (trailing zeros removed)");
            }

            BigDecimal lowestPossible = this.getLowestPossibleRoundUpNumber();
            if (lowestPossible.compareTo(pct) <= 0) {
                PurApAccountingLine newAccountingLine;
                newAccountingLine = null;

                try {
                    newAccountingLine = (PurApAccountingLine) clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                ObjectPopulationUtils.populateFromBaseClass(AccountingLineBase.class, accountingLine, newAccountingLine, PurapConstants.KNOWN_UNCOPYABLE_FIELDS);
                newAccountingLine.setAccountLinePercent(pct);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
                }
                newAccounts.add(newAccountingLine);
                percentTotal = percentTotal.add(newAccountingLine.getAccountLinePercent());
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName + " total = " + percentTotal);
                }
            }
        }

        if ((percentTotal.compareTo(BigDecimal.ZERO)) == 0) {
            /*
             * This means there are so many accounts or so strange a distribution that we can't round properly... not sure of viable
             * solution
             */
            throwRuntimeException(methodName, "Can't round properly due to number of accounts");
        }

        // Now deal with rounding
        if ((ONE_HUNDRED.compareTo(percentTotal)) < 0) {
            /*
             * The total percent is greater than one hundred Here we find the account that occurs latest in our list with a percent
             * that is higher than the difference and we subtract off the difference
             */
            BigDecimal difference = percentTotal.subtract(ONE_HUNDRED);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " Rounding up by " + difference);
            }

            boolean foundAccountToUse = false;
            int currentNbr = newAccounts.size() - 1;
            while (currentNbr >= 0) {
                PurApAccountingLine potentialSlushAccount = newAccounts.get(currentNbr);

                BigDecimal linePercent = BigDecimal.ZERO;
                if (ObjectUtils.isNotNull(potentialSlushAccount.getAccountLinePercent())) {
                    linePercent = potentialSlushAccount.getAccountLinePercent();
                }

                if ((difference.compareTo(linePercent)) < 0) {
                    // the difference amount is less than the current accounts percent... use this account
                    // the 'potentialSlushAccount' technically is now the true 'Slush Account'
                    potentialSlushAccount.setAccountLinePercent(linePercent.subtract(difference).movePointLeft(2).stripTrailingZeros().movePointRight(2));
                    foundAccountToUse = true;
                    break;
                }
                currentNbr--;
            }

            if (!foundAccountToUse) {
                /*
                 * We could not find any account in our list where the percent of that account was greater than that of the
                 * difference... doing so on just any account could result in a negative percent value
                 */
                throwRuntimeException(methodName, "Can't round properly due to math calculation error");
            }

        } else if ((ONE_HUNDRED.compareTo(percentTotal)) > 0) {
            /*
             * The total percent is less than one hundred Here we find the last account in our list and add the remaining required
             * percent to its already calculated percent
             */
            BigDecimal difference = ONE_HUNDRED.subtract(percentTotal);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName + " Rounding down by " + difference);
            }
            PurApAccountingLine slushAccount = newAccounts.get(newAccounts.size() - 1);

            BigDecimal slushLinePercent = BigDecimal.ZERO;
            if (ObjectUtils.isNotNull(slushAccount.getAccountLinePercent())) {
                slushLinePercent = slushAccount.getAccountLinePercent();
            }

            slushAccount.setAccountLinePercent(slushLinePercent.add(difference).movePointLeft(2).stripTrailingZeros().movePointRight(2));
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + " ended");
        }
        return newAccounts;
    }

    /*
     * Check if all the summary accounts amount are zero. If all are zero, it will cause exception when creating
     * distribution account.
     */
    private boolean isAllSummaryAccoountsAmountZero(List<SourceAccountingLine> summaryAccounts) {
    	boolean isZero = true;
    	for (SourceAccountingLine account : summaryAccounts) {
    		isZero = account.getAmount().isZero();
    		if (!isZero) {
    			break;
    		}
    	}
    	return isZero;
    	
    }

    /*
     * get first line item's accounts as distribution accounts
     */
    private List<PurApAccountingLine> getFirstItemAccountsForDistribution(List<PurApItem> purapItems, Class clazz) {
        List<PurApAccountingLine> newAccounts = new ArrayList<PurApAccountingLine>();
    	
        for (PurApItem item : purapItems) {
            if (item.getItemType().isLineItemIndicator()) {
            	for (PurApAccountingLine account : item.getSourceAccountingLines()) {
                    PurApAccountingLine newAccountingLine = null;
                    try {
                        newAccountingLine = (PurApAccountingLine) clazz.newInstance();
                        ObjectPopulationUtils.populateFromBaseClass(AccountingLineBase.class, account, newAccountingLine, PurapConstants.KNOWN_UNCOPYABLE_FIELDS);
                        newAccountingLine.setAccountLinePercent(account.getAccountLinePercent());
                        newAccounts.add(newAccountingLine);
                    }
                    catch (Exception e) {
                        LOG.error("getFirstItemAccountsForDistribution() : Instantiate Purap Account error " + e.getMessage());
                    }
                }
            	if (CollectionUtils.isNotEmpty(newAccounts)) {
            		break;
            	}
            }
       } 
        return newAccounts;
    }

    @Override
    public List<UseTaxContainer> generateUseTaxAccount(PurchasingAccountsPayableDocument document) {
        List<UseTaxContainer> useTaxAccounts = new ArrayList<UseTaxContainer>();

        HashMap<PurApItemUseTax, UseTaxContainer> useTaxItemMap = new HashMap<PurApItemUseTax, UseTaxContainer>();
        Class accountingLineClass = null;
        if (!document.isUseTaxIndicator()) {
            // not useTax, return
            return useTaxAccounts;
        }
        for (PurApItem purApItem : document.getItems()) {
            if (!purApItem.getUseTaxItems().isEmpty()) {
                if (accountingLineClass == null) {
                    accountingLineClass = purApItem.getAccountingLineClass();
                }
                UseTaxContainer useTaxContainer = new UseTaxContainer();
                for (PurApItemUseTax itemUseTax : purApItem.getUseTaxItems()) {
                    PurApItemUseTax itemUseTaxCopy = (PurApItemUseTax)ObjectUtils.deepCopy(itemUseTax);
                    if (useTaxItemMap.containsKey(itemUseTaxCopy)) {
                        useTaxContainer = useTaxItemMap.get(itemUseTaxCopy);
                        PurApItemUseTax exisitingItemUseTax = useTaxContainer.getUseTax();
                        
                        KualiDecimal tax = exisitingItemUseTax.getTaxAmount();
                        tax = tax.add(itemUseTaxCopy.getTaxAmount());
                        exisitingItemUseTax.setTaxAmount(tax);

                        List<PurApItem> items = useTaxContainer.getItems();
                        items.add(purApItem);
                        useTaxContainer.setItems(items);

                    }
                    else {
                        useTaxContainer = new UseTaxContainer(itemUseTaxCopy, purApItem);
                        useTaxItemMap.put(itemUseTaxCopy, useTaxContainer);
                        useTaxAccounts.add(useTaxContainer);
                    }
                }
            }
        }
        // iterate over useTaxAccounts and set summary accounts using proration
        for (UseTaxContainer useTaxContainer : useTaxAccounts) {

            // create summary from items
            List<SourceAccountingLine> origSourceAccounts = this.generateSummaryWithNoZeroTotals(useTaxContainer.getItems());
            KualiDecimal totalAmount = calculateSumTotal(origSourceAccounts);
            // UAF-3794 only this statement changed
            List<PurApAccountingLine> accountingLines = generateAccountDistributionForProration(origSourceAccounts, totalAmount, PurapConstants.PRORATION_SCALE, accountingLineClass, document.getItems());


            List<SourceAccountingLine> newSourceLines = new ArrayList<SourceAccountingLine>();
            // convert back to source
            convertAmtToTax(accountingLines, useTaxContainer.getUseTax().getTaxAmount(), newSourceLines);

            // do we need an update accounts here?
            useTaxContainer.setAccounts(newSourceLines);
        }

        useTaxAccounts = new ArrayList<UseTaxContainer>(useTaxItemMap.values());
        return useTaxAccounts;
    }

    @Override
    public List<SourceAccountingLine> generateAccountSummary(List<PurApItem> items, Set<String> itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals, Boolean useAlternateAmount, Boolean useTaxIncluded, Boolean taxableOnly) {
        return super.generateAccountSummary(items, itemTypeCodes, itemTypeCodesAreIncluded, useZeroTotals, useAlternateAmount, useTaxIncluded, taxableOnly);
    }
	
    @Override
    public List<SourceAccountingLine> generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document) {
        updateAccountAmounts(document);
        List<SourceAccountingLine> vendorSummaryAccounts = new ArrayList<SourceAccountingLine>();

        if (document instanceof PaymentRequestDocument) {
            vendorSummaryAccounts = purapAccountingHelperService.generateSummaryWithNoZeroTotalsNoUseTaxNoWithholding(((PaymentRequestDocument) document).getItemsSetupAlternateAmount());
        } else {
            vendorSummaryAccounts = generateSummaryWithNoZeroTotalsNoUseTax(document.getItems());
        }
        
        return vendorSummaryAccounts;
    }
    
    /**
     * calculates values for a list of accounting lines based on an amount taking discount into account
     *
     * @param sourceAccountingLines
     * @param totalAmount
     * @param discountAmount
     */
    @Override
    public <T extends PurApAccountingLine> void updateAccountAmountsWithTotal(List<T> sourceAccountingLines, KualiDecimal totalAmount, KualiDecimal discountAmount) {
        // if we have a discount, then we need to base the amounts on the discount, but the percent on the total
        boolean noDiscount = true;
        if ((discountAmount != null) && KualiDecimal.ZERO.compareTo(discountAmount) != 0) {
            noDiscount = false;
        }

        if ((totalAmount != null) && KualiDecimal.ZERO.compareTo(totalAmount) != 0) {
            KualiDecimal accountTotal = KualiDecimal.ZERO;
            T lastAccount = null;

            for (T account : sourceAccountingLines) {
                if (ObjectUtils.isNotNull(account.getAccountLinePercent()) || ObjectUtils.isNotNull(account.getAmount())) {
                    BigDecimal pct = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
                    if (noDiscount) {
                        if (ObjectUtils.isNull(account.getAmount()) || account.getAmount().isZero()) {
                            account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(totalAmount.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
                        }
                    }
                    else {
                        account.setAmount(new KualiDecimal(pct.multiply(new BigDecimal(discountAmount.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
                    }
                }
                
                accountTotal = accountTotal.add(account.getAmount());

                lastAccount = account;
            }

            // put excess on last account
            if (lastAccount != null) {
                KualiDecimal difference = new KualiDecimal(0);
                if (noDiscount) {
                    difference = totalAmount.subtract(accountTotal);
                }
                else {
                    difference = discountAmount.subtract(accountTotal);
                }

                for (T account : sourceAccountingLines) {
                    if (ObjectUtils.isNotNull(account.getAccountLinePercent()) || ObjectUtils.isNotNull(account.getAmount())) {
                        BigDecimal percentPerAccountLine = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
                        account.setAmount(account.getAmount().add(new KualiDecimal(percentPerAccountLine.multiply(new BigDecimal(difference.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR))));
                    }
                }
            }
        }
        else {
            // zero out if extended price is zero
            for (T account : sourceAccountingLines) {
                account.setAmount(KualiDecimal.ZERO);
            }
        }
    }

}
