package org.openflexo.foundation.sg.implmodel.metamodel;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A factory for LinkableObjectMetaModel.
 * This a singleton with an internal cache of MetaModel.
 * MetaModel are lazy initialized and immutable.
 */
public class LinkableObjectMetaModelFactory {

    private static final Logger logger = Logger.getLogger(LinkableObjectMetaModelFactory.class.getPackage().getName());

    private Map<Class<? extends LinkableTechnologyModelObject>, LinkableObjectMetaModel> cache =
            new HashMap<Class<? extends LinkableTechnologyModelObject>, LinkableObjectMetaModel>();

    private static final LinkableObjectMetaModelFactory instance = new LinkableObjectMetaModelFactory();

    public static LinkableObjectMetaModelFactory instance(){
        return instance;
    }

    public <T extends LinkableTechnologyModelObject> LinkableObjectMetaModel<T> forClass(Class<T> clazz) {
        LinkableObjectMetaModel<T> metaModel = cache.get(clazz);
        if (metaModel == null) {
            metaModel = buildMetaModel(clazz);
            cache.put(clazz, metaModel);
        }
        return metaModel;
    }

    private <T extends LinkableTechnologyModelObject> LinkableObjectMetaModel<T> buildMetaModel(Class<T> clazz) {
        LinkableObjectMetaModelImpl<T> metaModel = new LinkableObjectMetaModelImpl(clazz);

        for (Field field : getInheritedFields(clazz)) {
            String attributeName = field.getName();
            Class<?> valueType = field.getType();
            Method setter = null;
            try {
                setter = findSetter(clazz, attributeName, valueType);
            } catch (NoSuchMethodException e) {
                logger.fine("Ignoring field "+clazz.getName()+"."+attributeName+" since it don't have setter.");
            }
            if (isDerivedAttribute(field, setter)) {
                logger.info("field : "+clazz.getName()+"." +attributeName+" is a derived attribute.");
                Method transformationMethod;
                try {
                    transformationMethod = findTransformationMethod(clazz, attributeName);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Field " + field.getDeclaringClass() + "." + field.getName()
                            + " is annotate as a DerivedAttribute but the required method :"
                            + "getDerived" + StringUtils.capitalize(field.getName()) + "() cannot be found.");
                }
                metaModel.register(attributeName,setter,transformationMethod);

            }
        }
        return metaModel;
    }

    private boolean isDerivedAttribute(Field field, Method setter) {
        if(setter==null)return false;
        return field.getAnnotation(DerivedAttribute.class) != null || setter.getAnnotation(DerivedAttribute.class) != null;
    }

    private <T extends LinkableTechnologyModelObject> Method findSetter(Class<T> clazz, String attributeName, Class<?> valueType) throws NoSuchMethodException {
        return clazz.getMethod("set" + StringUtils.capitalize(attributeName), valueType);
    }

    private <T extends LinkableTechnologyModelObject> Method findTransformationMethod(Class<T> clazz, String attributeName) throws NoSuchMethodException {
        return clazz.getMethod("getDerived" + StringUtils.capitalize(attributeName));
    }

    private static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public Set<String> getAllDerivableAttributes(Class<LinkableTechnologyModelObject> aClass) {
        LinkableObjectMetaModel metaModel = forClass(aClass);
        return metaModel.getAllDerivableAttributes();
    }
}
