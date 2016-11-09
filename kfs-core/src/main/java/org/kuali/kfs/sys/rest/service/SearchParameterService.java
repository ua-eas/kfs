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
package org.kuali.kfs.sys.rest.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.rest.ErrorMessage;
import org.kuali.kfs.sys.rest.exception.ApiRequestException;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchParameterService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SearchParameterService.class);

    private PersistenceStructureService persistenceStructureService;

    public <T extends PersistableBusinessObject> int getLimit(Class<T> boClass, MultivaluedMap<String, String> params) {
        int limit = getIntQueryParameter(KFSConstants.Search.LIMIT, params);
        if (limit <= 0) {
            limit = LookupUtils.getSearchResultsLimit(boClass);
            if (limit <= 0) {
                limit = LookupUtils.getApplicationSearchResultsLimit();
            }
        }
        return limit;
    }

    public <T extends PersistableBusinessObject> String[] getSortCriteria(Class<T> boClass, MultivaluedMap<String, String> params,
                                                                                 List<String> boFields) {
        String orderByString = params.getFirst(KFSConstants.Search.SORT);
        if (orderByString != null) {
            List<ErrorMessage> errorMessages = new ArrayList<>();
            List<String> validSortFields = new ArrayList<>();
            String[] orderBy = orderByString.split(",");
            for (String sort : orderBy) {
                String cleanSort = sort.replaceFirst("^-", "");
                if (boFields.contains(cleanSort)) {
                    validSortFields.add(sort);
                } else {
                    LOG.debug("invalid sort field: " + sort);
                    errorMessages.add(new ErrorMessage("invalid sort field", sort));
                }
            }

            if (errorMessages.size() > 0) {
                throw new ApiRequestException("Invalid Search Criteria", errorMessages);
            }

            return validSortFields.toArray(new String[]{});
        } else {
            final List<String> ojbPrimaryKeys = persistenceStructureService.listPrimaryKeyFieldNames(boClass);
            if (!CollectionUtils.isEmpty(ojbPrimaryKeys)) {
                return ojbPrimaryKeys.toArray(new String[] {});
            } else if (boFields.contains("objectId")){
                return new String[]{"objectId"};
            }
        }

        return new String[] { boFields.get(0) }; // no other fields to check from...let's just sort on the first ojb column
    }

    public int getIntQueryParameter(String name, MultivaluedMap<String, String> params) {
        String paramString = params.getFirst(name);
        if (StringUtils.isNotBlank(paramString)) {
            try {
                return Integer.parseInt(paramString);
            } catch (NumberFormatException nfe) {
                LOG.debug(name + " parameter is not a number", nfe);
                throw new ApiRequestException("Invalid Search Criteria", new ErrorMessage("parameter is not a number", name));
            }
        }
        return 0;
    }

    public Map<String, String> getSearchQueryCriteria(MultivaluedMap<String, String> params, List<String> boFields) {
        List<String> reservedParams = Arrays.asList(KFSConstants.Search.SORT, KFSConstants.Search.LIMIT, KFSConstants.Search.SKIP);
        List<ErrorMessage> errorMessages = new ArrayList<>();
        Map<String, String> validParams = params.entrySet().stream()
            .filter(entry -> !reservedParams.contains(entry.getKey().toLowerCase()))
            .filter(entry -> {
                if(boFields.contains(entry.getKey())) {
                    return true;
                }

                LOG.debug("invalid query parameter name: " + entry.getKey());
                errorMessages.add(new ErrorMessage("invalid query parameter name", entry.getKey()));
                return false;
            })
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> params.getFirst(entry.getKey())));

        if (errorMessages.size() > 0) {
            throw new ApiRequestException("Invalid Search Criteria", errorMessages);
        }

        return validParams;
    }

    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
}
