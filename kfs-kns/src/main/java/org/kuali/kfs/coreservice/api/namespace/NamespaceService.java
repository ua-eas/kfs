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
package org.kuali.kfs.coreservice.api.namespace;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@WebService(name = "namespaceService", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface NamespaceService {

    /**
     * Gets a {@link Namespace} from a namespace code.
     *
     * <p>
     *   This method will return null if the namespace does not exist.
     * </p>
     *
     * @param code the code to retrieve the namespace by. cannot be blank.
     * @return a {@link Namespace} or null
     * @throws RiceIllegalArgumentException if the code is blank
     */
    @WebMethod(operationName="getNamespace")
    @WebResult(name = "namespace")
    @Cacheable(value= Namespace.Cache.NAME, key="'key=' + #p0")
	Namespace getNamespace(@WebParam(name = "code") String code) throws RiceIllegalArgumentException;

    /**
     * Returns all Namespaces.
     *
     * @return all namespaces
     */
    @WebMethod(operationName = "findAllNamespaces")
    @WebResult(name = "namespaces")
    @XmlElementWrapper(name = "namespaces", required = true)
    @XmlElement(name = "namespace", required = false)
    @Cacheable(value=Namespace.Cache.NAME, key="'all'")
    List<Namespace> findAllNamespaces();
}
