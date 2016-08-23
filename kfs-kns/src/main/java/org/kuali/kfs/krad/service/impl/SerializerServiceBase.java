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
package org.kuali.kfs.krad.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.kuali.kfs.krad.util.DateTimeConverter;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentSerializerService;
import org.kuali.kfs.krad.service.PersistenceService;
import org.kuali.kfs.krad.service.SerializerService;
import org.kuali.kfs.krad.service.XmlObjectSerializerService;
import org.kuali.kfs.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.PropertyType;
import org.kuali.kfs.krad.util.documentserializer.SerializationState;
import org.springframework.util.AutoPopulatingList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of the {@link DocumentSerializerService}.  If no &lt;workflowProperties&gt; have been defined in the
 * data dictionary for a document type (i.e. {@link Document#getDocumentPropertySerizabilityEvaluator()} returns an instance of
 * {@link AlwaysTruePropertySerializibilityEvaluator}), then this service will revert to using the {@link XmlObjectSerializerService}
 * bean, which was the old way of serializing a document for routing.  If workflowProperties are defined, then this implementation
 * will selectively serialize items.
 */
public abstract class SerializerServiceBase implements SerializerService  {
//	private static final Log LOG = LogFactory.getLog(SerializerServiceBase.class);

    protected PersistenceService persistenceService;
    protected XmlObjectSerializerService xmlObjectSerializerService;

    protected XStream xstream;
    protected ThreadLocal<SerializationState> serializationStates;
    protected ThreadLocal<PropertySerializabilityEvaluator> evaluators;

    public SerializerServiceBase() {
        serializationStates = new ThreadLocal<SerializationState>();
        evaluators = new ThreadLocal<PropertySerializabilityEvaluator>();

        xstream = new XStream(new ProxyAndStateAwareJavaReflectionProvider());
        xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
        xstream.addDefaultImplementation(ArrayList.class, ListProxyDefaultImpl.class);
        xstream.addDefaultImplementation(AutoPopulatingList.class, ListProxyDefaultImpl.class);
        xstream.registerConverter(new AutoPopulatingListConverter(xstream.getMapper()));
        xstream.registerConverter(new DateTimeConverter());
    }

    public class ProxyConverter extends ReflectionConverter {
        public ProxyConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
        }
        public boolean canConvert(Class clazz) {
            return clazz.getName().contains("CGLIB") || clazz.getName().equals("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
        }

        public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
            if (obj instanceof ListProxyDefaultImpl) {
                List copiedList = new ArrayList();
                List proxiedList = (List) obj;
                for (Iterator iter = proxiedList.iterator(); iter.hasNext();) {
                    copiedList.add(iter.next());
                }
                context.convertAnother( copiedList );
            }
            else {
                super.marshal(getPersistenceService().resolveProxy(obj), writer, context);
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }
    }

    public class ProxyAndStateAwareJavaReflectionProvider extends PureJavaReflectionProvider {
        @Override
        public void visitSerializableFields(Object object, Visitor visitor) {
            SerializationState state = serializationStates.get();
            PropertySerializabilityEvaluator evaluator = evaluators.get();

            for (Iterator iterator = fieldDictionary.serializableFieldsFor(object.getClass()); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!fieldModifiersSupported(field)) {
                    continue;
                }

                if (ignoreField(field)) {
                    continue;
                }

                validateFieldAccess(field);

                initializeField(object, field);

                Object value = null;
                try {
                    value = field.get(object);
                } catch (IllegalArgumentException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                }

                if (evaluator.isPropertySerializable(state, object, field.getName(), value)) {
                    if (value != null && ProxyHelper.isProxy(value)) {
                        // resolve proxies after we determine that it's serializable
                        value = getPersistenceService().resolveProxy(value);
                    }
                    PropertyType propertyType = evaluator.determinePropertyType(value);
                    state.addSerializedProperty(field.getName(), propertyType);
                    visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
                    state.removeSerializedProperty();
                }
            }
        }

        protected boolean ignoreField(Field field) {
            return false;
        }

        protected void initializeField(Object object, Field field) {
        }
    }

    public PersistenceService getPersistenceService() {
        return this.persistenceService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public XmlObjectSerializerService getXmlObjectSerializerService() {
        return this.xmlObjectSerializerService;
    }

    public void setXmlObjectSerializerService(XmlObjectSerializerService xmlObjectSerializerService) {
        this.xmlObjectSerializerService = xmlObjectSerializerService;
    }

    protected SerializationState createNewDocumentSerializationState(Document document) {
        return new SerializationState();
    }

    public class AutoPopulatingListConverter extends CollectionConverter {

    	public AutoPopulatingListConverter(Mapper mapper){
    		super(mapper);
    	}

        @Override
    	public boolean canConvert(Class clazz) {
    		return clazz.equals(AutoPopulatingList.class);
        }

    }


}

