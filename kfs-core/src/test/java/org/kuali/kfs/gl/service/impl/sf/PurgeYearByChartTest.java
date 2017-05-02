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
package org.kuali.kfs.gl.service.impl.sf;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.gl.batch.dataaccess.SufficientFundsDao;
import org.kuali.kfs.gl.service.impl.SufficientFundsServiceImpl;

public class PurgeYearByChartTest {
    private SufficientFundsServiceImpl sufficientFundsService;
    private SufficientFundsDao sufficientFundsDao;

    @Before
    public void setUp() {
        sufficientFundsService = new SufficientFundsServiceImpl();

        sufficientFundsDao = EasyMock.mock(SufficientFundsDao.class);

        sufficientFundsService.setSufficientFundsDao(sufficientFundsDao);
    }

    @Test
    public void testPurgeYearByChart() {
        sufficientFundsDao.purgeYearByChart("BL", 2017);
        EasyMock.replay(sufficientFundsDao);

        sufficientFundsService.purgeYearByChart("BL", 2017);
    }
}
