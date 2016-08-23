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
package org.kuali.kfs.coreservice.impl.style;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * An OJB implementation of the {@link StyleDao}.
 */
public class StyleDaoOjb extends PersistenceBrokerDaoSupport implements StyleDao {

    @Override
    public void saveStyle(StyleBo styleData) {
        if (styleData == null) {
            return;
        }
        this.getPersistenceBrokerTemplate().store(styleData);
    }

    @Override
    public StyleBo getStyle(String styleName) {
        if (styleName == null) {
            return null;
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", styleName);
        criteria.addEqualTo("active", Boolean.TRUE);
        return (StyleBo) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(StyleBo.class, criteria));
    }

    @Override
    public List<String> getAllStyleNames() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("active", Boolean.TRUE);
        List<StyleBo> styles = (List<StyleBo>) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(StyleBo.class, criteria));
        List<String> styleNames = new ArrayList<String>();
        for (StyleBo style : styles) {
            styleNames.add(style.getName());
        }
        return styleNames;
    }

}
