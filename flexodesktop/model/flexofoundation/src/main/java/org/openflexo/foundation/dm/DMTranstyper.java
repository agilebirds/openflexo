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
package org.openflexo.foundation.dm;


import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dm.DMType.KindOfType;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.dm.dm.PropertyUnregistered;
import org.openflexo.foundation.dm.javaparser.MethodSourceCode;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod;
import org.openflexo.foundation.dm.javaparser.ParsedJavadoc;
import org.openflexo.foundation.dm.javaparser.ParsedJavadocItem;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.dm.javaparser.SourceCodeOwner;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

public class DMTranstyper extends DMObject implements Typed, Bindable, SourceCodeOwner {

    protected static final Logger logger = Logger.getLogger(DMTranstyper.class.getPackage().getName());

    String name;
    DMEntity declaringEntity;
    DMType returnedType;

    Vector<DMTranstyperEntry> entries;
    Vector<DMTranstyperValue> values;
    
    TranstyperBindingModel bindingModel;

    private boolean isMappingDefined;
    
    // ===========================================================
	// ===================== Constructor =========================
	// ===========================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMTranstyper(FlexoDMBuilder builder)
	{
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMTranstyper(DMModel dmModel)
	{
		super(dmModel);
		entries = new Vector<DMTranstyperEntry>();
		values = new Vector<DMTranstyperValue>();
		bindingModel = new TranstyperBindingModel();
		updateValues();
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public DMTranstyper(DMModel dmModel, DMEntity aDeclaringEntity, String aName, DMType aType)
	{
		this(dmModel);
		declaringEntity = aDeclaringEntity;
		name = aName;
		returnedType = aType;
	}

	@Override
	public BindingModel getBindingModel() 
	{
		return bindingModel;
	}
	
	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public void setName(String aName)
	{
		if ((name == null) || !name.equals(aName)) {
			String oldName = name;
			name = aName;
	       	updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("name",oldName,aName));
		}
	}
	
    @Override
	public void setDescription(String aDescription)
    {
    	super.setDescription(aDescription);
    	updateCode();
   }
    
    @Override
	public void setSpecificDescriptionsForKey(String description, String key)
    {
    	super.setSpecificDescriptionsForKey(description, key);
    	updateCode();
    }
    
	public boolean getIsMappingDefined() 
	{
		return isMappingDefined && allowsMappingDefinition();
	}

	public void setIsMappingDefined(boolean aFlag) 
	{
		if (isMappingDefined != aFlag) {
			isMappingDefined = aFlag;
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("isMappingDefined",!aFlag,aFlag));
		}
	}
 	
	public boolean allowsMappingDefinition()
	{
		return allowsMappingDefinitionForType(getReturnedType());
	}
	
	public static boolean allowsMappingDefinitionForType(DMType type)
	{
		return ((type != null) && (type.getKindOfType() == KindOfType.RESOLVED));
	}
	
	@Override
	public boolean getAllowsChildren() 
	{
		return false;
	}

	@Override
	public Vector<DMObject> getEmbeddedDMObjects() 
	{
		return EMPTY_VECTOR;
	}

	@Override
	public Vector<DMObject> getOrderedChildren()
	{
		return EMPTY_VECTOR;
	}

	@Override
	public TreeNode getParent() 
	{
		return declaringEntity;
	}

	@Override
	public boolean isDeletable() 
	{
		return true;
	}

	@Override
	public String getClassNameKey() 
	{
		return "dm_transtyper";
	}

	@Override
	public String getFullyQualifiedName() 
	{
		return "TRANSTYPER."+(getDeclaringEntity()!=null?getDeclaringEntity().getFullQualifiedName():"null")+"."+name;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.DM.DM_TRANSTYPER_INSPECTOR;
	}

	@Override
	public DMType getType() 
	{
		return returnedType;
	}

	@Override
	public void setType(DMType type) 
	{
		setReturnedType(type);
	}

	public DMType getReturnedType() 
	{
		return returnedType;
	}

    public void setReturnedType(DMType aType)
    {
    	//logger.info("setReturnedType with "+aType+" was "+returnedType);
    	if (((aType == null) && (returnedType != null)) || ((aType != null) && !aType.equals(returnedType))) {
    		DMType oldType = returnedType;
    		returnedType = aType;
    		if (returnedType != null) {
				returnedType.setOwner(this);
			}
    		updateValues();
	       	updateCode();
    		setChanged();
    		notifyObservers(new DMAttributeDataModification("returnedType",oldType,returnedType));
    		if (getDMModel() != null) {
				getDMModel().notifyTranstyperTypeChanged(this, oldType, returnedType);
			}
    	}
    }

