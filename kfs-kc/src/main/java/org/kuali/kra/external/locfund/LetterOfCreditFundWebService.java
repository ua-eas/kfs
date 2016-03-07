package org.kuali.kra.external.locfund;

import org.kuali.kfs.module.external.kc.KcConstants;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;


@WebService(name = "letterOfCreditFundWebService", targetNamespace = KcConstants.KC_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface LetterOfCreditFundWebService {
	
	
	public List<LetterOfCreditFundDto> findMatchingFund(@WebParam(name = "fundCode") String fundCode,
														@WebParam(name = "description") String description);
	
	public List<LetterOfCreditFundDto> allLocFunds();

	
	public List<LetterOfCreditFundGroupDto> findMatchingFundGroup(@WebParam(name = "groupCode") String groupCode, @WebParam(name = "description") String description);
	
	public List<LetterOfCreditFundGroupDto> allLocFundGroups();
	
	
}
