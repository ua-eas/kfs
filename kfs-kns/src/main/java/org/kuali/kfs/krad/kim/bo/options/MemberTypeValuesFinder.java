/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.krad.kim.bo.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.KimConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This exists to make the data dictionary loading happy!
 */
public class MemberTypeValuesFinder extends KeyValuesBase {

    private static final List<KeyValue> LABELS;

    static {
        final List<KeyValue> labels = new ArrayList<KeyValue>(3);
        labels.add(new ConcreteKeyValue(MemberType.PRINCIPAL.getCode(), KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL));
        labels.add(new ConcreteKeyValue(MemberType.GROUP.getCode(), KimConstants.KimUIConstants.MEMBER_TYPE_GROUP));
        labels.add(new ConcreteKeyValue(MemberType.ROLE.getCode(), KimConstants.KimUIConstants.MEMBER_TYPE_ROLE));
        LABELS = Collections.unmodifiableList(labels);
    }

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return LABELS;
    }

}