	public DMEntity getDeclaringEntity() 
	{
		return declaringEntity;
	}
	
	public void setDeclaringEntity(DMEntity entity) 
	{
		declaringEntity = entity;
	}
	
	public DMEntity getBaseEntity()
	{
		if (getReturnedType() != null) {
			return getReturnedType().getBaseEntity();
		}
		return null;
	}
	
	public Vector<DMTranstyperEntry> getEntries()
	{
		return entries;
	}

	public void setEntries(Vector<DMTranstyperEntry> someEntries) 
	{
		for (DMTranstyperEntry entry : someEntries) {
			addToEntries(entry);
		}
	}

	public void addToEntries(DMTranstyperEntry entry) 
	{
		entry._transtyper = this;
		entries.add(entry);
		updateValues();
      	updateCode();
		setChanged();
		notifyObservers(new DMAttributeDataModification("entries",null,null));
	}

	public void removeFromEntries(DMTranstyperEntry entry) 
	{
		entry._transtyper = null;
		entries.remove(entry);
		updateValues();
       	updateCode();
		setChanged();
		notifyObservers(new DMAttributeDataModification("entries",null,null));
	}

	 public DMTranstyperEntry addEntry() 
	 {
		 DMTranstyperEntry newEntry = new DMTranstyperEntry(getDMModel(),null);
		 newEntry.setName(FlexoLocalization.localizedForKey("entry")+(entries.size()+1));
		 addToEntries(newEntry);
		 return newEntry;
	 }

	 public void removeEntry(DMTranstyperEntry entry) 
	 {
		 removeFromEntries(entry);
	 }
	
	 public boolean addEntryEnabled(DMTranstyperEntry entry)
	 {
		 return true;
	 }

	 public boolean removeEntryEnabled(DMTranstyperEntry entry)
	 {
		 return true;
	 }

	 public Vector<DMTranstyperValue> getValues()
	 {
		 return values;
	 }

	 public void setValues(Vector<DMTranstyperValue> someValues) 
	 {
		 for (DMTranstyperValue value : someValues) {
			addToValues(value);
		}
	 }

	 public void addToValues(DMTranstyperValue value) 
	 {
		 value._transtyper = this;
		 values.add(value);
		 updateCode();
		 setChanged();
		 notifyObservers(new DMAttributeDataModification("values",null,null));
	 }

	 public void removeFromValues(DMTranstyperValue value) 
	 {
		 value._transtyper = null;
		 values.remove(value);
		 updateCode();
		 setChanged();
		 notifyObservers(new DMAttributeDataModification("values",null,null));
	 }

	 private DMEntity _observedEntity = null;
	 
