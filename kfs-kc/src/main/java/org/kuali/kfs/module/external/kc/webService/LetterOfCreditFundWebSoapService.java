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
package org.kuali.kfs.module.external.kc.webService;


import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kra.external.locfund.LetterOfCreditFundWebService;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;

@WebServiceClient(name = KcConstants.LetterOfCreditFund.SOAP_SERVICE_NAME,
        wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/letterOfCreditFundWebService?wsdl",
        targetNamespace = KcConstants.KC_NAMESPACE_URI)
public class LetterOfCreditFundWebSoapService extends KfsKcSoapService {

    public final static QName LetterOfCreditFundWebServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.LetterOfCreditFund.SERVICE_PORT);
    static {
        try {
            getWsdl(KcConstants.LetterOfCreditFund.SERVICE);
        } catch (MalformedURLException e) {
            LOG.warn("Can not initialize the wsdl");
        }
    }

    public LetterOfCreditFundWebSoapService() throws MalformedURLException {
        super(getWsdl(KcConstants.LetterOfCreditFund.SERVICE), KcConstants.LetterOfCreditFund.SERVICE);
    }

    @WebEndpoint(name = KcConstants.LetterOfCreditFund.SERVICE_PORT)
    public LetterOfCreditFundWebService getLetterOfCreditFundWebServicePort() {
        return super.getPort(LetterOfCreditFundWebServicePort, LetterOfCreditFundWebService.class);
    }

    @WebEndpoint(name = KcConstants.LetterOfCreditFund.SERVICE_PORT)
    public LetterOfCreditFundWebService getLetterOfCreditFundWebServicePort(WebServiceFeature... features) {
        return super.getPort(LetterOfCreditFundWebServicePort, LetterOfCreditFundWebService.class, features);
    }

    @Override
    public URL getWsdl() throws MalformedURLException {
        return super.getWsdl(KcConstants.LetterOfCreditFund.SERVICE);
    }
}
