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
package org.kuali.kfs.module.purap.document;

import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.sys.context.KualiTestBase;

import java.util.List;

public class PurchasingDocumentTestUtils extends KualiTestBase {

    public static void testAddItem(PurchasingDocument document, List<PurchasingItem> items, int expectedItemTotal) throws Exception {
        assertTrue("no items found", items.size() > 0);

        assertEquals(0, document.getItems().size());

        for (PurchasingItem item : items) {
            document.addItem(item);
        }

        assertEquals("item count mismatch", expectedItemTotal, document.getItems().size());
    }

}
