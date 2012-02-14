package org.openflexo.foundation.sg.implmodel.metamodel;

import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinkableObjectMetaModelImpl<T extends LinkableTechnologyModelObject> implements LinkableObjectMetaModel<T>{

    private final Map<String, Method> setters = new HashMap<String, Method>();

    private final Map<String, Method> transaformationMethods = new HashMap<String, Method>();

    private final Class<T> linkableObjectClass;

    private final Set<String> derivableAttributes = new HashSet<String>();

    public LinkableObjectMetaModelImpl(Class<T> linkableObjectClass) {
        this.linkableObjectClass = linkableObjectClass;
    }

    public void register(String attributeName, Method setter, Method transaformationMethod){
       if(setter==null){
           throw new IllegalArgumentException("setter cannot be null.");
       }
        if(transaformationMethod==null){
            throw new IllegalArgumentException("transformation method cannot be null");
        }
        if(setters.containsKey(attributeName)){
            throw new IllegalArgumentException(attributeName+" already registred.");
        }
        setters.put(attributeName,setter);
        transaformationMethods.put(attributeName,transaformationMethod);
        derivableAttributes.add(attributeName);
    }

    @Override
    public Method getSetter(String attributeName) {
        return setters.get(attributeName);
    }

    @Override
    public Method getTransaformationMethod(String attributeName) {
        return transaformationMethods.get(attributeName);
    }

    @Override
    public Class<T> getLinkableObjectClass() {
        return linkableObjectClass;
    }

    @Override
    public Set<String> getAllDerivableAttributes() {
        return derivableAttributes;
    }

    @Override
    public boolean isDerivedAttribute(String attributeName) {
        return derivableAttributes.contains(attributeName);
    }
}
