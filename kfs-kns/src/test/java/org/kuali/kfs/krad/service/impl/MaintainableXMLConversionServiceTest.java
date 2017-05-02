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
package org.kuali.kfs.krad.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.bo.NoteForTest;
import org.kuali.kfs.krad.bo.NoteForTestExtension;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.MaintainableXMLConversionService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MaintainableXMLConversionServiceTest {
    @Test
    public void testClassNameSubstitution() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                this.classNameRuleMap.put("org.kuali.kfs.krad.bo.AdHocRoutePerson", "org.kuali.kfs.krad.bo.AdHocRouteWorkgroup");
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.AdHocRoutePerson><type>1</type></org.kuali.kfs.krad.bo.AdHocRoutePerson>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should start with new class name", convertedXML.startsWith("<org.kuali.kfs.krad.bo.AdHocRouteWorkgroup>"));
        Assert.assertTrue("Converted XML should end with new class name", convertedXML.endsWith("</org.kuali.kfs.krad.bo.AdHocRouteWorkgroup>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testClassPropertySubstitution() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                this.classNameRuleMap.put("org.kuali.kfs.krad.bo.AdHocRoutePerson", "org.kuali.kfs.krad.bo.AdHocRouteWorkgroup");
                this.classPropertyRuleMap = new HashMap<>();
                Map<String, String> childRules = new HashMap<>();
                childRules.put("adHocRouteType", "type");
                this.classPropertyRuleMap.put("org.kuali.kfs.krad.bo.AdHocRouteWorkgroup", childRules);
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.AdHocRoutePerson><adHocRouteType>1</adHocRouteType></org.kuali.kfs.krad.bo.AdHocRoutePerson>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should start with new class name", convertedXML.startsWith("<org.kuali.kfs.krad.bo.AdHocRouteWorkgroup>" + System.getProperty("line.separator") + "<type>"));
        Assert.assertTrue("Converted XML should end with new class name", convertedXML.endsWith("</type>" + System.getProperty("line.separator") + "</org.kuali.kfs.krad.bo.AdHocRouteWorkgroup>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testClassAttributeSubstitution() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                this.classNameRuleMap.put("org.kuali.rice.kim.bo.impl.PersonImpl", "org.kuali.rice.kim.impl.identity.PersonImpl");
            }

            @Override
            public KualiModuleService getKualiModuleService() {
                KualiModuleService moduleServiceMock = EasyMock.mock(KualiModuleService.class);
                ModuleService personModuleService = EasyMock.mock(ModuleService.class);
                EasyMock.<Class<? extends ExternalizableBusinessObject>>expect(personModuleService.getExternalizableBusinessObjectImplementation(org.kuali.rice.kim.api.identity.Person.class)).andReturn(org.kuali.rice.kim.impl.identity.PersonImpl.class);
                EasyMock.expect(moduleServiceMock.getResponsibleModuleService(EasyMock.eq(org.kuali.rice.kim.api.identity.Person.class))).andReturn(personModuleService);
                EasyMock.replay(personModuleService);
                EasyMock.replay(moduleServiceMock);
                return moduleServiceMock;
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.NoteForTest><authorUniversal class=\"org.kuali.rice.kim.bo.impl.PersonImpl\"><principalName>khuntley</principalName></authorUniversal></org.kuali.kfs.krad.bo.NoteForTest>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should start with new class name", convertedXML.startsWith("<org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<authorUniversal class=\"org.kuali.rice.kim.impl.identity.PersonImpl\">"));
        Assert.assertTrue("Converted XML should end with new class name", convertedXML.endsWith("</authorUniversal>" + System.getProperty("line.separator") + "</org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testClassAttributeRemovalWithNoChildren() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                this.classRemovals.add("org.kuali.rice.kim.bo.impl.PersonImpl");
            }

            @Override
            public KualiModuleService getKualiModuleService() {
                KualiModuleService moduleServiceMock = EasyMock.mock(KualiModuleService.class);
                ModuleService personModuleService = EasyMock.mock(ModuleService.class);
                EasyMock.<Class<? extends ExternalizableBusinessObject>>expect(personModuleService.getExternalizableBusinessObjectImplementation(org.kuali.rice.kim.api.identity.Person.class)).andReturn(org.kuali.rice.kim.impl.identity.PersonImpl.class);
                EasyMock.expect(moduleServiceMock.getResponsibleModuleService(EasyMock.eq(org.kuali.rice.kim.api.identity.Person.class))).andReturn(personModuleService);
                EasyMock.replay(personModuleService);
                EasyMock.replay(moduleServiceMock);
                return moduleServiceMock;
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.NoteForTest><authorUniversal class=\"org.kuali.rice.kim.bo.impl.PersonImpl\"/></org.kuali.kfs.krad.bo.NoteForTest>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should have removed person class", convertedXML.equals("<org.kuali.kfs.krad.bo.NoteForTest/>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testExtensionSubstitution() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                Map<String, String> propertySubstitution = new HashMap<>();
                propertySubstitution.put("aValue", "someValue");
                this.classPropertyRuleMap.put("org.kuali.kfs.krad.bo.NoteForTestExtension", propertySubstitution);
            }

            @Override
            public PersistenceStructureService getPersistenceStructureService() {
                PersistenceStructureService mockPersistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
                Map<String, DataObjectRelationship> relationshipDefinitionMap = new HashMap<>();
                DataObjectRelationship relationshipDefinition = new DataObjectRelationship() {
                    @Override
                    public Class<?> getParentClass() {
                        return NoteForTest.class;
                    }

                    @Override
                    public Class<?> getRelatedClass() {
                        return NoteForTestExtension.class;
                    }
                };

                relationshipDefinitionMap.put(KRADPropertyConstants.EXTENSION, relationshipDefinition);
                EasyMock.expect(mockPersistenceStructureService.getRelationshipMetadata(NoteForTest.class, KRADPropertyConstants.EXTENSION)).andReturn(relationshipDefinitionMap);
                EasyMock.replay(mockPersistenceStructureService);
                return mockPersistenceStructureService;
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.NoteForTest><extension><aValue>khuntley</aValue></extension></org.kuali.kfs.krad.bo.NoteForTest>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should start with new class name", convertedXML.startsWith("<org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<extension>" + System.getProperty("line.separator") + "<someValue>khuntley"));
        Assert.assertTrue("Converted XML should end with new class name", convertedXML.endsWith("</someValue>" + System.getProperty("line.separator") + "</extension>" + System.getProperty("line.separator") + "</org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testUninstantiatableClassSkipping() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.NoteForTest><workRatio><value>0.95</value></workRatio></org.kuali.kfs.krad.bo.NoteForTest>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should start with new class name", convertedXML.equals("<org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<workRatio>" + System.getProperty("line.separator") + "<value>0.95</value>" + System.getProperty("line.separator") + "</workRatio>" + System.getProperty("line.separator") + "</org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testRemovablePropertiesRemoved() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                Map<String, String> propertySubstitution = new HashMap<>();
                propertySubstitution.put("autoIncrementSet", "");
                this.classPropertyRuleMap.put("*", propertySubstitution);
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.NoteForTest><autoIncrementSet>1</autoIncrementSet></org.kuali.kfs.krad.bo.NoteForTest>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should remove autoIncrementSet", convertedXML.equals("<org.kuali.kfs.krad.bo.NoteForTest/>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    @Test
    public void testRemovablePropertiesInsideListRemoved() throws Exception {
        MaintainableXMLConversionService maintainableXMLConversionService = new MockMaintainableXMLConversionServiceImpl() {
            @Override
            protected void setRuleMaps() {
                super.setRuleMaps();
                Map<String, String> propertySubstitution = new HashMap<>();
                propertySubstitution.put("autoIncrementSet", "");
                this.classPropertyRuleMap.put("*", propertySubstitution);
            }
        };
        ((InitializingBean) maintainableXMLConversionService).afterPropertiesSet();
        final String originalXML = "<org.kuali.kfs.krad.bo.NoteForTest><subNotes class=\"java.util.ArrayList\"><org.kuali.kfs.krad.bo.NoteForTest><noteText>hi</noteText><autoIncrementSet>1</autoIncrementSet></org.kuali.kfs.krad.bo.NoteForTest></subNotes></org.kuali.kfs.krad.bo.NoteForTest>";
        final String convertedXML = maintainableXMLConversionService.transformMaintainableXML(originalXML);
        Assert.assertTrue("Converted XML should remove autoIncrementSet from NoteForTest inside subNotes", convertedXML.equals("<org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<subNotes class=\"java.util.ArrayList\">" + System.getProperty("line.separator") + "<org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<noteText>hi</noteText>" + System.getProperty("line.separator") + "</org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "</subNotes>" + System.getProperty("line.separator") + "</org.kuali.kfs.krad.bo.NoteForTest>" + System.getProperty("line.separator") + "<maintenanceAction>"));
    }

    class MockMaintainableXMLConversionServiceImpl extends MaintainableXMLConversionServiceImpl {
        @Override
        protected void setRuleMaps() {
            this.classNameRuleMap = new HashMap<>();
            this.classPropertyRuleMap = new HashMap<>();
            this.classRemovals = new HashSet<>();
        }

        /**
         * Override to return _something_ - without which conversion will not run
         *
         * @return a silly test rule
         */
        @Override
        public List<String> getConversionRuleFiles() {
            List<String> fakeList = new ArrayList<String>();
            fakeList.add("classpath:/fakerulez.xml");
            return fakeList;
        }
    }
}
