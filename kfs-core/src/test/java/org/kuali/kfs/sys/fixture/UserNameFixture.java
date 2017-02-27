/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.sys.fixture;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

public enum UserNameFixture {

    NO_SESSION("NO_SESSION"), // This is not a user name. It is a Sentinal value telling KualiTestBase not to create a session. (It's needed
                // because null is not a valid default for the ConfigureContext annotation's session element.)
    kfs("kfs"), // This is the KualiUser.SYSTEM_USER, which certain automated document type authorizers require.
    khuntley("khuntley"), // KualiTestBaseWithSession used this one by default. (testUsername in configuration.properties, no longer used but
                // cannot be removed because that file cannot be committed).
    ghatten("ghatten"), stroud("stroud"), dfogle("dfogle"), rjweiss("rjweiss"), rorenfro("rorenfro"), sterner("sterner"),
    ferland("ferland"), hschrein("hschrein"), hsoucy("hsoucy"),  lraab("lraab"), jhavens("jhavens"), kcopley("kcopley"),
    mhkozlow("mhkozlow"), ineff("ineff"), vputman("vputman"), cswinson("cswinson"), mylarge("mylarge"), rruffner("rruffner"),
    season("season"), dqperron("dqperron"), aatwood("aatwood"), parke("parke"), appleton("appleton"), twatson("twatson"),
    butt("butt"), jkitchen ("jkitchen"), bomiddle("bomiddle"), aickes("aickes"), day("day"), jgerhart("jgerhart"),
    // Regional Budget Manager
    ocmcnall("ocmcnall"),
    // University Administration Budget Manager
    wbrazil("wbrazil"),
    // Non-KFS User
    bcoffee("bcoffee"),
    // Account secondary delegate
    rmunroe("rmunroe"),
    // Org Hierarchy Reviewer
    cknotts("cknotts"),
    // Accounting Hierarchy Reviewer
    jrichard("jrichard"),
    // Fund Managers
    wklykins("wklykins"), wcorbitt("wcorbitt"),
    // UA specific users
    KFS_TEST_SYS8("kfs-test-sys8"),
    KFS_TEST_SYS9("kfs-test-sys9");


    private String principalName;


    private UserNameFixture(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public Person getPerson() {
        return SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(getPrincipalName());
    }
}

