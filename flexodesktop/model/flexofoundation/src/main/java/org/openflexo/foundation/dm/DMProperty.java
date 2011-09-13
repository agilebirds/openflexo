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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreeNode;


import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.dm.DMEntity.DMTypeVariable;
import org.openflexo.foundation.dm.DMType.KindOfType;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMPropertyNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.dm.PropertyDeleted;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.javaparser.FieldSourceCode;
import org.openflexo.foundation.dm.javaparser.MethodSourceCode;
import org.openflexo.foundation.dm.javaparser.ParsedJavaField;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod;
import org.openflexo.foundation.dm.javaparser.ParsedJavadoc;
import org.openflexo.foundation.dm.javaparser.ParsedJavadocItem;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.dm.javaparser.SourceCodeOwner;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents an accessor as a get/set key-value pair
 *
 * @author sguerin
 *
 */
public class DMProperty extends DMObject implements Typed, BindingValue.BindingPathElement, DMGenericDeclaration, DMTypeOwner, DMMember, SourceCodeOwner
{

	static final Logger logger = Logger.getLogger(DMEntity.class.getPackage().getName());

	private static final String COMPILED_CODE = "    /* compiled code not available */";
	private static final String COMPILED_CODE_IN_JAVADOC = "< compiled code >";

	// ==============================================================
	// ================== Instance variables ========================
	// ==============================================================

	protected String name;

	private DMType _type;

	private DMCardinality _cardinality;

	protected boolean _isReadOnly;

	private boolean _isSettable;

	private DMPropertyImplementationType _implementationType;

	private DMEntity entity;

	private String _fieldName;

	private boolean _underscoredAccessors = false;
	private boolean isStatic = false;

	protected DMType _keyType;

	private boolean _isStaticallyDefinedInTemplate = false;

