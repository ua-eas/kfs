package edu.arizona.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
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

    @Override
    public List<SourceAccountingLine> generateAccountSummary(List<PurApItem> items, Set<String> itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals, Boolean useAlternateAmount, Boolean useTaxIncluded, Boolean taxableOnly) {
        List<PurApItem> itemsToProcess = getProcessablePurapItems(items, itemTypeCodes, itemTypeCodesAreIncluded, useZeroTotals);
        Map<PurApAccountingLine, KualiDecimal> accountMap = new HashMap<PurApAccountingLine, KualiDecimal>();

        for (PurApItem currentItem : itemsToProcess) {
            if (PurApItemUtils.checkItemActive(currentItem)) {
                List<PurApAccountingLine> sourceAccountingLines = currentItem.getSourceAccountingLines();

                // skip if item is not taxable and taxable only flag has been set
                if (taxableOnly) {
                    PurchasingAccountsPayableDocument document = currentItem.getPurapDocument();
                    if (!purapService.isTaxableForSummary(document.isUseTaxIndicator(), purapService.getDeliveryState(document), currentItem)) {
                        continue;
                    }
                }

                if (!useTaxIncluded) {
                    // if no use tax set the source accounting lines to a clone so we can update them to be based on the non tax amount
                    PurApItem cloneItem = (PurApItem) ObjectUtils.deepCopy(currentItem);
                    sourceAccountingLines = cloneItem.getSourceAccountingLines();
                    // Accounting lines need to be calculated with no tax according to the percentage they were assigned and then updated.
                    calculateSourceLineWithNoTax(sourceAccountingLines, currentItem.getTotalRemitAmount());
                    updateAccountAmountsWithTotal(sourceAccountingLines, currentItem.getTotalRemitAmount());
                }

                for (PurApAccountingLine account : sourceAccountingLines) {
                    // skip account if not taxable and taxable only flag is set
                    if (taxableOnly) {
                        PurchasingAccountsPayableDocument document = currentItem.getPurapDocument();
                        // check if account is not taxable, if not skip this account
                        if (!purapService.isAccountingLineTaxable(account, purapService.isDeliveryStateTaxable(purapService.getDeliveryState(document)))) {
                            continue;
                        }
                    }

                    // getting the total to set on the account
                    KualiDecimal total = KualiDecimal.ZERO;
                    if (accountMap.containsKey(account)) {
                        total = accountMap.get(account);
                    }

                    if (useAlternateAmount) {
                        total = total.add(account.getAlternateAmountForGLEntryCreation());
                    } else {
                        if (ObjectUtils.isNotNull(account.getAmount())) {
                            total = total.add(account.getAmount());
                        }
                    }

                    accountMap.put(account, total);
                }
            }
        }

        Integer currentFiscalYear = SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getUniversityFiscalYear();
        
        // convert list of PurApAccountingLine objects to SourceAccountingLineObjects
        Iterator<PurApAccountingLine> iterator = accountMap.keySet().iterator();
        List<SourceAccountingLine> sourceAccounts = new ArrayList<SourceAccountingLine>();
        for (Iterator<PurApAccountingLine> iter = iterator; iter.hasNext();) {
            PurApAccountingLine accountToConvert = iter.next();
            if (accountToConvert.isEmpty()) {
                String errorMessage = "Found an 'empty' account in summary generation " + accountToConvert.toString();
                LOG.error("generateAccountSummary() " + errorMessage);
                throw new RuntimeException(errorMessage);
            }
            KualiDecimal sourceLineTotal = accountMap.get(accountToConvert);
            SourceAccountingLine sourceLine = accountToConvert.generateSourceAccountingLine();
            sourceLine.setAmount(sourceLineTotal);
            sourceLine.setPostingYear(currentFiscalYear);
            sourceAccounts.add(sourceLine);
        }

        // sort the sourceAccounts list first by account number, then by object code, ignoring chart code
        Collections.sort(sourceAccounts, new Comparator<SourceAccountingLine>() {
            @Override
            public int compare(SourceAccountingLine sal1, SourceAccountingLine sal2) {
                int compare = 0;
                if (sal1 != null && sal2 != null) {
                    if (sal1.getAccountNumber() != null && sal2.getAccountNumber() != null) {
                        compare = sal1.getAccountNumber().compareTo(sal2.getAccountNumber());
                        if (compare == 0) {
                            if (sal1.getFinancialObjectCode() != null && sal2.getFinancialObjectCode() != null) {
                                compare = sal1.getFinancialObjectCode().compareTo(sal2.getFinancialObjectCode());
                            }
                        }
                    }
                }
                return compare;
            }
        });

        return sourceAccounts;
    }
	
    private void calculateSourceLineWithNoTax(List<PurApAccountingLine> sourceAccountingLines, KualiDecimal totalRemitAmount) {
        for (PurApAccountingLine s : sourceAccountingLines) {
            BigDecimal pct = s.getAccountLinePercent();
            BigDecimal amount = totalRemitAmount.bigDecimalValue().multiply(pct).divide(new BigDecimal(100));
            s.setAmount(new KualiDecimal(amount));
        }
    }

}