	 private void updateValues()
	 { 
		 if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateValues() called for getBaseEntity()="+getBaseEntity());
		}
		 boolean modified = false;
		 if (getBaseEntity() == null) {
			 if (_observedEntity != null) {
				_observedEntity.deleteObserver(this);
			}
			 values.clear();
			 return;
		 }
		 else {
			 if (_observedEntity != getBaseEntity()) {
				 _observedEntity = getBaseEntity();
				 getBaseEntity().addObserver(this);
				 if (logger.isLoggable(Level.FINE)) {
					logger.fine("Registering as observer of "+getBaseEntity());
				}
			 }
		 }
		 Vector<DMTranstyperValue> valuesToRemove = new Vector<DMTranstyperValue>();
		 valuesToRemove.addAll(values);
		 for (DMProperty p : getBaseEntity().getAccessibleProperties()) {
			 if (p.getIsSettable()) {
				 boolean found = false;
				 for (DMTranstyperValue v : values) {
					 if (v.getProperty() == p) {
						 valuesToRemove.remove(v);
						 found = true;
					 }
				 }
				 if (!found) {
					 DMTranstyperValue newValue = new DMTranstyperValue(getDMModel(),this,p);
					 addToValues(newValue);
					 modified = true;
				 }
			 }
		 }
		 for (DMTranstyperValue v : valuesToRemove) {
			 removeFromValues(v);
			 modified = true;
		 }
		 if (modified) {
			 setChanged();
			 notifyObservers(new DMAttributeDataModification("values",null,null)); // lazy notification for inspector
		 }
	 }
	 
     @Override
	public void update(FlexoObservable observable, DataModification dataModification)
     {
    	 if ((dataModification instanceof PropertyRegistered) 
    			 || (dataModification instanceof PropertyUnregistered)) {
			updateValues();
		}
    	 super.update(observable, dataModification);
     }
     
 	@Override
	public void delete()
	{
		//logger.info(">>> delete() called for property "+hashCode()+" (is "+_implementationType+")");

		if(getDeclaringEntity()!=null){
			getDeclaringEntity().removeFromDeclaredTranstypers(this);
		}

		name = null;
		declaringEntity = null;
		returnedType = null;
		entries.clear();
		entries = null;
		values.clear();
		values = null;
		bindingModel = null;
		super.delete();
		deleteObservers();
	}
 	
    public String getTranstyperStringRepresentation()
    {
    	StringBuffer sb = new StringBuffer();
    	if (getEntries().size() > 1) {
			sb.append("(");
		}
    	boolean isFirst = true;
    	for (DMTranstyperEntry entry : getEntries()) {
    		sb.append((isFirst?"":",")+(entry.getType()!=null?entry.getType().getSimplifiedStringRepresentation():"???"));
    		isFirst = false;
    	}
    	if (getEntries().size() > 1) {
			sb.append(")");
		}
    	sb.append("->");
    	sb.append(getReturnedType()!=null?getReturnedType().getSimplifiedStringRepresentation():"???");
    	return sb.toString();
    }

    // ==========================================================================
    // ==================== DMTranstyperEntry implementation ====================
    // ==========================================================================

    public static class DMTranstyperEntry extends DMObject implements Typed, DMTypeOwner
    {
        private DMType _type;
        DMTranstyper _transtyper;
        private String _name;
        private final EntryBindingVariable _variable;

        /**
         * Constructor used during deserialization
         */
        public DMTranstyperEntry(FlexoDMBuilder builder)
        {
            this(builder.dmModel);
            initializeDeserialization(builder);
        }

        /**
         * Default constructor
         */
        public DMTranstyperEntry(DMModel dmModel)
        {
            super(dmModel);
            _variable = new EntryBindingVariable();
        }

        /**
         * Constructor used for dynamic creation
         */
        public DMTranstyperEntry(DMModel dmModel, DMTranstyper transtyper)
        {
            this(dmModel);
            this._transtyper = transtyper;
         }

    	@Override
		public void setIsModified()
    	{
    		if (ignoreNotifications()) {
				return;
			}
    		super.setIsModified();
            if (getTranstyper() != null) {
				getTranstyper().setIsModified();
			}
    	}


         @Override
		public String getName()
        {
            return _name;
        }
        
        @Override
		public void setName(String name) 
        {
        	if (((name == null) && (_name != null)) || ((name != null) && !name.equals(_name))) {
        		String oldName = _name;         	
        		_name = name;
    	       	if (_transtyper != null) {
					_transtyper.updateCode();
				}
         		setChanged();
        		notifyObservers(new DMAttributeDataModification("name",oldName,name));
        	}
        }

        @Override
		public void setDescription(String aDescription)
        {
        	super.setDescription(aDescription);
   	       	if (_transtyper != null) {
				_transtyper.updateCode();
			}
        }

        @Override
		public boolean isDeletable()
        {
        	return _transtyper.isDeletable();
        }

        public DMType getType() 
        {
        	return _type;
        }
        
         public void setType(DMType type)
        {
        	 if (((type == null) && (_type != null)) || ((type != null) && !type.equals(_type))) {
        		 DMType oldType = _type;
        		 _type = type;
        		 if (_type != null) {
					_type.setOwner(this);
				}
        		 if (_transtyper != null) {
					_transtyper.updateCode();
				}
        		 setChanged();
        		 notifyObservers(new DMAttributeDataModification("type",oldType,type));
         	 }
        }

        public DMTranstyper getTranstyper() 
        {
            return _transtyper;
        }

        public void setTranstyper(DMTranstyper transtyper)
        {
            _transtyper = transtyper;
        }

        @Override
		public String getFullyQualifiedName()
        {
            if (getTranstyper() != null) {
                return getTranstyper().getFullyQualifiedName() + "." + getName();
            }
            return "NULL." + getName();
        }

        @Override
		public Vector<DMObject> getOrderedChildren()
        {
            return EMPTY_VECTOR;
        }

        @Override
		public TreeNode getParent()
        {
            return getTranstyper();
        }

        @Override
		public boolean getAllowsChildren()
        {
            return false;
        }

        /**
         * Return null since parameter is never inspected by its own
         * 
         * @return null
         */
        public String getInspectorName()
        {
            return null;
        }

        /**
         * Return a Vector of embedded DMObjects at this level.
         * 
         * @return null
         */
        @Override
		public Vector<DMObject> getEmbeddedDMObjects()
        {
            return EMPTY_VECTOR;
        }

        /**
         * Overrides getClassNameKey
         * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
         */
        @Override
		public String getClassNameKey()
        {
            return "dm_transtyper_entry";
        }
        
         @Override
		public void update(FlexoObservable observable, DataModification dataModification)
        {
        	 super.update(observable, dataModification);
        }
        
          protected class EntryBindingVariable extends BindingVariable
         {
        	 protected EntryBindingVariable()
        	 {
        		 super(_transtyper,DMTranstyperEntry.this.getDMModel(),DMTranstyperEntry.this.getDescription());
        	 }
        	 
        	 @Override
        	public String getDescription() {
        		return DMTranstyperEntry.this.getDescription();
        	}
        	 
        	 @Override
        	public String getVariableName() {
        		return DMTranstyperEntry.this.getName();
        	}
        	 
        	 @Override
        	public DMType getType() {
         		return DMTranstyperEntry.this.getType();
        	}
        	 
        	 @Override
        	 public String getJavaAccess() {
        		 return getVariableName();
        	 }
         }

		public EntryBindingVariable getBindingVariable() 
		{
			return _variable;
		}
    }

    // ==========================================================================
    // ==================== DMTranstyperValue implementation ====================
    // ==========================================================================

    public static class DMTranstyperValue extends DMObject implements Bindable
    {
    	DMTranstyper _transtyper;
        private DMProperty _property;
        private AbstractBinding _propertyValue;
        private BindingDefinition _bindingDefinition;

        /**
         * Constructor used during deserialization
         */
        public DMTranstyperValue(FlexoDMBuilder builder)
        {
            this(builder.dmModel);
            initializeDeserialization(builder);
        }

        /**
         * Default constructor
         */
        public DMTranstyperValue(DMModel dmModel)
        {
            super(dmModel);
        }

        /**
         * Constructor used for dynamic creation
         */
        public DMTranstyperValue(DMModel dmModel, DMTranstyper transtyper, DMProperty property)
        {
            this(dmModel);
            _transtyper = transtyper;
            _property = property;
            updateBindingDefinition();
         }

        @Override
		public String getName()
        {
        	if (_property != null) {
				return _property.getName();
			}
            return null;
        }
        
        private String _deserializedPropertyName = null;
        
        // Used while deserializing
        @Override
		public void setName(String name) 
        {
        	_deserializedPropertyName = name;
        }

    	@Override
		public void setIsModified()
    	{
		if (ignoreNotifications()) {
			return;
		}
   		super.setIsModified();
            if (getTranstyper() != null) {
				getTranstyper().setIsModified();
			}
    	}

        @Override
		public boolean isDeletable()
        {
            return _transtyper.isDeletable();
        }

        public DMTranstyper getTranstyper() 
        {
            return _transtyper;
        }

        public void setTranstyper(DMTranstyper transtyper)
        {
            _transtyper = transtyper;
        }

        @Override
		public String getFullyQualifiedName()
        {
            if (getTranstyper() != null) {
                return getTranstyper().getFullyQualifiedName() + "." + getName();
            }
            return "NULL." + getName();
        }

        @Override
		public Vector<DMObject> getOrderedChildren()
        {
            return EMPTY_VECTOR;
        }

        @Override
		public TreeNode getParent()
        {
            return getTranstyper();
        }

        @Override
		public boolean getAllowsChildren()
        {
            return false;
        }

        /**
         * Return null since parameter is never inspected by its own
         * 
         * @return null
         */
        public String getInspectorName()
        {
            return null;
        }

        /**
         * Return a Vector of embedded DMObjects at this level.
         * 
         * @return null
         */
        @Override
		public Vector<DMObject> getEmbeddedDMObjects()
        {
            return EMPTY_VECTOR;
        }

        /**
         * Overrides getClassNameKey
         * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
         */
        @Override
		public String getClassNameKey()
        {
            return "dm_transtyper_value";
        }
        
         @Override
		public void update(FlexoObservable observable, DataModification dataModification)
        {
        	 super.update(observable, dataModification);
        }
        
 		public DMProperty getProperty() 
		{
			return _property;
		}

		public AbstractBinding getPropertyValue() 
		{
			return _propertyValue;
		}

		public void setPropertyValue(AbstractBinding propertyValue) 
		{
			AbstractBinding oldPropertyValue = _propertyValue;
			_propertyValue = propertyValue;
			if (_transtyper != null) {
				_transtyper.updateCode();
			}
			setChanged();
			notifyObservers(new DMAttributeDataModification("propertyValue",oldPropertyValue,propertyValue));
		}

		private String _deserializedPropertyValue = null;
		
		// Used for serialization/deserialization
		public String _getPropertyValueAsString() 
		{
			if (_propertyValue != null) {
				return _propertyValue.getStringRepresentation();
			}
			return _deserializedPropertyValue;
		}

		// Used for serialization/deserialization
		public void _setPropertyValueAsString(String propertyValueAsString) 
		{
			_deserializedPropertyValue = propertyValueAsString;
		}
		
		@Override
		public void finalizeDeserialization(Object builder) 
		{
			super.finalizeDeserialization(builder);
			if ((_deserializedPropertyName != null)
					&& (_transtyper != null)
					&& (_transtyper.getBaseEntity() != null)) {
				_property = _transtyper.getBaseEntity().getProperty(_deserializedPropertyName);
				updateBindingDefinition();
			}
			if (_deserializedPropertyValue != null) {
				getProject().getAbstractBindingConverter().setBindable(this);
				AbstractBinding binding = getProject().getAbstractBindingConverter().convertFromString(_deserializedPropertyValue);
				if (binding != null) {
					binding.setOwner(this);
					binding.setBindingDefinition(getBindingDefinition());
					setPropertyValue(binding);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Decoding "+_deserializedPropertyValue+" as "+getPropertyValue());
					}
				}
				else {
					logger.warning("Could not decode "+_deserializedPropertyValue);
				}
				_deserializedPropertyValue = null;
			}
		}

		public BindingModel getBindingModel() 
		{
			if (_transtyper != null) {
				return _transtyper.getBindingModel();
			}
			return null;
		}      
		
		public BindingDefinition getBindingDefinition()
		{
			return _bindingDefinition;
		}
		
		private void updateBindingDefinition()
		{
			_bindingDefinition = new BindingDefinition(_property.getName(),DMType.makeInstantiatedDMType(_property.getType(),_transtyper.getReturnedType()),this,BindingDefinitionType.GET,false);
		}
		
		public BindingAssignment buildAssignment(String variableName)
		{
			if (_property == null) {
				return null;
			}
			BindingValue receiver = new BindingValue(
					new BindingDefinition(_property.getName(),_property.getType(),this,BindingDefinitionType.SET,false),
					this);
			BindingVariable variable = new BindingVariable(this,getDMModel(),"")
			{
				@Override
				public String getJavaAccess() {
					return getVariableName();
				}
			};
			variable.setVariableName(variableName);
			variable.setType(DMType.makeResolvedDMType(_transtyper.getBaseEntity()));
			receiver.setBindingVariable(variable);
			receiver.addBindingPathElement(getProperty());
			return new BindingAssignment(receiver,getPropertyValue(),this);
		}
    }

    protected class TranstyperBindingModel extends BindingModel
    {

        @Override
		public int getBindingVariablesCount()
        {
            return entries.size();
        }

        @Override
		public BindingVariable getBindingVariableAt(int index)
        {
            return entries.elementAt(index).getBindingVariable();
        }

		@Override
		public boolean allowsNewBindingVariableCreation() {
			return false;
		}
    }
    
	@Override
	public void finalizeDeserialization(Object builder) 
	{
		super.finalizeDeserialization(builder);
		updateValues();
       	updateCode();
	}
	
    private MethodSourceCode sourceCode;
    
    public String getCode()
    {
    	return getSourceCode().getCode();
    }
	
    public void setCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException
    {
    	getSourceCode().setCode(someCode);
    	setChanged();
    	notifyObservers(new DMAttributeDataModification("code",null,someCode));
    }
    
    public void resetSourceCode() throws ParserNotInstalledException, DuplicateMethodSignatureException {
    	if (sourceCode!=null) {
			sourceCode.setCode("");
		}
    }

	public MethodSourceCode getSourceCode() 
	{
		if (sourceCode == null) {
			sourceCode = new MethodSourceCode(this) {
				@Override
				public String makeComputedCode() {
					return getJavadoc()+StringUtils.LINE_SEPARATOR+getMethodHeader()+" {"+StringUtils.LINE_SEPARATOR+getDefaultCoreCode()+StringUtils.LINE_SEPARATOR+"}";
				}
			    @Override
				public void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException
			    {
			    	
			    	logger.info(">>>>>>>>>>>> Interpret "+javaMethod);
			    	/*try {
			    		getJavaMethodParser().updateWith(DMMethod.this, javaMethod);
			    		
			    		if (!isResolvable()) {
                            setHasParseErrors(true);
                            setParseErrorWarning("<html><font color=\"red\">" + FlexoLocalization.localizedForKey("unresolved_type(s)")
                                    + " : " + getUnresolvedTypes() + "</font></html>");
                        }
                        DMMethod.this.setChanged();
                        DMMethod.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("code", null, getCode()));
                    } catch (DuplicateMethodSignatureException e) {
                        setHasParseErrors(true);
                        setParseErrorWarning("<html><font color=\"red\">"
                                + FlexoLocalization.localizedForKey("duplicated_method_signature")
				    			+"</font></html>");
			    		throw e;
					}*/
			    }
			 

			};
		}
		return sourceCode;
	}
	
	   String getJavadoc()
	    {
	    	StringBuffer javadoc = new StringBuffer();
	    	javadoc.append("/**"+StringUtils.LINE_SEPARATOR);
	    	if ((getDescription() != null) && (getDescription().trim().length() > 0)) {
				javadoc.append("  * "+ToolBox.getJavaDocString(getDescription(),"  "));
			}
	    	javadoc.append("  *"+StringUtils.LINE_SEPARATOR);
	    	
	    	Hashtable<String,String> specificDescriptions = getSpecificDescriptions();
	    	if ((specificDescriptions != null) && (specificDescriptions.size() > 0)) {
	    		for (String key : specificDescriptions.keySet()) {
	    			String specificDescription = ToolBox.getJavaDocString(specificDescriptions.get(key));
	    			if ((specificDescription == null) || specificDescription.trim().equals("")) {
						specificDescription = FlexoLocalization.localizedForKey("no_description");
					}
	   	   			javadoc.append(getTagAndParamRepresentation("doc",key,specificDescription));
	   		}
	        	javadoc.append("  *"+StringUtils.LINE_SEPARATOR);
	    	}

	    	if (getEntries().size() > 0) {
	    		for (DMTranstyperEntry entry : getEntries()) {
	    			String entryDescription = entry.getDescription();
	    			if ((entryDescription == null) || entryDescription.trim().equals("")) {
						entryDescription = FlexoLocalization.localizedForKey("no_description");
					}
	    			javadoc.append(getTagAndParamRepresentation("param",entry.getName(),ToolBox.getJavaDocString(entryDescription)));
	    		}
	     	}
	    	
	    	javadoc.append("  * @return "
	    			+getReturnedType().getSimplifiedStringRepresentation()
	    			+" "+FlexoLocalization.localizedForKey("newly_created_value")
	    			+StringUtils.LINE_SEPARATOR);
	    	
	    	javadoc.append("  */");
	    	
	    	return javadoc.toString();
	    }

	   public String getJavaMethodName()
	   {
		   return ToolBox.getJavaName(name);
	   }
	   
	    String getMethodHeader()
	    {
	    	StringBuffer methodHeader = new StringBuffer();
	    	methodHeader.append("public static ");
	    	methodHeader.append(getReturnedType()!=null?getReturnedType().getSimplifiedStringRepresentation():"void");
	    	methodHeader.append(" ");
	    	methodHeader.append(getJavaMethodName());
	    	methodHeader.append("(");
	    	methodHeader.append(getParameterNamedListAsString());
	    	methodHeader.append(")");
	    	return methodHeader.toString();
	     }

	    private String getParameterNamedListAsString()
	    {
	    	StringBuffer returned = new StringBuffer();
	    	boolean isFirst = true;
	    	for (DMTranstyperEntry entry : getEntries()) {
	    		String paramName = entry.getName();
	    		String typeName = "";
	    		if (entry.getType() != null) {
					typeName = entry.getType().getSimplifiedStringRepresentation();
				}
	    		returned.append((isFirst?"":",")+typeName+" "+paramName);
	    		isFirst = false;
	    	}
	    	return returned.toString();
	    }


	    String getDefaultCoreCode() 
	    {
	    	if (getIsMappingDefined()) {
	    		StringBuffer sb = new StringBuffer();
	    		sb.append ("    "+StringUtils.LINE_SEPARATOR);
	    		sb.append ("    // "+FlexoLocalization.localizedForKey("build_new_object")+StringUtils.LINE_SEPARATOR);
	    		sb.append ("    "+getReturnedType().getSimplifiedStringRepresentation()+" returned = new "+getReturnedType()+"();"+StringUtils.LINE_SEPARATOR);
	    		sb.append ("    "+StringUtils.LINE_SEPARATOR);
	    		sb.append ("    // "+FlexoLocalization.localizedForKey("perform_data_mapping")+StringUtils.LINE_SEPARATOR);
	    		for (DMTranstyperValue value : getValues()) {
	    			if ((value.getProperty() != null) && (value.getPropertyValue() != null)) {
						sb.append ("    "+value.buildAssignment("returned").getJavaStringRepresentation()+StringUtils.LINE_SEPARATOR);
					}
	    		}
	    		sb.append ("    "+StringUtils.LINE_SEPARATOR);
	    		sb.append ("    // "+FlexoLocalization.localizedForKey("return_object")+StringUtils.LINE_SEPARATOR);
	    		sb.append ("    return returned;");	    		
	    		return sb.toString();
	    	}
	    	else {
	    		return "    // "+FlexoLocalization.localizedForKey("please_define_your_own_code_here")
	    		+StringUtils.LINE_SEPARATOR+ "    return null;";
	    	}
		}

	    protected void updateCode()
	    {
	    	if (isDeserializing()) {
				return;
			}
	    	
	          String oldCode = getSourceCode().getCode();
	        
	         if (getSourceCode().isEdited()) {
	        	 getSourceCode().replaceMethodDeclarationInEditedCode(getMethodHeader());
	        	 ParsedJavadoc jd;
	        	 try {
	        		 jd = getSourceCode().parseJavadoc(oldCode);
	        		 
	        		 if (jd != null)  {

	        			 jd.setComment(getDescription());

	        			 Hashtable<String,String> specificDescriptions = getSpecificDescriptions();
	        			 if ((specificDescriptions != null) && (specificDescriptions.size() > 0)) {
	        				 for (String key : specificDescriptions.keySet()) {
	        					 String specificDescription = ToolBox.getJavaDocString(specificDescriptions.get(key));
	        					 if ((specificDescription == null) || specificDescription.trim().equals("")) {
									specificDescription = FlexoLocalization.localizedForKey("no_description");
								}
	        					 ParsedJavadocItem jdi = jd.getTagByName("doc", key);
	        					 if (jdi != null) {
									jdi.setParameterValue(specificDescription);
								} else {
									jd.addTagForNameAndValue("doc", key,specificDescription, true);
								}
	        				 }
	        			 }

	        			 if (getEntries().size() > 0) {
	        				 for (DMTranstyperEntry entry : getEntries()) {
	        					 ParsedJavadocItem jdi = jd.getTagByName("param", entry.getName());
	        					 if (jdi != null) {
	        						 String entryDescription = entry.getDescription();
	        						 if ((entryDescription == null) || entryDescription.trim().equals("")) {
										entryDescription = FlexoLocalization.localizedForKey("no_description");
									}
	        						 jdi.setParameterValue(ToolBox.getJavaDocString(entryDescription));
	        					 }
	        					 else {
	        						 jd.addTagForNameAndValue("param", entry.getName(), ToolBox.getJavaDocString(entry.getDescription()),false);
	        					 }
	        				 }
	        			 }

	        			 getSourceCode().replaceJavadocInEditedCode(jd);
	        		 }
	        		 
	        		 else {
	        			 getSourceCode().replaceJavadocInEditedCode(getJavadoc()+StringUtils.LINE_SEPARATOR);
	        		 }
	        	 } 
	        	 catch (ParserNotInstalledException e) {
	        		 logger.warning("JavaParser not installed");
	        	 }
	         }
	         else {
	         	getSourceCode().updateComputedCode();
	         }

	          setChanged();
	         notifyObservers(new DMAttributeDataModification("code",oldCode,getSourceCode().getCode()));
	    }

		public boolean codeIsComputable() {
			return true;
		}


}