	// ===========================================================
	// ===================== Constructor =========================
	// ===========================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMProperty(FlexoDMBuilder builder)
	{
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMProperty(DMModel dmModel)
	{
		super(dmModel);
		_isSettable = true;
		//logger.info("Created property "+hashCode());
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public DMProperty(DMModel dmModel,/* DMEntity entity, */String name, DMType type, DMCardinality cardinality, boolean isReadOnly,
			boolean isSettable, DMPropertyImplementationType implementationType)
	{
		this(dmModel);
		// this.entity = entity;
		this.name = name;
		_fieldName = name;
		_type = type;
		_cardinality = cardinality;
		_isReadOnly = isReadOnly;
		_isSettable = isSettable;
		_implementationType = implementationType;
	}

	@Override
	public void delete()
	{
		//logger.info(">>> delete() called for property "+hashCode()+" (is "+_implementationType+")");

		if(getEntity()!=null){
			getEntity().unregisterProperty(this);
		}

		if (_type!=null)
			_type.removeFromTypedWithThisType(this);
		setChanged();
		notifyObservers(new PropertyDeleted(this));
		name = null;
		entity = null;
		_type = null;
		_cardinality = null;
		_implementationType = null;
		super.delete();
		deleteObservers();
	}

	@Override
	public boolean isDeletable()
	{
		if (getEntity() == null)
			return true;
		return !getIsReadOnly();
	}

	@Override
	public String getFullyQualifiedName()
	{
		if (getEntity() != null) {
			return getEntity().getFullyQualifiedName() + "." + name;
		}
		return "NULL." + name;
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

	public String getSerializationRepresentation()
	{
		return getName();
	}

	/**
	 * Return String uniquely identifying inspector template which must be
	 * applied when trying to inspect this object
	 *
	 * @return a String value
	 */
	public String getInspectorName()
	{
		if (getIsReadOnly()) return Inspectors.DM.DM_RO_PROPERTY_INSPECTOR;
		if (getDMRepository()==null || getDMRepository().isReadOnly())
			return Inspectors.DM.DM_RO_PROPERTY_INSPECTOR;
		else
			return Inspectors.DM.DM_PROPERTY_INSPECTOR;
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

	public DMRepository getDMRepository()
	{
		if (getEntity() != null) {
			return getEntity().getRepository();
		}
		return null;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String newName) throws InvalidNameException, DuplicatePropertyNameException
	{
		if ((name == null) || (!name.equals(newName))) {
			if (!isDeserializing() && (newName==null || !DMRegExp.ENTITY_NAME_PATTERN.matcher(newName).matches()))
				throw new InvalidNameException("'"+newName+"' is not a valid name for property.");
			DMEntity containerEntity = getEntity();
			boolean isBindable = containerEntity instanceof ComponentDMEntity && ((ComponentDMEntity)containerEntity).isBindable(this);
			boolean mandatory = containerEntity instanceof ComponentDMEntity && ((ComponentDMEntity)containerEntity).isMandatory(this);
			boolean settable = containerEntity instanceof ComponentDMEntity && ((ComponentDMEntity)containerEntity).isSettable(this);
			if (containerEntity != null) {

				if (containerEntity.getProperties().get(newName) != null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("You are trying to redefine property "+newName+"("+getType()+") "
								+" but property "+newName+"("+containerEntity.getProperties().get(newName).getType()+") is already existing");
					}
					throw new DuplicatePropertyNameException(newName);
				}

				containerEntity.unregisterProperty(this, false);
			}
			String oldName = name;
			name = newName;
			if (oldName==null && getFieldName()==null)
				setFieldName(newName);
			else if (getFieldName()!=null && getFieldName().equals(oldName))
				setFieldName(newName);
			// logger.fine("Change "+oldName+" to "+newName);
			if (containerEntity != null) {
				containerEntity.registerProperty(this, false,isBindable);
				if(containerEntity instanceof ComponentDMEntity){
					((ComponentDMEntity)containerEntity).setSettable(this, settable);
					((ComponentDMEntity)containerEntity).setMandatory(this, mandatory);
				}
			}
			updateCode();
			setChanged();
			notifyObservers(new DMPropertyNameChanged(this, oldName, newName));
			if (containerEntity != null) {
				containerEntity.notifyReordering(this);
			}
		}
	}

	public DMCardinality getCardinality()
	{
		return _cardinality;
	}

	public void setCardinality(DMCardinality cardinality)
	{
		DMCardinality oldCardinality = _cardinality;
		_cardinality = cardinality;
		updateCode();
		setChanged();
		notifyObservers(new DMAttributeDataModification("cardinality", oldCardinality, cardinality));
	}

	public DMEntity getEntity()
	{
		return entity;
	}

	public void setEntity(DMEntity entity)
	{
		this.entity = entity;
		setChanged();
	}

	public boolean getIsReadOnly()
	{
		if (getIsStaticallyDefinedInTemplate()) return true;
		if (getEntity() != null && getEntity().getIsReadOnly()) return true;
		return _isReadOnly;
	}

	public void setIsReadOnly(boolean isReadOnly)
	{
		boolean oldReadOnly = _isReadOnly;
		if (oldReadOnly != isReadOnly) {
			_isReadOnly = isReadOnly;
			setChanged();
			notifyObservers(new DMAttributeDataModification("isReadOnly", new Boolean(oldReadOnly), new Boolean(isReadOnly)));
		}
	}

	public boolean isSettable()
	{
		return getIsSettable();
	}

	public boolean getIsSettable()
	{
		return _isSettable;
	}

	public void setIsSettable(boolean isSettable)
	{
		boolean oldSettable = _isSettable;
		_isSettable = isSettable;
		updateCode();
		setChanged();
		notifyObservers(new DMAttributeDataModification("isSettable", new Boolean(oldSettable), new Boolean(isSettable)));
	}

	public DMPropertyImplementationType getImplementationType()
	{
		//logger.info("getImplementationType() called for property "+hashCode()+" (is "+_implementationType+")");
		return _implementationType;
	}

	public boolean isPublicField(){
		return DMPropertyImplementationType.PUBLIC_FIELD.equals(getImplementationType());
	}
	public boolean isProtectedField(){
		return DMPropertyImplementationType.PROTECTED_FIELD.equals(getImplementationType());
	}
	public boolean isPublicStaticFinalField(){
		return DMPropertyImplementationType.PUBLIC_STATIC_FINAL_FIELD.equals(getImplementationType());
	}
	public boolean isPublicAccessorProtectedField(){
		return DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD.equals(getImplementationType());
	}
	public boolean isPublicAccessorPrivateField(){
		return DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD.equals(getImplementationType());
	}
	public boolean isPublicAccessorOnly(){
		return DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY.equals(getImplementationType());
	}

	public void setImplementationType(DMPropertyImplementationType implementationType)
	{
		//logger.info(">>> setImplementationType() called for property "+hashCode()+" ("+getName()+" is "+implementationType+")");
		DMPropertyImplementationType oldImplementationType = _implementationType;
		if (oldImplementationType != implementationType) {
			_implementationType = implementationType;
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("implementationType", oldImplementationType, implementationType));
		}
	}

	public boolean isMultipleCardinality()
	{
		return getCardinality().isMultiple();
	}

	private DMType _vectorResultingType;
	private DMType _hashtableResultingType;

	/**
	 * Return resulting type of this property, which refer both to base type and cardinality
	 * If cardinality is SINGLE, resulting type equals type
	 * @return DMType, resulting type
	 */
	public DMType getResultingType()
	{
		if (getCardinality() == DMCardinality.SINGLE) {
			return getType();
		}
		else if (getCardinality() == DMCardinality.VECTOR) {
			if (_vectorResultingType == null && !isDeserializing()) {
				_vectorResultingType = DMType.makeVectorDMType(getType(),getProject());
				if (getType() != null)
					_vectorResultingType.setParameterAtIndex(getType(), 0);
			}
			return _vectorResultingType;
		}
		else if (getCardinality() == DMCardinality.HASHTABLE) {
			if (_hashtableResultingType == null && !isDeserializing()) {
				_hashtableResultingType = DMType.makeHashtableDMType(getKeyType(),getType(),getProject());
				if (getKeyType() != null)
					_hashtableResultingType.setParameterAtIndex(getKeyType(), 0);
				if (getType() != null)
					_hashtableResultingType.setParameterAtIndex(getType(), 1);
			}
			return _hashtableResultingType;
		}
		return null;
	}

	public String getTypeStringRepresentation()
	{
		if (getResultingType() == null) return FlexoLocalization.localizedForKey("no_type");
		else return getResultingType().getSimplifiedStringRepresentation();
	}

	//private String typeAsString;

	/**
	 * Return type of this property
	 */
	public DMType getType()
	{
		/*if (_type==null && typeAsString!=null) {
			setType(getDMModel().getDmTypeConverter().convertFromString(typeAsString),false);
			typeAsString = null;
		}*/
		return _type;
	}

	public void setType(DMType type)
	{
		setType(type, true);
	}

	public void setType(DMType type, boolean notify)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("setType in "+name+" with "+type.getStringRepresentation());
		if ((type == null && _type != null) || (type != null && !type.equals(_type))) {
			DMType oldType = _type;
			if (oldType != null) {
				oldType.removeFromTypedWithThisType(this);
			}
			_type = type;
			if (_type != null) {
				_type.setOwner(this);
				_type.addToTypedWithThisType(this);
				/*if (_type.getName() != null && (_type.getName().equals("Vector") || _type.getName().equals("NSArray")))
                    setCardinality(DMCardinality.VECTOR);*/
			}
			if (notify) {
				updateCode();
				setChanged();
				notifyObservers(new DMAttributeDataModification("type", oldType, type));
			}
		}
	}

	/*public String getTypeAsString()
	{
		if (getType()!=null)
			return getDMModel().getDmTypeConverter().convertToString(getType());
		else
			return null;
	}

	public void setTypeAsString(String type)
	{
		this.typeAsString = type;
	}*/

	/**
	 * @deprecated Use getType() instead, kept for backward compatibility in XML mappings
	 * @return DMEntity
	 */
	@Deprecated
	public DMEntity getTypeBaseEntity()
	{
		if (getType() != null) return getType().getBaseEntity();
		return null;
	}

	/**
	 * @deprecated Use setType(DMType) instead, kept for backward compatibility in XML mappings
	 * @param anEntity
	 */
	@Deprecated
	public void setTypeBaseEntity(DMEntity anEntity)
	{
		setType(DMType.makeResolvedDMType(anEntity));
	}

	public boolean overrides(DMProperty property)
	{
		if ((property == null) || (property.getEntity() == null) || (property.getName() == null))
			return false;
		return (property.getEntity().isAncestorOf(getEntity()) && (property.getName().equals(getName())));
	}

	public String getStringRepresentation()
	{
		return name + " : " + _cardinality.getName() + " of " + getType().getName();
	}

	/**
	 * Update this property given an other property. This method updates only
	 * data extracted from LoadableDMEntity features and exclude many properties
	 * such as description.
	 * @throws InvalidNameException
	 * @throws DuplicatePropertyNameException
	 */
	public void update(DMProperty property, boolean updateDescription) throws InvalidNameException, DuplicatePropertyNameException
	{
		if (logger.isLoggable(Level.FINE)) logger.fine("Update "+getName()+" with "+property.getName());

		// Name is supposed to be the same, but check anyway
		if (!getName().equals(property.getName())) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Update name");
			setName(property.getName());
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Name is up-to-date");
		}

		// Cardinality
		if (!getCardinality().equals(property.getCardinality())) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Update cardinality");
			setCardinality(property.getCardinality());
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Cardinality is up-to-date");
		}
		// Type
		if ((getType() == null) || (!getType().equals(property.getType()))) {
			if (logger.isLoggable(Level.FINE))logger.fine("Update type from "+getType()+" to "+property.getType());
			setType(property.getType());
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Type is up-to-date");
		}
		// Key-Type
		if ((getKeyType() == null) || (!getKeyType().equals(property.getKeyType()))) {
			if (property.getKeyType() != null) {
				if (logger.isLoggable(Level.FINE))logger.fine("Update key type from "+getKeyType()+" to "+property.getKeyType());
				setKeyType(property.getKeyType(), true);
			}
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Key-type is up-to-date");
		}
		// Read-only
		if (getIsReadOnly() != property.getIsReadOnly()) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Update IsReadOnly");
			setIsReadOnly(property.getIsReadOnly());
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("IsReadOnly is up-to-date");
		}
		// Settable
		if (getIsSettable() != property.getIsSettable()) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Update IsSettable");
			setIsSettable(property.getIsSettable());
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("IsSettable is up-to-date");
		}
		// Implementation type
		if (getImplementationType() == null) {
			if (property.getImplementationType() != null)
				setImplementationType(property.getImplementationType());
		}
		else if (!getImplementationType().equals(property.getImplementationType())) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Update ImplementationType");
			setImplementationType(property.getImplementationType());
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("ImplementationType is up-to-date");
		}


		// Descriptions
		if (updateDescription) {
			if ((getDescription() == null && property.getDescription() != null)
					|| (getDescription() != null && !getDescription().equals(property.getDescription()))) {
				setDescription(property.getDescription());
			}
			for (String descriptionKey : property.getSpecificDescriptions().keySet()) {
				String description = property.getSpecificDescriptionForKey(descriptionKey);
				if ((description == null && getSpecificDescriptionForKey(descriptionKey) != null)
						|| (description != null && !description.equals(getSpecificDescriptionForKey(descriptionKey)))) {
					setSpecificDescriptionsForKey(description,descriptionKey);
				}
			}
		}

		// Code

		logger.info("Updating property "+getName()+" with "+property);

		if (getImplementationType().requiresField()) {
			try {
				if (getFieldSourceCode().getCode() == null
						&& property.getFieldSourceCode().getCode() != null)
					getFieldSourceCode().setCode(property.getFieldSourceCode().getCode());
				if (getFieldSourceCode().getCode() != null
						&& !getFieldSourceCode().getCode().equals(property.getFieldSourceCode().getCode()))
					getFieldSourceCode().setCode(property.getFieldSourceCode().getCode());
			} catch (ParserNotInstalledException e) {
				e.printStackTrace();
			} catch (DuplicateMethodSignatureException e) {
				logger.warning("Unexpected DuplicateMethodSignatureException");
				e.printStackTrace();
			}
		}

		if (getImplementationType().requiresAccessors()) {

			try {
				if (getGetterSourceCode().getCode() == null
						&& property.getGetterSourceCode().getCode() != null)
					getGetterSourceCode().setCode(property.getGetterSourceCode().getCode());
				if (getGetterSourceCode().getCode() != null
						&& !getGetterSourceCode().getCode().equals(property.getGetterSourceCode().getCode()))
					getGetterSourceCode().setCode(property.getGetterSourceCode().getCode());
			} catch (ParserNotInstalledException e) {
				e.printStackTrace();
			} catch (DuplicateMethodSignatureException e) {
				logger.warning("Unexpected DuplicateMethodSignatureException");
				e.printStackTrace();
			}

			if (getIsSettable()) {
				try {
					if (getSetterSourceCode().getCode() == null
							&& property.getSetterSourceCode().getCode() != null)
						getSetterSourceCode().setCode(property.getSetterSourceCode().getCode());
					if (getSetterSourceCode().getCode() != null
							&& !getSetterSourceCode().getCode().equals(property.getSetterSourceCode().getCode()))
						getSetterSourceCode().setCode(property.getSetterSourceCode().getCode());
				} catch (ParserNotInstalledException e) {
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					logger.warning("Unexpected DuplicateMethodSignatureException");
					e.printStackTrace();
				}
				if ((getSetterParamName() != null && property.getSetterParamName() == null)
						|| (getSetterParamName() == null && property.getSetterParamName() != null)
						|| (getSetterParamName() != null && !getSetterParamName().equals(property.getSetterParamName())))
					setSetterParamName(property.getSetterParamName());
				}
				if ((getSetterParamName() != null && property.getSetterParamName() == null)
						|| (getSetterParamName() == null && property.getSetterParamName() != null)
						|| (getSetterParamName() != null && !getSetterParamName().equals(property.getSetterParamName())))
					setSetterParamName(property.getSetterParamName());
			}

			if (getCardinality().isMultiple()) {
				try {
					if (getAdditionSourceCode().getCode() == null
							&& property.getAdditionSourceCode().getCode() != null)
						getAdditionSourceCode().setCode(property.getAdditionSourceCode().getCode());
					if (getAdditionSourceCode().getCode() != null
							&& !getAdditionSourceCode().getCode().equals(property.getAdditionSourceCode().getCode()))
						getAdditionSourceCode().setCode(property.getAdditionSourceCode().getCode());
				} catch (ParserNotInstalledException e) {
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					logger.warning("Unexpected DuplicateMethodSignatureException");
					e.printStackTrace();
				}
				if ((getAdditionAccessorParamName() != null && property.getAdditionAccessorParamName() == null)
						|| (getAdditionAccessorParamName() == null && property.getAdditionAccessorParamName() != null)
						|| (getAdditionAccessorParamName() != null && !getAdditionAccessorParamName().equals(property.getAdditionAccessorParamName())))
					setAdditionAccessorParamName(property.getAdditionAccessorParamName());

				try {
					if (getRemovalSourceCode().getCode() == null
							&& property.getRemovalSourceCode().getCode() != null)
						getRemovalSourceCode().setCode(property.getRemovalSourceCode().getCode());
					if (getRemovalSourceCode().getCode() != null
							&& !getRemovalSourceCode().getCode().equals(property.getRemovalSourceCode().getCode()))
						getRemovalSourceCode().setCode(property.getRemovalSourceCode().getCode());
				} catch (ParserNotInstalledException e) {
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					logger.warning("Unexpected DuplicateMethodSignatureException");
					e.printStackTrace();
				}
				if ((getRemovalAccessorParamName() != null && property.getRemovalAccessorParamName() == null)
						|| (getRemovalAccessorParamName() == null && property.getRemovalAccessorParamName() != null)
						|| (getRemovalAccessorParamName() != null && !getRemovalAccessorParamName().equals(property.getRemovalAccessorParamName())))
					setRemovalAccessorParamName(property.getRemovalAccessorParamName());

			}
				if ((getRemovalAccessorParamName() != null && property.getRemovalAccessorParamName() == null)
						|| (getRemovalAccessorParamName() == null && property.getRemovalAccessorParamName() != null)
						|| (getRemovalAccessorParamName() != null && !getRemovalAccessorParamName().equals(property.getRemovalAccessorParamName())))
					setRemovalAccessorParamName(property.getRemovalAccessorParamName());

		if (logger.isLoggable(Level.FINE)) logger.fine("Update "+getName()+" with "+property.getName()+": DONE");

	}

	// ==========================================================
	// ==================== Code management =====================
	// ==========================================================


	protected void updateCode()
	{
		if (isDeserializing()) return;
		if(!codeIsComputable()) return;

		if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_STATIC_FINAL_FIELD)) {
			updateFieldCode();
		}

		updateGetterCode();

		if (getIsSettable()) {
			updateSetterCode();
		}

		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			updateAdditionCode();
			updateRemovalCode();
		}

	}

	public boolean getIsStatic()
	{
		return isStatic;
	}

	public void setIsStatic(boolean isStatic)
	{
		if (isStatic != this.isStatic) {
			this.isStatic = isStatic;
			setChanged();
			notifyObservers(new DMAttributeDataModification("isStatic",!isStatic,isStatic));
			updateCode();
		}
	}



	public boolean getIsUnderscoredAccessors()
	{
		return _underscoredAccessors;
	}

	public void setIsUnderscoredAccessors(boolean underscoredAccessors)
	{
		if (_underscoredAccessors != underscoredAccessors) {
			_underscoredAccessors = underscoredAccessors;
			if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
					|| (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD)
					|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_STATIC_FINAL_FIELD)) {
				if (underscoredAccessors && getFieldName().indexOf("_") != 0) {
					setFieldName("_"+getFieldName(),false);
				}
				else if (!underscoredAccessors && getFieldName().indexOf("_") == 0) {
					setFieldName(getFieldName().substring(1),false);
				}
			}
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("isUnderscoredAccessors",!underscoredAccessors,underscoredAccessors));
		}
	}

	public String getFieldName()
	{
		if (_fieldName == null) {
			setFieldName((_underscoredAccessors ? "_" : "") + getName(),false,false);
		}
		return _fieldName;
	}

	public void setFieldName(String fieldName)
	{
		setFieldName(fieldName,true);
	}

	public void setFieldName(String fieldName, boolean updateCode)
	{
		setFieldName(fieldName, updateCode, true);
	}

	public void setFieldName(String fieldName, boolean updateCode, boolean notify) {
		String oldFieldName = _fieldName;
		_fieldName = fieldName;
		if(updateCode)updateCode();
        if (notify) {
		setChanged();
		notifyObservers(new DMAttributeDataModification("fieldName", oldFieldName, fieldName));
	}
	}

	//private String keyTypeAsString;

	public DMType getKeyType()
	{
		/*if (_keyType==null && keyTypeAsString!=null){
			setKeyType(getDMModel().getDmTypeConverter().convertFromString(keyTypeAsString), false);
			keyTypeAsString = null;
		}*/
		return _keyType;
	}

	public void setKeyType(DMType keyType)
	{
		setKeyType(keyType, true);
	}

	public void setKeyType(DMType keyType, boolean notify)
	{
		if ((keyType == null && _keyType != null) || (keyType != null && !keyType.equals(_keyType))) {
			DMType oldType = _keyType;
			/*
			 * if (oldType != null) { oldType.removeFromTypedWithThisType(this); }
			 */
			_keyType = keyType;
			if (_keyType != null) _keyType.setOwner(this);
			/*
			 * if (keyType != null) { keyType.addToTypedWithThisType(this); }
			 */
			if (notify) {
				updateCode();
				setChanged();
				notifyObservers(new DMAttributeDataModification("type", oldType, keyType));
			}
		}
	}

	/*public String getKeyTypeAsString()
	{
		if (getKeyType()!=null)
			return getDMModel().getDmTypeConverter().convertToString(getKeyType());
		else
			return null;
	}

	public void setKeyTypeAsString(String keyType)
	{
		keyTypeAsString = keyType;
	}*/

	/**
	 * @deprecated Use getKeyType() instead, kept for backward compatibility in XML mappings
	 * @return DMEntity
	 */
	@Deprecated
	public DMEntity getKeyTypeBaseEntity()
	{
		if (getKeyType() != null) return getKeyType().getBaseEntity();
		return null;
	}

	/**
	 * @deprecated Use setKeyType(DMType) instead, kept for backward compatibility in XML mappings
	 * @param anEntity
	 */
	@Deprecated
	public void setKeyTypeBaseEntity(DMEntity anEntity)
	{
		setKeyType(DMType.makeResolvedDMType(anEntity), true);
	}


	/* public String getGetterCode()
    {
        return _getterCode;
    }

    public void setGetterCode(String getterCode)
    {
        if (_getterCode == null) {
            if ((getGetterDefaultCode() != null) && (getGetterDefaultCode().equals(getterCode))) {
                return;
            }
        } else if (_getterCode.equals(getterCode)) {
            return;
        }
        _getterCode = getterCode;
        if (getterCode.equals(""))
            _getterCode = null;
        setChanged();
    }*/

	/*public String getSetterCode()
    {
        return _setterCode;
    }

    public void setSetterCode(String setterCode)
    {
        if (_setterCode == null) {
            if ((getSetterDefaultCode() != null) && (getSetterDefaultCode().equals(setterCode))) {
                return;
            }
        } else if (_setterCode.equals(setterCode)) {
            return;
        }
        _setterCode = setterCode;
        if (setterCode.equals(""))
            _setterCode = null;
        setChanged();
    }*/

	/*public String getAdditionAccessorCode()
    {
        return _additionAccessorCode;
    }

    public void setAdditionAccessorCode(String additionAccessorCode)
    {
        if (_additionAccessorCode == null) {
            if ((getAdditionAccessorDefaultCode() != null) && (getAdditionAccessorDefaultCode().equals(additionAccessorCode))) {
                return;
            }
        } else if (_additionAccessorCode.equals(additionAccessorCode)) {
            return;
        }
        _additionAccessorCode = additionAccessorCode;
        if (additionAccessorCode.equals(""))
            _additionAccessorCode = null;
        setChanged();
    }*/

	/*public String getRemovingAccessorCode()
    {
        return _removingAccessorCode;
    }

    public void setRemovingAccessorCode(String removingAccessorCode)
    {
        if (_removingAccessorCode == null) {
            if ((getRemovingAccessorDefaultCode() != null) && (getRemovingAccessorDefaultCode().equals(removingAccessorCode))) {
                return;
            }
        } else if (_removingAccessorCode.equals(removingAccessorCode)) {
            return;
        }
        _removingAccessorCode = removingAccessorCode;
        if (removingAccessorCode.equals(""))
            _removingAccessorCode = null;
        setChanged();
    }*/

	public boolean isBoolean()
	{
		return (getType() != null && getType().isBoolean());
	}

	public String getGetterName()
	{
		if (isBoolean()) {
			return (_underscoredAccessors ? "_" : "") + getName();
		}
		return (_underscoredAccessors ? "_" : "") + "get" + getCapitalizedName();
	}

	public String getSetterName()
	{
		return (_underscoredAccessors ? "_" : "") + "set" + getCapitalizedName();
	}

	public String getAdditionAccessorName()
	{
		if (getCardinality() == DMCardinality.VECTOR) {
			return (_underscoredAccessors ? "_" : "") + "addTo" + getCapitalizedName();
		} else if (getCardinality() == DMCardinality.HASHTABLE) {
			return (_underscoredAccessors ? "_" : "") + "set" + getCapitalizedName() + "ForKey";
		} else {
			return "";
		}
	}

	public String getRemovingAccessorName()
	{
		if (getCardinality() == DMCardinality.VECTOR) {
			return (_underscoredAccessors ? "_" : "") + "removeFrom" + getCapitalizedName();
		} else if (getCardinality() == DMCardinality.HASHTABLE) {
			return (_underscoredAccessors ? "_" : "") + "remove" + getCapitalizedName() + "WithKey";
		} else {
			return "";
		}
	}

	/*private String _getterHeader;

    private String _setterHeader;

    private String _additionAccessorHeader;

    private String _removingAccessorHeader;

    private String _getterDefaultCode;

    private String _setterDefaultCode;

    private String _additionAccessorDefaultCode;

    private String _removingAccessorDefaultCode;*/

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (isDeleted)
			return;
		if (dataModification instanceof DMEntityClassNameChanged && observable == getType().getBaseEntity()) {
			// Handle class name changed
			updateTypeClassNameChange((String)((DMEntityClassNameChanged)dataModification).oldValue(),
					(String)((DMEntityClassNameChanged)dataModification).newValue());
		} else if (dataModification instanceof EntityDeleted && observable == getType().getBaseEntity()) {
			setType(null);
		} else {
			super.update(observable, dataModification);
		}
	}

	private void updateTypeClassNameChange(String oldClassName,String newClassName)
	{
		// TODO reimplement this later
		/*	if(_getterHeader!=null){
			_getterHeader = ToolBox.replaceStringByStringInString(oldClassName,newClassName,_getterHeader);
		}
		if(_setterHeader!=null){
			_setterHeader = ToolBox.replaceStringByStringInString(oldClassName,newClassName,_setterHeader);
		}
		if(_additionAccessorCode!=null){
			_additionAccessorCode = ToolBox.replaceStringByStringInString(oldClassName,newClassName,_additionAccessorCode);
		}
		if(_removingAccessorHeader!=null){
			_removingAccessorHeader = ToolBox.replaceStringByStringInString(oldClassName,newClassName,_removingAccessorHeader);
		}
		if(_additionAccessorDefaultCode!=null){
			_additionAccessorDefaultCode = ToolBox.replaceStringByStringInString(oldClassName,newClassName,_additionAccessorDefaultCode);
		}
		if(_removingAccessorDefaultCode!=null){
			_removingAccessorDefaultCode = ToolBox.replaceStringByStringInString(oldClassName,newClassName,_removingAccessorDefaultCode);
		}
		setChanged(true);*/
	}

	protected String getFieldJavadoc()
	{
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/** "+FlexoLocalization.localizedForKey("field_javadoc")+" '"+getName()+"'"+"  */");
		return javadoc.toString();
	}

	protected String getFieldDeclaration()
	{
		StringBuffer fieldDeclaration = new StringBuffer();
		fieldDeclaration.append(getFieldModifier());
		fieldDeclaration.append(getAccessorTypeAsString());
		fieldDeclaration.append(" ");
		fieldDeclaration.append(getFieldName());
		return fieldDeclaration.toString();
	}

	protected String getFieldDefaultInitializationExpression()
	{
		if (getEntity() instanceof LoadableDMEntity) return ";";
		if (getType()==null || getType().isVoid()) {
			return ";";
		} else if (getType().isBoolean())
			return " = false;";
		else if (getType().isInteger())
			return " = 0;";
		else if (getType().isLong())
			return " = 0;";
		else if (getType().isChar())
			return " = ' ';";
		else if (getType().isFloat())
			return " = 0.0f;";
		else if (getType().isDouble())
			return " = 0.0;";
		else
			return ";";
	}


	protected String getGetterHeader()
	{
		StringBuffer methodHeader = new StringBuffer();
		methodHeader.append(getGetterModifier());
		methodHeader.append(getAccessorTypeAsString());
		methodHeader.append(" ");
		methodHeader.append(getGetterName());
		methodHeader.append("()");
		return methodHeader.toString();
	}

	protected String[] getGetterSignatureCandidates()
	{
		String[] candidates = { getGetterName()+"()" , getName()+"()", "_"+getGetterName()+"()" , "_"+getName()+"()"};
		return candidates;
	}

	protected String getGetterDefaultCoreCode()
	{
		if (getEntity() instanceof LoadableDMEntity) return COMPILED_CODE;
		if (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY) {
			if (getEntity() instanceof AutoGeneratedProcessBusinessDataDMEntity) return AutoGeneratedProcessBusinessDataDMEntity.getGetterDefaultCoreCodeForProperty(this);
			
			if(getType()!=null && getType().isBooleanPrimitive())
				return " { "+StringUtils.LINE_SEPARATOR+"    // TODO: Edit your code here"+StringUtils.LINE_SEPARATOR
					+"return false;"+StringUtils.LINE_SEPARATOR+"}";
			else
				return " { "+StringUtils.LINE_SEPARATOR+"    // TODO: Edit your code here"+StringUtils.LINE_SEPARATOR
			+"return null;"+StringUtils.LINE_SEPARATOR+"}";
		} else if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)) {
			return  " { "+StringUtils.LINE_SEPARATOR+"  return "+getFieldName()+";"+StringUtils.LINE_SEPARATOR+"}";
		}
		return "???";
	}

	protected String getGetterJavadoc()
	{
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/**"+StringUtils.LINE_SEPARATOR);
		if (getEntity() instanceof LoadableDMEntity
				&& (getDescription() == null || getDescription().equals(""))) {
			javadoc.append("  * "+COMPILED_CODE_IN_JAVADOC+StringUtils.LINE_SEPARATOR);
		}
		else if (getDescription() != null && getDescription().trim().length() > 0) {
			javadoc.append("  * "+ToolBox.getJavaDocString(getDescription(),"  "));
			/*
			BufferedReader rdr = new BufferedReader(new StringReader(getDescription()));
			boolean hasMoreLines = true;
			while (hasMoreLines) {
				String currentLine = null;
				try {
					currentLine = rdr.readLine();
				}
				catch (IOException e) {}
				if (currentLine != null) {
					currentLine = ToolBox.getJavaDocString(currentLine);
					javadoc.append("  * "+currentLine+StringUtils.LINE_SEPARATOR);
				}
				hasMoreLines = (currentLine != null);
			}*/
		}
		javadoc.append("  *"+StringUtils.LINE_SEPARATOR);

		Hashtable<String,String> specificDescriptions = getSpecificDescriptions();
		if (specificDescriptions != null && specificDescriptions.size() > 0) {
			for (String key : specificDescriptions.keySet()) {
				String specificDescription = ToolBox.getJavaDocString(specificDescriptions.get(key));
				javadoc.append(getTagAndParamRepresentation("doc",key,specificDescription));
			}
			javadoc.append("  *"+StringUtils.LINE_SEPARATOR);
		}

		javadoc.append("  * @return "+getAccessorTypeAsString()
				+" "+FlexoLocalization.localizedForKey("property_value")+StringUtils.LINE_SEPARATOR);

		javadoc.append("  */");

		//logger.info("Returning "+javadoc.toString());

		return javadoc.toString();
	}

	public static String getTagAndParamRepresentation(String tag, String param, String tagValue)
	{
		StringBuffer returned = new StringBuffer();

		int indentLength = (" @"+tag+" "+param).length();
		String indent = null;
		StringTokenizer st2 = new StringTokenizer(tagValue,StringUtils.LINE_SEPARATOR);
		boolean isFirst = true;
		while (st2.hasMoreTokens()) {
			if (isFirst) {
				returned.append("  * @"+tag+" "+param+" "+st2.nextToken()+StringUtils.LINE_SEPARATOR);
			}
			else {
				if (indent == null) indent = StringUtils.buildWhiteSpaceIndentation(indentLength);
				returned.append("  *"+indent+" "+st2.nextToken()+StringUtils.LINE_SEPARATOR);
			}
			isFirst = false;
		}
		return returned.toString();
	}

	private void updateFieldCode()
	{
		String oldCode = getFieldSourceCode().getCode();

		//logger.info("update field code with "+getFieldDeclaration());

		if (getFieldSourceCode().isEdited()) {
			getFieldSourceCode().replaceFieldDeclarationInEditedCode(getFieldDeclaration());
			ParsedJavadoc jd;
			try {
				jd = getFieldSourceCode().parseJavadoc(oldCode);
				if (jd == null)  {
					getFieldSourceCode().replaceJavadocInEditedCode(getFieldJavadoc()+StringUtils.LINE_SEPARATOR);
				}
			}
			catch (ParserNotInstalledException e) {
				logger.warning("JavaParser not installed");
			}
		}
		else {
			getFieldSourceCode().updateComputedCode();
		}

		setChanged();
		notifyObservers(new DMAttributeDataModification("fieldSourceCode",oldCode,getFieldCode()));
	}

	private void updateGetterCode()
	{
		String oldCode = getGetterSourceCode().getCode();

		if (getGetterSourceCode().isEdited()) {
			getGetterSourceCode().replaceMethodDeclarationInEditedCode(getGetterHeader());
			ParsedJavadoc jd;
			try {
				jd = getGetterSourceCode().parseJavadoc(oldCode);

				if (jd != null)  {

					jd.setComment(/*ToolBox.getJavaDocString(getDescription())*/getDescription());

					Hashtable<String,String> specificDescriptions = getSpecificDescriptions();
					if (specificDescriptions != null && specificDescriptions.size() > 0) {
						for (String key : specificDescriptions.keySet()) {
							String specificDescription = ToolBox.getJavaDocString(specificDescriptions.get(key));
							ParsedJavadocItem jdi = jd.getTagByName("doc", key);
							if (jdi != null) jdi.setParameterValue(specificDescription);
							else jd.addTagForNameAndValue("doc", key,specificDescription,true);
						}
					}

					getGetterSourceCode().replaceJavadocInEditedCode(jd);
				}

				else {
					getGetterSourceCode().replaceJavadocInEditedCode(getGetterJavadoc()+StringUtils.LINE_SEPARATOR);
				}
			}
			catch (ParserNotInstalledException e) {
				logger.warning("JavaParser not installed");
			}
		}
		else {
			getGetterSourceCode().updateComputedCode();
		}

		setChanged();
		notifyObservers(new DMAttributeDataModification("getterSourceCode",oldCode,getGetterCode()));
	}

	private String setterParamName = null;

	public String getSetterParamName()
	{
		if (setterParamName == null && getName() != null && !isSerializing()) return getNameAsMethodArgument();
		return setterParamName;
	}

	public void setSetterParamName(String paramName)
	{
		//logger.info("setSetterParamName with "+paramName);
		String oldSetterParamName = setterParamName;
		if (getNameAsMethodArgument().equals(paramName) && setterParamName != null) setterParamName = null;
		else setterParamName = paramName;

		String newSetterParamName = setterParamName;
		if ((oldSetterParamName != null && newSetterParamName == null)
				|| (oldSetterParamName == null && newSetterParamName != null)
				|| (oldSetterParamName != null && !oldSetterParamName.equals(newSetterParamName))) {
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("setterParamName", oldSetterParamName, newSetterParamName));
		}
	}

	protected String getSetterHeader()
	{
		StringBuffer methodHeader = new StringBuffer();
		methodHeader.append(getSetterModifier());
		methodHeader.append("void ");
		methodHeader.append(getSetterName());
		methodHeader.append("(");
		methodHeader.append(getAccessorTypeAsString());
		methodHeader.append(" ");
		methodHeader.append(getSetterParamName());
		methodHeader.append(")");
		return methodHeader.toString();
	}

	protected String[] getSetterSignatureCandidates()
	{
		String[] candidates = { getSetterName()+"("+getAccessorTypeAsString()+")"};
		return candidates;
	}

	protected String getSetterDefaultCoreCode()
	{
		if (getEntity() instanceof LoadableDMEntity) return COMPILED_CODE;
		if (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY) {
			
			if (getEntity() instanceof AutoGeneratedProcessBusinessDataDMEntity) return AutoGeneratedProcessBusinessDataDMEntity.getSetterDefaultCoreCodeForProperty(this);
			
			return " { "+StringUtils.LINE_SEPARATOR+"    // TODO: Edit your code here"+StringUtils.LINE_SEPARATOR+"}";
		} else if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)) {
			return  " { "+StringUtils.LINE_SEPARATOR+"  "+getFieldName()+"="+getNameAsMethodArgument()+";"+StringUtils.LINE_SEPARATOR+"}";
		}
		return "???";
	}

	protected String getSetterJavadoc()
	{
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/**"+StringUtils.LINE_SEPARATOR);
		javadoc.append("  * "
				+FlexoLocalization.localizedForKey("setter_method_javadoc")
				+" '"+getName()+"'"+StringUtils.LINE_SEPARATOR);

		javadoc.append("  *"+StringUtils.LINE_SEPARATOR);

		javadoc.append("  * @param "+getSetterParamName()
				+" "+FlexoLocalization.localizedForKey("value_to_set")+StringUtils.LINE_SEPARATOR);

		javadoc.append("  */");
		return javadoc.toString();
	}

	private void updateSetterCode()
	{
		String oldCode = getSetterSourceCode().getCode();

		if (getSetterSourceCode().isEdited()) {
			getSetterSourceCode().replaceMethodDeclarationInEditedCode(getSetterHeader());
			ParsedJavadoc jd;
			try {
				jd = getSetterSourceCode().parseJavadoc(oldCode);
				if (jd == null) {
					getSetterSourceCode().replaceJavadocInEditedCode(getSetterJavadoc()+StringUtils.LINE_SEPARATOR);
				}
			}
			catch (ParserNotInstalledException e) {
				logger.warning("JavaParser not installed");
			}
		}
		else {
			getSetterSourceCode().updateComputedCode();
		}

		setChanged();
		notifyObservers(new DMAttributeDataModification("setterSourceCode",oldCode,getSetterCode()));
	}

	private String additionAccessorParamName = null;

	public String getAdditionAccessorParamName()
	{
		if (additionAccessorParamName == null && getName() != null && !isSerializing()) return getNameAsMethodArgument();
		return additionAccessorParamName;
	}

	public void setAdditionAccessorParamName(String paramName)
	{
		String oldAdditionAccessorParamName = additionAccessorParamName;
		if (getNameAsMethodArgument().equals(paramName) && additionAccessorParamName != null) additionAccessorParamName = null;
		else additionAccessorParamName = paramName;

		String newAdditionAccessorParamName = additionAccessorParamName;
		if ((oldAdditionAccessorParamName != null && newAdditionAccessorParamName == null)
				|| (oldAdditionAccessorParamName == null && newAdditionAccessorParamName != null)
				|| (oldAdditionAccessorParamName != null && !oldAdditionAccessorParamName.equals(newAdditionAccessorParamName))) {
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("additionAccessorParamName", oldAdditionAccessorParamName, newAdditionAccessorParamName));
		}
	}

	protected String getAdditionAccessorHeader()
	{
		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			StringBuffer methodHeader = new StringBuffer();
			methodHeader.append(getAdditionAccessorModifier());
			methodHeader.append("void ");
			methodHeader.append(getAdditionAccessorName());
			methodHeader.append("(");
			methodHeader.append(getType() != null ? getType().getName() : "Object");
			methodHeader.append(" ");
			methodHeader.append(getAdditionAccessorParamName());
			if (getCardinality() == DMCardinality.HASHTABLE) {
				methodHeader.append(", ");
				methodHeader.append(getKeyType() != null ? getKeyType().getName() : "Object");
				methodHeader.append(" ");
				methodHeader.append(getNameAsMethodArgument("key"));
			}
			methodHeader.append(")");
			return methodHeader.toString();
		}
		return "???";
	}

	protected String[] getAdditionAccessorSignatureCandidates()
	{
		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			StringBuffer candidate1 = new StringBuffer();
			candidate1.append(getAdditionAccessorName());
			candidate1.append("(");
			candidate1.append(getType() != null ? getType().getName() : "Object");
			if (getCardinality() == DMCardinality.HASHTABLE) {
				candidate1.append(", ");
				candidate1.append(getKeyType() != null ? getKeyType().getName() : "Object");
			}
			candidate1.append(")");
			String[] candidates = { candidate1.toString() };
			return candidates;
		}
		return null;
	}


	protected String getAdditionAccessorDefaultCoreCode()
	{
		if (getEntity() instanceof LoadableDMEntity) return COMPILED_CODE;
		if (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY) {
			return " { "+StringUtils.LINE_SEPARATOR+"    // TODO: Edit your code here"+StringUtils.LINE_SEPARATOR+"}";
		} else if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)) {
			if (getCardinality() == DMCardinality.VECTOR) {
				return " { "+StringUtils.LINE_SEPARATOR+"  "+getFieldName()+".add("+getNameAsMethodArgument()+");"+StringUtils.LINE_SEPARATOR+"}";
			} else if (getCardinality() == DMCardinality.HASHTABLE) {
				return " { "+StringUtils.LINE_SEPARATOR+"  "+getFieldName()+".put("+getNameAsMethodArgument("key")+","+getNameAsMethodArgument()+");"+StringUtils.LINE_SEPARATOR+"}";
			}
		}
		return "???";
	}

	protected String getAdditionAccessorJavadoc()
	{
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/**"+StringUtils.LINE_SEPARATOR);
		javadoc.append("  * "
				+FlexoLocalization.localizedForKey("addition_method_javadoc")
				+" '"+getName()+"'"+StringUtils.LINE_SEPARATOR);

		javadoc.append("  *"+StringUtils.LINE_SEPARATOR);

		javadoc.append("  * @param "+getAdditionAccessorParamName()
				+" "+FlexoLocalization.localizedForKey("value_to_add")+StringUtils.LINE_SEPARATOR);
		if (getCardinality() == DMCardinality.HASHTABLE) {
			javadoc.append("  * @param "+getNameAsMethodArgument("key")
					+" "+FlexoLocalization.localizedForKey("key_to_use")+StringUtils.LINE_SEPARATOR);
		}
		javadoc.append("  */");
		return javadoc.toString();
	}

	private void updateAdditionCode()
	{
		String oldCode = getAdditionSourceCode().getCode();

		if (getAdditionSourceCode().isEdited()) {
			getAdditionSourceCode().replaceMethodDeclarationInEditedCode(getAdditionAccessorHeader());
			ParsedJavadoc jd;
			try {
				jd = getAdditionSourceCode().parseJavadoc(oldCode);
				if (jd == null) {
					getAdditionSourceCode().replaceJavadocInEditedCode(getAdditionAccessorJavadoc()+StringUtils.LINE_SEPARATOR);
				}
			}
			catch (ParserNotInstalledException e) {
				logger.warning("JavaParser not installed");
			}
		}
		else {
			getAdditionSourceCode().updateComputedCode();
		}

		setChanged();
		notifyObservers(new DMAttributeDataModification("additionSourceCode",oldCode,getAdditionCode()));
	}


	private String removalAccessorParamName = null;

	public String getRemovalAccessorParamName()
	{
		if (removalAccessorParamName == null && getName() != null && !isSerializing()) return getNameAsMethodArgument();
		return removalAccessorParamName;
	}

	public void setRemovalAccessorParamName(String paramName)
	{
		String oldRemovalAccessorParamName = removalAccessorParamName;
		if (getNameAsMethodArgument().equals(paramName) && removalAccessorParamName != null) removalAccessorParamName = null;
		else removalAccessorParamName = paramName;

		String newRemovalAccessorParamName = removalAccessorParamName;
		if ((oldRemovalAccessorParamName != null && newRemovalAccessorParamName == null)
				|| (oldRemovalAccessorParamName == null && newRemovalAccessorParamName != null)
				|| (oldRemovalAccessorParamName != null && !oldRemovalAccessorParamName.equals(newRemovalAccessorParamName))) {
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("removalAccessorParamName", oldRemovalAccessorParamName, newRemovalAccessorParamName));
		}
	}


	protected String getRemovalAccessorHeader()
	{
		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			StringBuffer methodHeader = new StringBuffer();
			methodHeader.append(getRemovingAccessorModifier());
			methodHeader.append("void ");
			methodHeader.append(getRemovingAccessorName());
			methodHeader.append("(");
			if (getCardinality() == DMCardinality.VECTOR) {
				methodHeader.append(getType() != null ? getType().getName() : "Object");
				methodHeader.append(" ");
				methodHeader.append(getRemovalAccessorParamName());
			}
			if (getCardinality() == DMCardinality.HASHTABLE) {
				methodHeader.append(getKeyType() != null ? getKeyType().getName() : "Object");
				methodHeader.append(" ");
				methodHeader.append(getNameAsMethodArgument("key"));
			}
			methodHeader.append(")");
			return methodHeader.toString();
		}
		return "???";
	}

	protected String[] getRemovalAccessorSignatureCandidates()
	{
		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			StringBuffer candidate1 = new StringBuffer();
			candidate1.append(getRemovingAccessorName());
			candidate1.append("(");
			if (getCardinality() == DMCardinality.VECTOR) {
				candidate1.append(getType() != null ? getType().getName() : "Object");
			}
			if (getCardinality() == DMCardinality.HASHTABLE) {
				candidate1.append(getKeyType() != null ? getKeyType().getName() : "Object");
			}
			candidate1.append(")");
			String[] candidates = { candidate1.toString() };
			return candidates;
		}
		return null;
	}


	protected String getRemovalAccessorDefaultCoreCode()
	{
		if (getEntity() instanceof LoadableDMEntity) return COMPILED_CODE;
		if (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY) {
			return " { "+StringUtils.LINE_SEPARATOR+"    // TODO: Edit your code here"+StringUtils.LINE_SEPARATOR+"}";
		} else if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)) {
			if (getCardinality() == DMCardinality.VECTOR) {
				return " { "+StringUtils.LINE_SEPARATOR+"  "+getFieldName()+".remove("+getNameAsMethodArgument()+");"+StringUtils.LINE_SEPARATOR+"}";
			} else if (getCardinality() == DMCardinality.HASHTABLE) {
				return " { "+StringUtils.LINE_SEPARATOR+"  "+getFieldName()+".remove("+getNameAsMethodArgument("key")+");"+StringUtils.LINE_SEPARATOR+"}";
			}
		}
		return "???";
	}

	protected String getRemovalAccessorJavadoc()
	{
		StringBuffer javadoc = new StringBuffer();
		javadoc.append("/**"+StringUtils.LINE_SEPARATOR);
		javadoc.append("  * "
				+FlexoLocalization.localizedForKey("removal_method_javadoc")
				+" '"+getName()+"'"+StringUtils.LINE_SEPARATOR);

		javadoc.append("  *"+StringUtils.LINE_SEPARATOR);

		if (getCardinality() == DMCardinality.VECTOR) {
			javadoc.append("  * @param "+getRemovalAccessorParamName()
					+" "+FlexoLocalization.localizedForKey("value_to_remove")+StringUtils.LINE_SEPARATOR);
		} else if (getCardinality() == DMCardinality.HASHTABLE) {
			javadoc.append("  * @param "+getNameAsMethodArgument("key")
					+" "+FlexoLocalization.localizedForKey("key_for_value_to_remove")+StringUtils.LINE_SEPARATOR);
		}
		javadoc.append("  */");
		return javadoc.toString();
	}

	private void updateRemovalCode()
	{
		String oldCode = getRemovalSourceCode().getCode();

		if (getRemovalSourceCode().isEdited()) {
			getRemovalSourceCode().replaceMethodDeclarationInEditedCode(getRemovalAccessorHeader());
			ParsedJavadoc jd;
			try {
				jd = getRemovalSourceCode().parseJavadoc(oldCode);
				if (jd == null) {
					getRemovalSourceCode().replaceJavadocInEditedCode(getRemovalAccessorJavadoc()+StringUtils.LINE_SEPARATOR);
				}
			}
			catch (ParserNotInstalledException e) {
				logger.warning("JavaParser not installed");
			}
		}
		else {
			getRemovalSourceCode().updateComputedCode();
		}

		setChanged();
		notifyObservers(new DMAttributeDataModification("removalSourceCode",oldCode,getRemovalCode()));
	}



	private String getFieldModifier()
	{
		if (getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
			return "public "+(getIsStatic()?"static ":"");
		else if (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD
				|| getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
			return "protected "+(getIsStatic()?"static ":"");
		else if (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
			return "private "+(getIsStatic()?"static ":"");
		else if (getImplementationType() == DMPropertyImplementationType.PUBLIC_STATIC_FINAL_FIELD)
			return "public static final ";
		return "??? ";
	}

	private String getGetterModifier()
	{
		return "public "+(getIsStatic()?"static ":"");
	}

	protected String getSetterModifier()
	{
		return "public "+(getIsStatic()?"static ":"");
	}

	protected String getAdditionAccessorModifier()
	{
		return "public "+(getIsStatic()?"static ":"");
	}

	private String getRemovingAccessorModifier()
	{
		return "public "+(getIsStatic()?"static ":"");
	}

	public String getAccessorTypeAsString()
	{
		DMType resultingType = getResultingType();
		return (resultingType != null ? resultingType.getSimplifiedStringRepresentation() : "Object" /* WARNING: undefined type ! */);
		/*if (getCardinality() == DMCardinality.SINGLE) {
            return (getType() != null ? getType().getStringRepresentation() : "");
        } else if (getCardinality() == DMCardinality.VECTOR) {
            return getType().getName().equals("NSArray")?"NSArray":"Vector";
        } else if (getCardinality() == DMCardinality.HASHTABLE) {
            return "Hashtable";
        }
        return "";*/
	}

	/*public String getAccessorsFooter()
    {
        return "}";
    }*/

	public String getQualifiedPropertyCode()
	{
		StringBuffer answer = new StringBuffer();
		if (!getGetterCode().equals(FlexoLocalization.localizedForKey("not_relevant"))) {
			answer.append(getGetterCode());
		}
		// TODO complete this
		/* if (!getSetterCode().equals(FlexoLocalization.localizedForKey("not_relevant"))) {
            answer.append("\n");
            answer.append(getSetterCode());
        }
        if (!getQualifiedAdditionAccessorCode().equals(FlexoLocalization.localizedForKey("not_relevant"))) {
            answer.append("\n");
            answer.append(getQualifiedAdditionAccessorCode());
        }
        if (!getQualifiedRemovingAccessorCode().equals(FlexoLocalization.localizedForKey("not_relevant"))) {
            answer.append("\n");
            answer.append(getQualifiedRemovingAccessorCode());
        }*/
		return answer.toString();
	}

	/* public String getQualifiedGetterCode()
    {
        if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)) {
            if (_getterCode != null) {
                return getGetterHeader() + "\n" + _getterCode + "\n" + getAccessorsFooter();
            } else {
                return getGetterHeader() + "\n" + getGetterDefaultCode() + "\n" + getAccessorsFooter();
            }
        } else if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD)) {
            return FlexoLocalization.localizedForKey("not_relevant");
        }
        return "";
    }

    public void setQualifiedGetterCode(String qualifiedCode)
    {
        if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD))
            return;
        setGetterCode(extractCore(qualifiedCode));
    }

    public String getQualifiedSetterCode()
    {
        if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)) {
            if (_setterCode != null) {
                return getSetterHeader() + "\n" + _setterCode + "\n" + getAccessorsFooter();
            } else {
                return getSetterHeader() + "\n" + getSetterDefaultCode() + "\n" + getAccessorsFooter();
            }
        } else if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD)) {
            return FlexoLocalization.localizedForKey("not_relevant");
        }
        return "";
    }

    public void setQualifiedSetterCode(String qualifiedCode)
    {
        if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD))
            return;
        setSetterCode(extractCore(qualifiedCode));
    }

    public String getQualifiedAdditionAccessorCode()
    {
        if (getCardinality().isMultiple()) {
            if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
                    || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
                    || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)) {
                if (_additionAccessorCode != null) {
                    return getAdditionAccessorHeader() + "\n" + _additionAccessorCode + "\n" + getAccessorsFooter();
                } else {
                    return getAdditionAccessorHeader() + "\n" + getAdditionAccessorDefaultCode() + "\n" + getAccessorsFooter();
                }
            }
        }
        return FlexoLocalization.localizedForKey("not_relevant");
    }

    public void setQualifiedAdditionAccessorCode(String qualifiedCode)
    {
        if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)) {
            setAdditionAccessorCode(extractCore(qualifiedCode));
        }
    }

    public String getQualifiedRemovingAccessorCode()
    {
        if (getCardinality().isMultiple()) {
            if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
                    || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
                    || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)) {
                if (_removingAccessorCode != null) {
                    return getRemovingAccessorHeader() + "\n" + _removingAccessorCode + "\n" + getAccessorsFooter();
                } else {
                    return getRemovingAccessorHeader() + "\n" + getRemovingAccessorDefaultCode() + "\n" + getAccessorsFooter();
                }
            }
        }
        return FlexoLocalization.localizedForKey("not_relevant");
    }

    public void setQualifiedRemovingAccessorCode(String qualifiedCode)
    {
        if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
                || (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)) {
            setRemovingAccessorCode(extractCore(qualifiedCode));
        }
    }*/

	public Vector<DMTypeVariable> getTypeVariables()
	{
		if (getEntity() != null) return getEntity().getTypeVariables();
		return null;
	}

	// ================================================================
	// ===================== TreeNode implementation ==================
	// ================================================================

	@Override
	public synchronized Vector<DMObject> getOrderedChildren()
	{
		return EMPTY_VECTOR;
	}

	@Override
	public TreeNode getParent()
	{
		return getEntity();
	}

	@Override
	public boolean getAllowsChildren()
	{
		return false;
	}

	/**
	 * Overrides getClassNameKey
	 *
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey()
	{
		return "dm_property";
	}

	public boolean isBindingValid()
	{
		return true;
	}

	@Override
	public void setIsModified()
	{
		if (ignoreNotifications())
			return;
		super.setIsModified();
		if (getEntity()!=null)
			getEntity().setIsModified();
	}

	public boolean isResolvable()
	{
		if (getCardinality() == DMCardinality.HASHTABLE && getKeyType() != null && !getKeyType().isResolved()) return false;
		if (getType() != null) return getType().isResolved();
		return false;
	}

	public Vector<DMType> getUnresolvedTypes()
	{
		Vector<DMType> unresolvedTypes = new Vector<DMType>();
		if (getCardinality() == DMCardinality.HASHTABLE && getKeyType() != null && !getKeyType().isResolved()) unresolvedTypes.add(getKeyType());;
		if (getType() != null && !getType().isResolved()) unresolvedTypes.add(getType());
		return unresolvedTypes;
	}


	public boolean hasAccessors()
	{
		return getImplementationType()!=null && (getImplementationType()==DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY || getImplementationType()==DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD || getImplementationType()==DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD);
	}

	public boolean isBindingValueOfTextField(){
		if(getEntity() instanceof ComponentDMEntity){
			IEWOComponent component = ((ComponentDMEntity)getEntity()).getComponentDefinition().getWOComponent();
			Enumeration<IETextFieldWidget> en = component.getTextfields().elements();
			IETextFieldWidget tf = null;
			while (en.hasMoreElements()) {
				tf = en.nextElement();
				if(tf.getBindingValue()!=null
						&& (tf.getBindingValue()).isProperty(this))
					return true;
			}
		}
		return false;
	}
	public boolean isBindingValueOfTextArea(){
		if(getEntity() instanceof ComponentDMEntity){
			IEWOComponent component = ((ComponentDMEntity)getEntity()).getComponentDefinition().getWOComponent();
			Enumeration<IETextAreaWidget> en = component.getTextareas().elements();
			IETextAreaWidget tf = null;
			while (en.hasMoreElements()) {
				tf = en.nextElement();
				if(tf.getBindingValue()!=null
						&& (tf.getBindingValue()).isProperty(this))
					return true;
			}
		}
		return false;
	}
	public boolean isBindingCheckedOfCheckBox(){
		if(getEntity() instanceof ComponentDMEntity){
			IEWOComponent component = ((ComponentDMEntity)getEntity()).getComponentDefinition().getWOComponent();
			Enumeration<IECheckBoxWidget> en = component.getCheckboxes().elements();
			IECheckBoxWidget tf = null;
			while (en.hasMoreElements()) {
				tf = en.nextElement();
				if(tf.getBindingChecked()!=null && tf.getBindingChecked().isProperty(this))return true;
			}
		}
		return false;
	}
	public boolean isBindingListOfDropDown()
	{
		if(getEntity() instanceof ComponentDMEntity){
			IEWOComponent component = ((ComponentDMEntity)getEntity()).getComponentDefinition().getWOComponent();
			Enumeration<IEDropDownWidget> en = component.getDropdowns().elements();
			IEDropDownWidget tf = null;
			while (en.hasMoreElements()) {
				tf = en.nextElement();
				if(tf.getBindingList()!=null
						&& tf.getBindingList() instanceof BindingValue
						&& ((BindingValue)tf.getBindingList()).isProperty(this))return true;
			}
		}
		return false;
	}
	public boolean isBindingItemOfRepetition(){
		if(getEntity() instanceof ComponentDMEntity){
			IEWOComponent component = ((ComponentDMEntity)getEntity()).getComponentDefinition().getWOComponent();
			Enumeration<RepetitionOperator> en = component.getAllRepetitionOperator().elements();
			RepetitionOperator tf = null;
			while (en.hasMoreElements()) {
				tf = en.nextElement();
				if(tf.getBindingItem()!=null && tf.getBindingItem().isProperty(this))return true;
			}
		}
		return false;
	}
	public boolean isBindingItemOfDropDown(){
		if(getEntity() instanceof ComponentDMEntity){
			IEWOComponent component = ((ComponentDMEntity)getEntity()).getComponentDefinition().getWOComponent();
			Enumeration<IEDropDownWidget> en = component.getDropdowns().elements();
			IEDropDownWidget tf = null;
			while (en.hasMoreElements()) {
				tf = en.nextElement();
				if(tf.getBindingItem()!=null && tf.getBindingItem().isProperty(this))return true;
			}
		}
		return false;
	}

	public boolean isEOAttribute()
	{
		return this instanceof DMEOAttribute;
	}

	public boolean getIsStaticallyDefinedInTemplate()
	{
		return _isStaticallyDefinedInTemplate;
	}

	public void setIsStaticallyDefinedInTemplate(boolean isStaticallyDefinedInTemplate)
	{
		if (_isStaticallyDefinedInTemplate != isStaticallyDefinedInTemplate) {
			_isStaticallyDefinedInTemplate = isStaticallyDefinedInTemplate;
			setChanged();
			notifyObservers(new DMAttributeDataModification("isStaticallyDefinedInTemplate",!isStaticallyDefinedInTemplate,isStaticallyDefinedInTemplate));
		}
	}

	// ========================================================
		// ===================== Code management ==================
			// ========================================================

	public void resetSourceCode(){
		try{
			if(fieldSourceCode!=null)fieldSourceCode.setCode("");
			if(getterSourceCode!=null)getterSourceCode.setCode("");
			if(setterSourceCode!=null)setterSourceCode.setCode("");
			if(additionSourceCode!=null)additionSourceCode.setCode("");
			if(removalSourceCode!=null)removalSourceCode.setCode("");
		}catch(DuplicateMethodSignatureException e){
			e.printStackTrace();
		} catch (ParserNotInstalledException e) {
			e.printStackTrace();
		}
	}

	private FieldSourceCode fieldSourceCode;
	private MethodSourceCode getterSourceCode;
	private MethodSourceCode setterSourceCode;
	private MethodSourceCode additionSourceCode;
	private MethodSourceCode removalSourceCode;

	public FieldSourceCode getFieldSourceCode()
	{
		if (fieldSourceCode == null) {
			fieldSourceCode = new FieldSourceCode(this,"fieldSourceCode","hasFieldParseError","fieldParseErrorWarning") {
				@Override
                public String makeComputedCode() {
					return getFieldJavadoc()+StringUtils.LINE_SEPARATOR+getFieldDeclaration()+getFieldDefaultInitializationExpression();
				}
				@Override
				public void interpretEditedJavaField(ParsedJavaField javaField) throws DuplicateMethodSignatureException
				{

					logger.info(">>>>>>>>>>>> Interpret FIELD with "+javaField);
					getJavaFieldParser().updatePropertyFromJavaField(DMProperty.this, javaField);

					if (!isResolvable()) {
						setHasParseErrors(true);
						setParseErrorWarning("<html><font color=\"red\">"
								+FlexoLocalization.localizedForKey("unresolved_type(s)")+" : "+getUnresolvedTypes()
								+"</font></html>");
					}
					DMProperty.this.setChanged();
					DMProperty.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("fieldCode", null, getCode()));
				}


			};
		}
		return fieldSourceCode;
	}

	public boolean hasFieldParseErrors()
	{
		return getFieldSourceCode().hasParseErrors();
	}

	public boolean isComponentProperty(){
		return getEntity() instanceof ComponentDMEntity;
	}

	public boolean getIsBindable(){
		if(isComponentProperty()) return ((ComponentDMEntity)getEntity()).isBindable(this);
		return false;
	}

	public void setIsBindable(boolean value){
		if(isComponentProperty()) ((ComponentDMEntity)getEntity()).setBindable(this, value);
	}

	public String getFieldParseErrorWarning()
	{
		return getFieldSourceCode().getParseErrorWarning();
	}

	public String getFieldCode()
	{
		if ((getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
				|| (getImplementationType() == DMPropertyImplementationType.PUBLIC_STATIC_FINAL_FIELD)) {
			return getFieldSourceCode().getCode();
		}
		return null;
	}

	public void setFieldCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException
	{
		getFieldSourceCode().setCode(someCode);
		setChanged();
		notifyObservers(new DMAttributeDataModification("fieldCode",null,someCode));
	}

	public MethodSourceCode getGetterSourceCode()
	{
		if (getterSourceCode == null) {
			getterSourceCode = new MethodSourceCode(this,"getterSourceCode","hasGetterParseError","getterParseErrorWarning") {
				@Override
                public String makeComputedCode() {
					return getGetterJavadoc()+StringUtils.LINE_SEPARATOR+getGetterHeader()+" "+getGetterDefaultCoreCode();
				}
				@Override
				public void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException
				{

					logger.info(">>>>>>>>>>>> Interpret GETTER with "+javaMethod);
					try {
						getJavaMethodParser().updateGetterWith(DMProperty.this, javaMethod);

						if (!isResolvable()) {
							setHasParseErrors(true);
							setParseErrorWarning("<html><font color=\"red\">"
									+FlexoLocalization.localizedForKey("unresolved_type(s)")+" : "+getUnresolvedTypes()
									+"</font></html>");
						}
						DMProperty.this.setChanged();
						DMProperty.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("getterCode", null, getCode()));
					} catch (DuplicateMethodSignatureException e) {
						setHasParseErrors(true);
						setParseErrorWarning("<html><font color=\"red\">"
								+FlexoLocalization.localizedForKey("duplicated_method_signature")
								+"</font></html>");
						throw e;
					}
				}


			};
		}
		return getterSourceCode;
	}

	public boolean hasGetterParseErrors()
	{
		return getGetterSourceCode().hasParseErrors();
	}

	public String getGetterParseErrorWarning()
	{
		return getGetterSourceCode().getParseErrorWarning();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getGetterCoreCode()
	{
		if (isSerializing()) return null;
		return getGetterSourceCode().getCoreCode();
	}

	/**
	 * @deprecated
	 * @param someCode
	 */
	@Deprecated
	public void setGetterCoreCode(String someCoreCode)
	{
		getGetterSourceCode().updateComputedCode(getGetterJavadoc()+StringUtils.LINE_SEPARATOR+getGetterHeader()+" { "+StringUtils.LINE_SEPARATOR+someCoreCode+StringUtils.LINE_SEPARATOR+"}");
	}

	public String getGetterCode()
	{
		return getGetterSourceCode().getCode();
	}

	public void setGetterCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException
	{
		getGetterSourceCode().setCode(someCode);
		setChanged();
		notifyObservers(new DMAttributeDataModification("getterCode",null,someCode));
	}

	public MethodSourceCode getSetterSourceCode()
	{
		if (setterSourceCode == null) {
			setterSourceCode = new MethodSourceCode(this,"setterSourceCode","hasSetterParseError","setterParseErrorWarning") {
				@Override
                public String makeComputedCode() {
					return getSetterJavadoc()+StringUtils.LINE_SEPARATOR+getSetterHeader()+" "+getSetterDefaultCoreCode();
				}
				@Override
				public void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException
				{

					logger.info(">>>>>>>>>>>> Interpret SETTER with "+javaMethod);
					try {
						getJavaMethodParser().updateSetterWith(DMProperty.this, javaMethod);

						if (!isResolvable()) {
							setHasParseErrors(true);
							setParseErrorWarning("<html><font color=\"red\">"
									+FlexoLocalization.localizedForKey("unresolved_type(s)")+" : "+getUnresolvedTypes()
									+ "</font></html>");
						}
						DMProperty.this.setChanged();
						DMProperty.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("setterCode", null, getCode()));
					} catch (DuplicateMethodSignatureException e) {
						setHasParseErrors(true);
						setParseErrorWarning("<html><font color=\"red\">"
								+FlexoLocalization.localizedForKey("duplicated_method_signature")
								+"</font></html>");
						throw e;
					}
				}


			};
		}
		return setterSourceCode;
	}

	public boolean hasSetterParseErrors()
	{
		return getSetterSourceCode().hasParseErrors();
	}

	public String getSetterParseErrorWarning()
	{
		return getSetterSourceCode().getParseErrorWarning();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getSetterCoreCode()
	{
		if (isSerializing()) return null;
		return getSetterSourceCode().getCoreCode();
	}

	/**
	 * @deprecated
	 * @param someCode
	 */
	@Deprecated
	public void setSetterCoreCode(String someCoreCode)
	{
		getSetterSourceCode().updateComputedCode(getSetterJavadoc()+StringUtils.LINE_SEPARATOR+getSetterHeader()+" { "+StringUtils.LINE_SEPARATOR+someCoreCode+StringUtils.LINE_SEPARATOR+"}");
	}

	public String getSetterCode()
	{
		return getSetterSourceCode().getCode();
	}

	public void setSetterCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException
	{
		getSetterSourceCode().setCode(someCode);
		setChanged();
		notifyObservers(new DMAttributeDataModification("setterCode",null,someCode));
	}


	public MethodSourceCode getAdditionSourceCode()
	{
		if (additionSourceCode == null) {
			additionSourceCode = new MethodSourceCode(this,"additionSourceCode","hasAdditionAccessorParseError","additionAccessorParseErrorWarning") {
				@Override
                public String makeComputedCode() {
					return getAdditionAccessorJavadoc()+StringUtils.LINE_SEPARATOR+getAdditionAccessorHeader()+" "+getAdditionAccessorDefaultCoreCode();
				}
				@Override
				public void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException
				{

					logger.info(">>>>>>>>>>>> Interpret ADDITION accessor with "+javaMethod);
					try {
						getJavaMethodParser().updateAdditionAccessorWith(DMProperty.this, javaMethod);

						if (!isResolvable()) {
							setHasParseErrors(true);
							setParseErrorWarning("<html><font color=\"red\">" + FlexoLocalization.localizedForKey("unresolved_type(s)")
									+ " : " + getUnresolvedTypes() + "</font></html>");
						}
						DMProperty.this.setChanged();
						DMProperty.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("additionCode", null, getCode()));
					} catch (DuplicateMethodSignatureException e) {
						setHasParseErrors(true);
						setParseErrorWarning("<html><font color=\"red\">"
								+FlexoLocalization.localizedForKey("duplicated_method_signature")
								+"</font></html>");
						throw e;
					}
				}


			};
		}
		return additionSourceCode;
	}

	public boolean hasAdditionAccessorParseErrors()
	{
		return getAdditionSourceCode().hasParseErrors();
	}

	public String getAdditionAccessorParseErrorWarning()
	{
		return getAdditionSourceCode().getParseErrorWarning();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getAdditionAccessorCoreCode()
	{
		if (isSerializing()) return null;
		return getAdditionSourceCode().getCoreCode();
	}

	/**
	 * @deprecated
	 * @param someCode
	 */
	@Deprecated
	public void setAdditionAccessorCoreCode(String someCoreCode)
	{
		getAdditionSourceCode().updateComputedCode(getAdditionAccessorJavadoc()+StringUtils.LINE_SEPARATOR+getAdditionAccessorHeader()+" { "+StringUtils.LINE_SEPARATOR+someCoreCode+StringUtils.LINE_SEPARATOR+"}");
	}

	public String getAdditionCode()
	{
		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			return getAdditionSourceCode().getCode();
		}
		return null;
	}

	public void setAdditionCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException
	{
		getAdditionSourceCode().setCode(someCode);
		setChanged();
		notifyObservers(new DMAttributeDataModification("additionCode",null,someCode));
	}

	public MethodSourceCode getRemovalSourceCode()
	{
		if (removalSourceCode == null) {
			removalSourceCode = new MethodSourceCode(this,"removalSourceCode","hasRemovalAccessorParseError","removalAccessorParseErrorWarning") {
				@Override
                public String makeComputedCode() {
					return getRemovalAccessorJavadoc()+StringUtils.LINE_SEPARATOR+getRemovalAccessorHeader()+" "+getRemovalAccessorDefaultCoreCode();
				}
				@Override
				public void interpretEditedJavaMethod(ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException
				{

					logger.info(">>>>>>>>>>>> Interpret REMOVAL accessor with "+javaMethod);
					try {
						getJavaMethodParser().updateRemovalAccessorWith(DMProperty.this, javaMethod);

						if (!isResolvable()) {
							setHasParseErrors(true);
							setParseErrorWarning("<html><font color=\"red\">"
									+ FlexoLocalization.localizedForKey("unresolved_type(s)")
									+ " : " + getUnresolvedTypes() + "</font></html>");
						}

						DMProperty.this.setChanged();
						DMProperty.this.notifyObserversAsReentrantModification(new DMAttributeDataModification("removalCode", null, getCode()));
					} catch (DuplicateMethodSignatureException e) {
						setHasParseErrors(true);
						setParseErrorWarning("<html><font color=\"red\">"
								+ FlexoLocalization.localizedForKey("duplicated_method_signature") + "</font></html>");
						throw e;
					}
				}


			};
		}
		return removalSourceCode;
	}

	public boolean hasRemovalAccessorParseErrors()
	{
		return getRemovalSourceCode().hasParseErrors();
	}

	public String getRemovalAccessorParseErrorWarning()
	{
		return getRemovalSourceCode().getParseErrorWarning();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getRemovalAccessorCoreCode()
	{
		if (isSerializing()) return null;
		return getRemovalSourceCode().getCoreCode();
	}

	/**
	 * @deprecated
	 * @param someCode
	 */
	@Deprecated
	public void setRemovalAccessorCoreCode(String someCoreCode)
	{
		getRemovalSourceCode().updateComputedCode(getRemovalAccessorJavadoc()+StringUtils.LINE_SEPARATOR+getRemovalAccessorHeader()+" { "+StringUtils.LINE_SEPARATOR+someCoreCode+StringUtils.LINE_SEPARATOR+"}");
	}

	public String getRemovalCode()
	{
		if ((getCardinality() == DMCardinality.VECTOR) || (getCardinality() == DMCardinality.HASHTABLE)) {
			return getRemovalSourceCode().getCode();
		}
		return null;
	}

	public void setRemovalCode(String someCode) throws ParserNotInstalledException, DuplicateMethodSignatureException
	{
		getRemovalSourceCode().setCode(someCode);
		setChanged();
		notifyObservers(new DMAttributeDataModification("removalCode",null,someCode));
	}

	// =============================================================
	// ===================== Code generation =======================
	// =============================================================

	/**
	 * Tells if code generation is applicable for related DMProperty
	 *
	 * @return
	 */
	public boolean isCodeGenerationApplicable()
	{
		if (getEntity() != null)
			return getEntity().isCodeGenerationApplicable();
		return false;
	}

	public String getCodeGenerationNotApplicableLabel()
	{
		if (getEntity() instanceof DMEOEntity) {
			if (!getDMModel().getEOCodeGenerationAvailable()) {
				return FlexoLocalization.localizedForKey("sorry_EO_code_generation_is_not_available_in_this_flexo_edition");
			}
		}
		return FlexoLocalization.localizedForKey("sorry_code_generation_not_applicable");
	}

	public boolean mustGenerateCode() {
		return true;
	}
	// =============================================================
	// ======================== Validation =========================
	// =============================================================

	public static class PropertyMustDefineType extends ValidationRule
	{

		/**
		 * @author gpolet
		 *
		 */
		public class SetType extends FixProposal
		{

			private DMType type;

			/**
			 * @param aMessage
			 */
			public SetType(DMType type)
			{
				super("set_type_to_($type)");
				this.type = type;
			}

			/**
			 * Overrides fixAction
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction()
			{
				((DMProperty)getObject()).setType(type);
			}

			public DMType getType()
			{
				return type;
			}

		}

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public PropertyMustDefineType()
		{
			super(DMProperty.class , "property_must_define_a_type");
		}

		/**
		 * Overrides applyValidation
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue applyValidation(Validable object)
		{
			DMProperty p = (DMProperty)object;
			if (p.getType()==null || (p.getType().getKindOfType()==KindOfType.UNRESOLVED && (p.getType().getStringRepresentation()==null || p.getType().getStringRepresentation().equals("null")))) {
				Vector<FixProposal> fixes = new Vector<FixProposal>();
				fixes.add(new SetType(DMType.makeResolvedDMType(p.getDMModel().getDMEntity(String.class))));
				fixes.add(new SetType(DMType.makeResolvedDMType(p.getDMModel().getDMEntity(Boolean.class))));
				fixes.add(new SetType(DMType.makeResolvedDMType(p.getDMModel().getDMEntity(Object.class))));
				return new ValidationError(this,object,"property_'($object.name)'_must_define_a_type_'($object.entity.name)'",fixes);
			}
			return null;
		}

	}

	public static class PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters extends ValidationRule<PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters, DMProperty> {

		public static class SetName extends FixProposal<PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters, DMProperty> {

			private String newName;
			private DMProperty property;

			public SetName(String newName, DMProperty property) {
				super("set_name_to_($newName)");
				this.newName = newName;
				this.property = property;
			}

			@Override
			protected void fixAction() {
				try {
                	if(ReservedKeyword.contains(newName))
                		throw new InvalidNameException(newName+" is a reserved keyword.");
					property.setName(newName);
				} catch (InvalidNameException e) {
					e.printStackTrace();
				} catch (DuplicatePropertyNameException e) {
					e.printStackTrace();
				}
			}

			public String getNewName() {
				return newName;
			}

		}

		public PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters() {
			super(DMProperty.class, "property_name_must_start_with_a_letter_and_be_followed_by_digits_or_letters");
		}

		@Override
		public ValidationIssue<PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters, DMProperty> applyValidation(DMProperty object) {
			if (object.getName()==null || object.getName().trim().length()==0){
				return new ValidationError<PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters, DMProperty>(this,object,"property_name_cannot_be_empty",new SetName(object.getDMModel().getNextDefautPropertyName(object.getEntity()),object));
			}
			if(!DMRegExp.ENTITY_NAME_PATTERN.matcher(object.getName()).matches()) {
				return new ValidationError<PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters, DMProperty>(this,object,"property_name_must_start_with_a_letter_and_be_followed_by_digits_or_letters",new SetName(object.getDMModel().getNextDefautPropertyName(object.getEntity()),object));
			}
			return null;
		}

	}

	public boolean codeIsComputable() {
		return true;
	}

}

