package org.openflexo.foundation.sg.implmodel.metamodel;

import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Provide convenient methods to retrieve the setter and the transformation method of a derived attribute.
 * @param <T>
 */
public interface LinkableObjectMetaModel<T extends LinkableTechnologyModelObject> {

    /**
     * @param attributeName  a field name define in linkableObjectClass or any ancestor
     * @return the setter method for the field named attributeName
     * @throws IllegalArgumentException if there is no field named attributeName in linkableObjectClass or any of it's ancestor.
     */
    public Method getSetter(String attributeName);

   /**
     * @param attributeName  a field name define in linkableObjectClass or any ancestor
     * @return the transaformation method for the field named attributeName (i.e. the derivation method, where the default value getDerived&lt;AttributeName&gt;()
     * @throws IllegalArgumentException if there is no field named attributeName in linkableObjectClass or any of it's ancestor.
     */
    public Method getTransaformationMethod(String attributeName);

    /**
     * @return the class this metamodel belongs to.
     */
    public Class<T> getLinkableObjectClass();

    Set<String> getAllDerivableAttributes();

    public boolean isDerivedAttribute(String attributeName);
}
