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
package org.kuali.kfs.coreservice.api.parameter;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Collection;

/**
 * Service for interacting with {@link Parameter Parameters}.
 */
@WebService(name = "parameterService", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ParameterRepositoryService {

    /**
     * This will create a {@link Parameter} exactly like the parameter passed in.
     *
     * @param parameter the parameter to create
     * @return the parameter that was created
     * @throws RiceIllegalArgumentException if the parameter is null
     * @throws RiceIllegalStateException    if the parameter is already existing in the system
     */
    @WebMethod(operationName = "createParameter")
    @WebResult(name = "parameter")
    @CacheEvict(value = {Parameter.Cache.NAME}, allEntries = true)
    Parameter createParameter(@WebParam(name = "parameter") Parameter parameter)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * This will update a {@link Parameter}.
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will update that parameter.
     * </p>
     *
     * @param parameter the parameter to update
     * @return the parameter that was updated
     * @throws RiceIllegalArgumentException if the parameter is null
     * @throws RiceIllegalStateException    if the parameter does not exist in the system under the
     *                                      specific application id or default rice application id
     */
    @WebMethod(operationName = "updateParameter")
    @WebResult(name = "parameter")
    @CacheEvict(value = {Parameter.Cache.NAME}, allEntries = true)
    Parameter updateParameter(@WebParam(name = "parameter") Parameter parameter)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * Gets a {@link Parameter} from a {@link ParameterKey}.
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will return that parameter.
     * </p>
     * <p>
     * <p>
     * This method will return null if the parameter does not exist.
     * </p>
     *
     * @param key the key to retrieve the parameter by. cannot be null.
     * @return a {@link Parameter} or null
     * @throws RiceIllegalArgumentException if the key is null
     */
    @WebMethod(operationName = "getParameter")
    @WebResult(name = "parameter")
    @Cacheable(value = Parameter.Cache.NAME, key = "'key=' + #p0.getCacheKey()")
    Parameter getParameter(@WebParam(name = "key") ParameterKey key) throws RiceIllegalArgumentException;

    /**
     * Gets a {@link ParameterContract#getValue()} from a {@link ParameterKey}.
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will return that parameter.
     * </p>
     * <p>
     * <p>
     * This method will return null if the parameter does not exist.
     * </p>
     *
     * @param key the key to retrieve the parameter by. cannot be null.
     * @return a string value or null
     * @throws RiceIllegalArgumentException if the key is null
     */
    @WebMethod(operationName = "getParameterValueAsString")
    @WebResult(name = "value")
    @Cacheable(value = Parameter.Cache.NAME, key = "'{getParameterValueAsString}' + 'key=' + #p0.getCacheKey()")
    String getParameterValueAsString(@WebParam(name = "key") ParameterKey key) throws RiceIllegalArgumentException;

    /**
     * Gets a {@link ParameterContract#getValue()} as a Boolean from a {@link ParameterKey}.
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will return that parameter.
     * </p>
     * <p>
     * <p>
     * This method will return null if the parameter does not exist or is not a valid truth value.
     * </p>
     * <p>
     * valid true values (case insensitive):
     * <ul>
     * <li>Y</li>
     * <li>true</li>
     * <li>on</li>
     * <li>1</li>
     * <li>t</li>
     * <li>enabled</li>
     * </ul>
     * <p>
     * valid false values (case insensitive):
     * <ul>
     * <li>N</li>
     * <li>false</li>
     * <li>off</li>
     * <li>0</li>
     * <li>f</li>
     * <li>disabled</li>
     * </ul>
     *
     * @param key the key to retrieve the parameter by. cannot be null.
     * @return a boolean value or null
     * @throws RiceIllegalArgumentException if the key is null
     */
    @WebMethod(operationName = "getParameterValueAsBoolean")
    @WebResult(name = "value")
    @Cacheable(value = Parameter.Cache.NAME, key = "'{getParameterValueAsBoolean}' + 'key=' + #p0.getCacheKey()")
    Boolean getParameterValueAsBoolean(@WebParam(name = "key") ParameterKey key) throws RiceIllegalArgumentException;

    /**
     * Gets a {@link ParameterContract#getValue()} from a {@link ParameterKey}
     * where the value is split on a semi-colon delimeter and each token is trimmed
     * of white space.
     * <p>
     * for example:  param_name=foo; bar; baz
     * <p>
     * will yield a collection containing foo, bar, baz
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will return that parameter.
     * </p>
     * <p>
     * <p>
     * This method will always return an <b>immutable</b> Collection
     * even when no values exist.
     * </p>
     *
     * @param key the key to retrieve the parameter by. cannot be null.
     * @return an immutable collection of strings
     * @throws RiceIllegalArgumentException if the key is null
     */
    @WebMethod(operationName = "getParameterValuesAsString")
    @XmlElementWrapper(name = "values", required = true)
    @XmlElement(name = "value", required = false)
    @WebResult(name = "values")
    @Cacheable(value = Parameter.Cache.NAME, key = "'{getParameterValuesAsString}' + 'key=' + #p0.getCacheKey()")
    Collection<String> getParameterValuesAsString(@WebParam(name = "key") ParameterKey key) throws RiceIllegalArgumentException;

    /**
     * Gets a {@link ParameterContract#getValue()} from a {@link ParameterKey}
     * where the value is split on a semi-colon delimeter and each token is trimmed
     * of white space.  Those values are themselves keyvalue pairs which are searched
     * for the sub parameter name.
     * <p>
     * for example:
     * <p>
     * param_name=foo=f1; bar=b1; baz=z1
     * subParameterName=bar
     * <p>
     * will yield b1
     * <p>
     * <p>if multiple subparameters are contained in the parameter value the first one is returned</p>
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will return that parameter.
     * </p>
     * <p>
     * <p>
     * This method will always return null when the subparameter does not
     * exist or if the parameter value does not conform to the following format(s):
     * <ol>
     * <li>subparameter_name=subparameter_value;</li>
     * </ol>
     * </p>
     *
     * @param key              the key to retrieve the parameter by. cannot be null.
     * @param subParameterName the sub parameter to search for
     * @return a string value or null
     * @throws RiceIllegalArgumentException if the key is null or if the subParameterName is blank
     */
    @WebMethod(operationName = "getSubParameterValueAsString")
    @WebResult(name = "value")
    String getSubParameterValueAsString(@WebParam(name = "key") ParameterKey key,
                                        @WebParam(name = "subParameterName") String subParameterName)
        throws RiceIllegalArgumentException;

    /**
     * Gets a {@link ParameterContract#getValue()} from a {@link ParameterKey}
     * where the value is split on a semi-colon delimeter and each token is trimmed
     * of white space.  Those values are themselves keyvalue pairs which are searched
     * for the sub parameter name.  After the sub parameter is found it is split on a comma
     * and trimmed or whitespace before adding it to the final collection for return.
     * <p>
     * for example:
     * <p>
     * param_name=foo=f1,f2,f3; bar=b1,b2; baz=z1
     * subParameterName=bar
     * <p>
     * will yield a collection containing b1, b2
     * <p>
     * <p>if multiple subparameters are contained in the parameter value the first one is returned</p>
     * <p>
     * <p>
     * If the parameter does not exist under the application
     * code passed, then this method will check if the parameter
     * exists under the default rice application id and
     * will return that parameter.
     * </p>
     * <p>
     * <p>
     * This method will always return an <b>immutable</b> Collection
     * even when no values exist.
     * </p>
     * <p>
     * <p>
     * This method will always return an empty <b>immutable</b> Collection when
     * the subparameter does not exist or if the parameter value does not
     * conform to the following format(s):
     * <ol>
     * <li>subparameter_name=subparameter_value;</li>
     * <li>subparameter_name=subparameter_value1, subparameter_value2;</li>
     * <li>subparameter_name=subparameter_value1, subparameter_value2,;</li>
     * </ol>
     * </p>
     *
     * @param key              the key to retrieve the parameter by. cannot be null.
     * @param subParameterName the sub parameter to search for
     * @return an immutable collection of strings
     * @throws RiceIllegalArgumentException if the key is null or if the subParameterName is blank
     */
    @WebMethod(operationName = "getSubParameterValuesAsString")
    @XmlElementWrapper(name = "values", required = true)
    @XmlElement(name = "value", required = false)
    @WebResult(name = "values")
    Collection<String> getSubParameterValuesAsString(@WebParam(name = "key") ParameterKey key,
                                                     @WebParam(name = "subParameterName") String subParameterName)
        throws RiceIllegalArgumentException;

    @WebMethod(operationName = "findParameters")
    @WebResult(name = "results")
    ParameterQueryResults findParameters(@WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException;
}
