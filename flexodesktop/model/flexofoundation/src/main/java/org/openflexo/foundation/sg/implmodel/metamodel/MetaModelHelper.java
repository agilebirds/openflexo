package org.openflexo.foundation.sg.implmodel.metamodel;

import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;

import java.lang.Class;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class MetaModelHelper {

    public static void invokeSetter(Object instance, String attributeName, Object value){
        try {
            LinkableObjectMetaModelFactory.instance()
                    .forClass((Class<LinkableTechnologyModelObject>) instance.getClass())
                    .getSetter(attributeName).invoke(instance, value);
        } catch (IllegalAccessException e) {
            handleException(e);
        } catch (InvocationTargetException e) {
            handleException(e);
        }
    }

    public static Object invokeTransformation(Object instance, String attributeName){
        try {
            return LinkableObjectMetaModelFactory.instance()
                    .forClass((Class<LinkableTechnologyModelObject>)instance.getClass())
                    .getTransaformationMethod(attributeName).invoke(instance);
        } catch (IllegalAccessException e) {
            return handleException(e);
        } catch (InvocationTargetException e) {
            return handleException(e);
        }
    }

    public static Set<String> getAllDerivableAttributes(Object instance){
        return LinkableObjectMetaModelFactory.instance().getAllDerivableAttributes((Class<LinkableTechnologyModelObject>) instance.getClass());
    }
    private static Object handleException(Exception e){
        e.printStackTrace();
        throw new RuntimeException(e);
    }

    public static boolean isDerivedAttribute(LinkableTechnologyModelObject instance, String attributeName) {
        return LinkableObjectMetaModelFactory.instance()
                    .forClass((Class<LinkableTechnologyModelObject>) instance.getClass())
                    .isDerivedAttribute(attributeName);
    }
}
