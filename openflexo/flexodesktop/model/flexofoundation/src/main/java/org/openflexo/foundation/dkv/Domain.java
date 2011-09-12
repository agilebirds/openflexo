/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.dkv;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dkv.action.AddKeyAction;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.dkv.dm.KeyAdded;
import org.openflexo.foundation.dkv.dm.KeyRemoved;
import org.openflexo.foundation.dkv.dm.LanguageAdded;
import org.openflexo.foundation.dkv.dm.LanguageRemoved;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.xml.FlexoDKVModelBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class Domain extends DKVObject implements FlexoObserver, InspectableObject
{
    private static final Logger logger = FlexoLogger.getLogger(Domain.class.getPackage().getName());

    Vector<Key> keys;

    ValueHashtable<String,Value> values;

    private KeyList keyList;

    private ValueList valueList;

    public Domain(FlexoDKVModelBuilder builder)
    {
        this(builder.dkvModel);
        initializeDeserialization(builder);
    }

    /**
     * 
     */
    public Domain(DKVModel dkv)
    {
        super(dkv);
        keys = new Vector<Key>();
        values = new ValueHashtable<String, Value>();
        keyList = new KeyList(dkv);
        valueList = new ValueList(dkv);
    }

    /**
     * Overrides finalizeDeserialization
     * 
     * @see org.openflexo.foundation.FlexoXMLSerializableObject#finalizeDeserialization(java.lang.Object)
     */
    @Override
	public void finalizeDeserialization(Object builder)
    {
        dkvModel.addObserver(this);
        Enumeration en = dkvModel.getLanguages().elements();
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            lg.addObserver(this);
        }
        super.finalizeDeserialization(builder);
    }

    @Override
	final public void delete()
    {
        setChanged();
        notifyObservers(new DomainDeleted(this));
        getDkvModel().deleteObserver(this);
        getDkvModel().removeFromDomains(this);
        super.delete();
        deleteObservers();
    }

    @Override
	public void undelete(){
    	super.undelete();
    	getDkvModel().addObserver(this);
    	getDkvModel().addToDomains(this);
    }
    
    /**
     * Overrides getSpecificActionListForThatClass
     * @see org.openflexo.foundation.FlexoModelObject#getSpecificActionListForThatClass()
     */
    @Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
        Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
        v.add(AddKeyAction.actionType);
        return v;
    }
    
    public boolean isKeyNameLegal(String keyName) throws DuplicateDKVObjectException, EmptyStringException 
    {
        if (keyName == null)
            throw new NullPointerException();
        if (keyName.trim().length() == 0)
            throw new EmptyStringException();
        Enumeration<Key> en = keys.elements();
        while (en.hasMoreElements()) {
            Key key = en.nextElement();
            if (key.getName().equals(keyName))
                throw new DuplicateDKVObjectException(key);
        }
         return true;
    } 
    
    public Key addKeyNamed(String keyName) throws EmptyStringException, DuplicateDKVObjectException
    {
        if (keyName == null)
            throw new NullPointerException();
        if (keyName.trim().length() == 0)
            throw new EmptyStringException();
        if (getKeyNamed(keyName)!=null)
            throw new DuplicateDKVObjectException(getKeyNamed(keyName));
        Key key = new Key(getDkvModel(), this);
        key.setName(keyName);
        addToKeys(key);
        return key;
    }

    public void addToKeys(Key key)
    {
        if (keys.contains(key)) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Attempt to insert twice the same key.");
            return;
        }
        Enumeration en = getDkvModel().getLanguages().elements();
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            Value v = new Value(getDkvModel(), key, lg);
            values.put(v.getFullyQualifiedName(), v);
        }
        keys.add(key);
        key.setDomain(this);
        key.addObserver(this);
        setChanged();
        KeyAdded ka = new KeyAdded(key);
        notifyObservers(ka);
        getKeyList().setChanged();
        getKeyList().notifyObservers(ka);
        getValueList().setChanged();
        getValueList().notifyObservers(ka);
    }

    public void removeFromKeys(Key key)
    {
        if (!keys.contains(key)) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Attempt to remove a key that can not be found: " + key.getName());
            return;
        }
        keys.remove(key);
        FlexoIndexManager.reIndexObjectOfArray(getKeys().toArray(new Key[0]));
        Enumeration en = getDkvModel().getLanguages().elements();
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            values.remove(lg.getName() + "." + key.getName());
        }
        key.deleteObserver(this);
        setChanged();
        KeyRemoved kr = new KeyRemoved(key);
        notifyObservers(kr);
        getKeyList().setChanged();
        getKeyList().notifyObservers(kr);
        getValueList().setChanged();
        getValueList().notifyObservers(kr);
    }

    public void removeValueWithKey(String valueFullyQualifiedName)
    {
        values.remove(valueFullyQualifiedName);
    }

    public Value getValue(Key key, Language lg)
    {
        return values.get(lg.getName() + "." + key.getName());
    }

    public Key getKeyNamed(String name)
    {
        Enumeration<Key> en = keys.elements();
        while (en.hasMoreElements()) {
            Key key = en.nextElement();
            if (key.getName().equals(name))
                return key;
        }
        return null;
    }
    
    /**
     * Not used for now.
     * 
     */
    private void registerValuesObserving()
    {
        if (this.values != null) {
            Enumeration en = values.elements();
            while (en.hasMoreElements()) {
                Value v = (Value) en.nextElement();
                v.addObserver(this);
            }
        }
    }

    private void unregisterValuesObserving()
    {
        if (this.values != null) {
            Enumeration en = values.elements();
            while (en.hasMoreElements()) {
                Value v = (Value) en.nextElement();
                v.deleteObserver(this);
            }
        }
    }

    public void setValue(Value value)
    {
        if (value != null)
            values.put(value.getFullyQualifiedName(), value);
    }

    public void setValueForKey(Value v, String fullyQualifiedName)
    {
        values.put(fullyQualifiedName, v);
    }

    /**
     * Overrides getFullyQualifiedName
     * 
     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
     */
    @Override
	public String getFullyQualifiedName()
    {
        return "DOMAIN." + name;
    }

    @Override
	public DKVModel getDkvModel()
    {
        return dkvModel;
    }

    public void setDkvModel(DKVModel dkvModel)
    {
        if (this.dkvModel != null)
            this.dkvModel.deleteObserver(this);
        this.dkvModel = dkvModel;
        if (dkvModel != null)
            this.dkvModel.addObserver(this);
    }

    public Vector<Key> getKeys()
    {
        return keys;
    }

    public void setKeys(Vector<Key> keys)
    {
        this.keys = keys;
    }

    public Object[] getSortedKeys()
    {
        if (keys.size() == 0)
            return new Object[0];
        Object[] o = new Object[keys.size()];
        int i = 0;
        Enumeration<Key> en = keys.elements();
        while (en.hasMoreElements()) {
            o[i++] = en.nextElement();
        }
        Arrays.sort(o, (Comparator) o[0]);
        return o;
    }

    public Hashtable<String,Value> getValues()
    {
        return values;
    }

    public void setValues(Hashtable<String,Value> values)
    {
        unregisterValuesObserving();
        this.values = new ValueHashtable<String, Value>(values);
    }

    /**
     * Overrides update
     * 
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        if (dataModification instanceof LanguageRemoved) {
            Language lg = (Language) ((LanguageRemoved) dataModification).oldValue();
            Enumeration<Key> en = getKeys().elements();
            while (en.hasMoreElements()) {
                Key key = en.nextElement();
                Value removed = values.remove(lg.getName() + "." + key.getName());
                key.notifyValueRemoved(removed);
            }
            getValueList().setChanged();
            getValueList().notifyObservers(dataModification);
        } else if (dataModification instanceof LanguageAdded) {
            Language lg = (Language) ((LanguageAdded) dataModification).newValue();
            Enumeration<Key> en = keys.elements();
            while (en.hasMoreElements()) {
                Key key = en.nextElement();
                Value v = new Value(getDkvModel(), key, lg);
                values.put(v.getFullyQualifiedName(), v);
                key.notifyValueAdded(v);
            }
            getValueList().setChanged();
            getValueList().notifyObservers(dataModification);
        } else if (dataModification instanceof DKVDataModification
                && ((DKVDataModification) dataModification).propertyName().equals("value")) {
            setChanged();
            notifyObservers(dataModification);
        }
    }

    public Object[] getSortedValues()
    {
        Object[] values = new Object[keys.size() * getDkvModel().getLanguages().size()];
        Object[] keys = getSortedKeys();
        Enumeration en = getDkvModel().getLanguages().elements();
        int j = 0;
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            for (int i = 0; i < keys.length; i++) {
                values[j++] = getValue((Key) keys[i], lg);
            }
        }
        return values;
    }

    /**
     * Overrides getInspectorName
     * 
     * @see org.openflexo.inspector.InspectableObject#getInspectorName()
     */
    @Override
	public String getInspectorName()
    {
        return Inspectors.IE.DOMAIN_INSPECTOR;
    }

    public class KeyList extends DKVObject
    {

        /**
         * @param dl
         */
        public KeyList(DKVModel dl)
        {
            super(dl);
        }

        public Object[] getKeyList()
        {
            return getSortedKeys();
        }

        public Object elementAt(int index)
        {
            return getKeyList()[index];
        }
        
        /**
         * Overrides getFullyQualifiedName
         * 
         * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
         */
        @Override
		public String getFullyQualifiedName()
        {
            return getName() + ".KEYLIST";
        }

        /**
         * @param name
         * @throws DuplicateDKVObjectException
         * @throws EmptyStringException
         */
        public void addKeyNamed(String name) throws EmptyStringException, DuplicateDKVObjectException
        {
            Domain.this.addKeyNamed(name);
        }

        /**
         * Overrides getClassNameKey
         * 
         * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
         */
        @Override
		public String getClassNameKey()
        {
            return "dkv_key_list";
        }

        public Domain getDomain()
        {
            return Domain.this;
        }

        /**
         * Overrides isDeleteAble
         * 
         * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
         */
        @Override
		public boolean isDeleteAble()
        {
            return false;
        }
        @Override
		public void undelete(){
        	
        }
        /**
         * Overrides getSpecificActionListForThatClass
         * @see org.openflexo.foundation.dkv.DKVObject#getSpecificActionListForThatClass()
         */
        @Override
		protected Vector<FlexoActionType> getSpecificActionListForThatClass()
        {
            Vector<FlexoActionType> v= super.getSpecificActionListForThatClass();
            v.add(AddKeyAction.actionType);
            return v;
        }

		@Override
		public Vector getAllEmbeddedValidableObjects() {
			return keys;
		}
    }

    public class ValueList extends DKVObject
    {

        /**
         * @param dl
         */
        public ValueList(DKVModel dl)
        {
            super(dl);
        }

        public Object[] getValues()
        {
            return Domain.this.getSortedValues();
        }

        /**
         * Overrides getFullyQualifiedName
         * 
         * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
         */
        @Override
		public String getFullyQualifiedName()
        {
            return getName() + ".VALUELIST";
        }

        /**
         * Overrides getClassNameKey
         * 
         * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
         */
        @Override
		public String getClassNameKey()
        {
            return "dkv_value_list";
        }

        /**
         * Overrides isDeleteAble
         * 
         * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
         */
        @Override
		public boolean isDeleteAble()
        {
            return false;
        }
        @Override
		public void undelete(){
        }

		@Override
		public Vector getAllEmbeddedValidableObjects() {
			Vector reply = new Vector();
			reply.addAll(values.values());
			return reply;
		}

    }

    public KeyList getKeyList()
    {
        return keyList;
    }

    public ValueList getValueList()
    {
        return valueList;
    }

    public class ValueHashtable<K,V> extends Hashtable<K,V>
    {

        /**
         * Calling this method will automatically add observers to all Values of
         * the Hashtable
         */
        public ValueHashtable(Hashtable<K,V> h)
        {
            super(h);
        }

        /**
         * 
         */
        public ValueHashtable()
        {
            super();
        }

        /**
         * Overrides put
         * 
         * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
         */
        @Override
		public synchronized V put(K key, V value)
        {
            ((Value) value).addObserver(Domain.this);
            return super.put(key, value);
        }

        /**
         * Overrides remove
         * 
         * @see java.util.Hashtable#remove(java.lang.Object)
         */
        @Override
		public synchronized V remove(Object key)
        {
            V o = super.remove(key);
            ((Value) o).deleteObserver(Domain.this);
            return o;
        }
        
        @Override
		public synchronized Enumeration keys() 
        { 
        	if (isSerializing()) {
                // Order keys in this case
                Vector<String> orderedKeys = new Vector<String>();
                for (Enumeration en = super.keys(); en.hasMoreElements();) {
                    orderedKeys.add((String) en.nextElement());
                }
                Collections.sort(orderedKeys, new Comparator<String>() {
                    @Override
					public int compare(String o1, String o2)
                    {
                        return Collator.getInstance().compare(o1, o2);
                    }
                });
                return orderedKeys.elements();
            }
            return super.keys();
       }

    }

    /**
     * Overrides getClassNameKey
     * 
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "dkv_domain";
    }

    /**
     * Overrides isDeleteAble
     * 
     * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
     */
    @Override
	public boolean isDeleteAble()
    {
        return true;
    }

    public String randomValue(){
    	int r = (new Random()).nextInt(keys.size());
    	return getValue((Key)(getKeyList().getKeyList()[r]), getDkvModel().getLanguages().get(0)).getDisplayString();
    }

    public void putValueForLanguage(Key key, String value, Language language) 
    {
        Value val = getValue(key, language);
        if (val == null) {
            val = new Value(getDkvModel(),key,language);
            setValue(val);
            key.notifyValueAdded(val);
        }
        val.setValue(value);
    }
    
    public Vector<Language> getLanguages()
    {
        return getDkvModel().getLanguages();
    }

	@Override
	public Vector getAllEmbeddedValidableObjects() {
		Vector reply = new Vector();
		reply.addAll(getKeys());
		return reply;
	}
}
