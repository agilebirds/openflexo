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
package org.openflexo.foundation.dm.eo;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMRegExp;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMPropertyNameChanged;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOProperty;
import org.openflexo.foundation.dm.eo.model.EORelationship;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents an accessor as a get/set key-value pair mapping a data stored in a database, as a field in a table.
 * 
 * @author sguerin
 * 
 */
public class DMEOAttribute extends DMEOProperty {

	private static final Logger logger = Logger.getLogger(DMEOAttribute.class.getPackage().getName());

	public static final String BOOLEAN_PROTOTYPE_NAME = "boolean";

	public static final String BOOLEAN_METHOD_POSTFIX = "Boolean";

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	protected EOAttribute _eoAttribute;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMEOAttribute(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMEOAttribute(DMModel dmModel) {
		super(dmModel);
	}

	/**
	 * Default constructor for dynamic creation
	 */
	public DMEOAttribute(DMModel dmModel, EOAttribute eoAttribute) {
		this(dmModel);
		_eoAttribute = eoAttribute;
		if (eoAttribute != null) {
			// setName(eoAttribute.name());
			name = eoAttribute.getName();
		}
	}

	/**
	 * Used for dynamic creation
	 */
	public static DMEOAttribute createsNewDMEOAttribute(DMModel dmModel, DMEOEntity dmEOEntity, String name, boolean isReadOnly,
			boolean isSettable, DMPropertyImplementationType implementationType) throws EOAccessException {
		EOAttribute eoAttribute = new EOAttribute();
		eoAttribute.setName(name);
		try {
			dmEOEntity.getEOEntity().addAttribute(eoAttribute);
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("EOControl management failed :" + e.getMessage());
			}
			throw new EOAccessException(e);
		}
		DMEOAttribute answer = new DMEOAttribute(dmModel, eoAttribute);
		dmEOEntity.registerProperty(answer);
		answer.setColumnName(ToolBox.convertJavaStringToDBName(name));
		answer.setIsReadOnlyAttribute(isReadOnly);
		answer.setIsSettable(isSettable);
		answer.setImplementationType(implementationType);
		answer.setIsUsedForLocking(false);
		answer.setAllowsNull(true);
		answer.setIsClassProperty(true);
		answer.setEntity(dmEOEntity);
		return answer;
	}

	@Override
	protected void updateCode() {
		if (getPrototype() != null) {
			super.updateCode();
		}
	}

	@Override
	public boolean delete() {
		setPrototype(null);
		if (getEOAttribute() != null) {
			try {
				if (getDMEOEntity() != null && getDMEOEntity().getEOEntity() != null) {
					getDMEOEntity().getEOEntity().removeAttribute(getEOAttribute());
				} else if (getEOAttribute().getEntity() != null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("No parent DMEOEntity or no EOEntity declared for DMEOEntity. Trying to proceed anyway.");
					}
					getEOAttribute().getEntity().removeAttribute(getEOAttribute());
				}
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOControl management failed :" + e.getMessage());
				}
			}
		}
		super.delete();
		_eoAttribute = null;
		return true;
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (getDMRepository() != null && getDMRepository().isReadOnly()) {
			return Inspectors.DM.DM_RO_EO_ATTRIBUTE_INSPECTOR;
		} else {
			return Inspectors.DM.DM_EO_ATTRIBUTE_INSPECTOR;
		}
	}

	@Override
	public boolean isDescriptionImportant() {
		return !getIsPrimaryKeyAttribute();
	}

	public EOAttribute getEOAttribute() {
		if (_eoAttribute == null) {
			if (getDMEOEntity() != null && getDMEOEntity().getEOEntity() != null) {
				try {
					_eoAttribute = getDMEOEntity().getEOEntity().attributeNamed(getName());
				} catch (IllegalArgumentException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find EOAttribute named " + getName() + " : EOControl management failed");
					}
				}
				if (_eoAttribute == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find EOAttribute named " + getName());
					}
				}
			}
		}
		return _eoAttribute;
	}

	public void setEOAttribute(EOAttribute attribute) {
		_eoAttribute = attribute;
	}

	/*
	 * public String getName() { if (_eoAttribute != null) { return
	 * _eoAttribute.name(); } else { return super.getName(); } }
	 */
	/**
	 * used by velocity (dynamic invocation: DON'T delete !)
	 * 
	 * @return java class name used in EOClass
	 */
	public String getJavaClassName() {
		try {
			if (getEOAttribute().getClassName() != null) {
				return getEOAttribute().getClassName().substring(getEOAttribute().getClassName().lastIndexOf(".") + 1);
			} else if (getEOAttribute().getPrototype() != null) {
				return getEOAttribute().getPrototype().getClassName()
						.substring(getEOAttribute().getPrototype().getClassName().lastIndexOf(".") + 1);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No JavaClassName nor prototype is set on eoAttribute named \"" + getName() + "\" in entity "
							+ getEntity().getName());
				}
				return "Object"; // Don't know what to do, let's use an
				// Object.
			}
		} catch (Exception e) {
			if (getEOAttribute() == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("getEOAttribute() return null !!!!");
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Error with EOAttribute : " + getName());
					}

				}
			}
			// e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setName(String newName) throws IllegalArgumentException, InvalidNameException {
		if (name == null || !name.equals(newName)) {
			if (!isDeserializing() && (newName == null || !DMRegExp.ENTITY_NAME_PATTERN.matcher(newName).matches())) {
				throw new InvalidNameException("'" + newName + "' is not a valid name for attribute.");
			}
			DMEntity containerEntity = getEntity();
			if (getEOAttribute() != null) {
				boolean isPK = _eoAttribute.getIsPrimaryKey();
				boolean isLock = _eoAttribute.getIsUsedForLocking();
				boolean isClassProperty = _eoAttribute.getIsClassProperty();
				EOEntity e = _eoAttribute.getEntity();
				e.removeAttribute(_eoAttribute);
				_eoAttribute.setName(newName);
				try {
					e.addAttribute(_eoAttribute);
				} catch (IllegalArgumentException ex) {
					_eoAttribute.setName(name);
					e.addAttribute(_eoAttribute);
					_eoAttribute.setIsPrimaryKey(isPK);
					_eoAttribute.setIsUsedForLocking(isLock);
					_eoAttribute.setIsClassProperty(isClassProperty);
					throw ex;
				}
				_eoAttribute.setIsPrimaryKey(isPK);
				_eoAttribute.setIsUsedForLocking(isLock);
				_eoAttribute.setIsClassProperty(isClassProperty);
			}
			if (containerEntity != null) {
				containerEntity.unregisterProperty(this, false);
			}
			String oldName = name;
			name = newName;
			if (!isDeserializing() && getEOAttribute() != null) {
				Iterator<EORelationship> i = _eoAttribute.getIncomingRelationships().iterator();
				while (i.hasNext()) {
					EORelationship r = i.next();
					DMEOEntity e = getDMModel().getDMEOEntity(r.getEntity());
					e.setChanged();
				}

				i = _eoAttribute.getOutgoingRelationships().iterator();
				while (i.hasNext()) {
					EORelationship r = i.next();
					if (r.getEntity() != null) {
						DMEOEntity e = getDMModel().getDMEOEntity(r.getEntity());
						e.setChanged();
					}
				}
			}
			if (containerEntity != null) {
				containerEntity.registerProperty(this, false);
			}

			updateCode();

			setChanged();
			notifyObservers(new DMPropertyNameChanged(this, oldName, newName));

			if (containerEntity != null) {
				containerEntity.notifyReordering(this);
			}
			if (getDMModel() != null && getDMModel().getEOPrototypeRepository() != null
					&& getDMModel().getEOPrototypeRepository().getEOPrototypeEntity() != null) {
				if (!isDeserializing()
						&& newName != null
						&& newName.trim().length() > 0
						&& (getColumnName() == null || getColumnName().equals(oldName) || oldName != null
								&& getColumnName().equals(ToolBox.getDBTableNameFromPropertyName(oldName)))) {
					setColumnName(ToolBox.getDBTableNameFromPropertyName(newName));
				}
				if (!isDeserializing() && newName != null && newName.trim().length() > 0 && getPrototype() == null
						&& newName.toUpperCase().indexOf("DATE") > -1) {
					setPrototype(getDMModel().getEOPrototypeRepository().getPrototypeNamed("date"));
				}
				if (!isDeserializing() && newName != null && newName.trim().length() > 0 && getPrototype() == null
						&& newName.toUpperCase().endsWith("ID")) {
					setPrototype(getDMModel().getEOPrototypeRepository().getPrototypeNamed("id"));
				}
				if (!isDeserializing() && newName != null && newName.trim().length() > 0 && getPrototype() == null
						&& (newName.toUpperCase().startsWith("IS") || newName.toUpperCase().startsWith("HAS"))) {
					setPrototype(getDMModel().getEOPrototypeRepository().getPrototypeNamed("boolean"));
				}
			}
		}
	}

	public boolean getIsReadOnlyAttribute() {
		if (getEOAttribute() != null) {
			return getEOAttribute().getIsReadOnly();
		}
		return super.getIsReadOnly();
	}

	public void setIsReadOnlyAttribute(boolean aBoolean) {
		if (getEOAttribute() != null) {
			getEOAttribute().setIsReadOnly(aBoolean);
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("isReadOnly", new Boolean(!aBoolean), new Boolean(aBoolean)));
		}
	}

	public String getColumnName() {
		if (getEOAttribute() != null) {
			return getEOAttribute().getColumnName();
		}
		return null;
	}

	public void setColumnName(String cName) {
		if (getEOAttribute() != null) {
			String oldColumnName = getColumnName();
			getEOAttribute().setColumnName(cName);
			setChanged();
			notifyObservers(new DMAttributeDataModification("columnName", oldColumnName, cName));
		}
	}

	public boolean getIsPrimaryKeyAttribute() {
		if (getDMEOEntity() != null) {
			return getDMEOEntity().getPrimaryKeyAttributes().contains(this);
		}
		return false;
	}

	public void setIsPrimaryKeyAttribute(boolean aBoolean) {
		if (getIsPrimaryKeyAttribute() != aBoolean) {
			if (getDMEOEntity() != null && getDMEOEntity().getEOEntity() != null && getEOAttribute() != null) {
				List<EOAttribute> arrayOfPrimaryKeyAttributes = getDMEOEntity().getEOEntity().getPrimaryKeyAttributes();
				if (aBoolean) {
					arrayOfPrimaryKeyAttributes.add(getEOAttribute());
					getEOAttribute().setAllowsNull(false);
				} else {
					arrayOfPrimaryKeyAttributes.remove(getEOAttribute());
					getEOAttribute().setAllowsNull(true);
				}
				getDMEOEntity().rebuildPrimaryKeyAttributes();
				updateCode();
				setChanged();
				notifyObservers(new DMAttributeDataModification("isPrimaryKeyAttribute", new Boolean(!aBoolean), new Boolean(aBoolean)));
			}

		}
	}

	public boolean getIsUsedForLocking() {
		if (getDMEOEntity() != null) {
			return getDMEOEntity().getAttributesUsedForLocking().contains(this);
		}
		return false;
	}

	public void setIsUsedForLocking(boolean aBoolean) {
		if (getIsUsedForLocking() != aBoolean) {
			if (getDMEOEntity() != null && getDMEOEntity().getEOEntity() != null && getEOAttribute() != null) {
				List<EOAttribute> arrayOfAttributesUsedForLocking = getDMEOEntity().getEOEntity().getAttributesUsedForLocking();
				if (aBoolean) {
					arrayOfAttributesUsedForLocking.add(getEOAttribute());
				} else {
					arrayOfAttributesUsedForLocking.remove(getEOAttribute());
				}
				getDMEOEntity().rebuildAttributesUsedForLocking();
				updateCode();
				setChanged();
				notifyObservers(new DMAttributeDataModification("isUsedForLocking", new Boolean(!aBoolean), new Boolean(aBoolean)));
			}

		}
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.dm.DMEOProperty#getEOProperty()
	 * @see org.openflexo.foundation.dm.eo.DMEOProperty#getEOProperty()
	 */
	@Override
	public EOProperty getEOProperty() {
		return getEOAttribute();
	}

	@Override
	public DMCardinality getCardinality() {
		return DMCardinality.SINGLE;
	}

	@Override
	public void setCardinality(DMCardinality cardinality) {
		// Non relevant
	}

	@Override
	public DMType getType() {
		if (getPrototype() != null) {
			// logger.info("Type for "+getPrototype().getName()+" is "+getPrototype().getType());
			return getPrototype().getType();
		}
		return null;
	}

	@Override
	public void setType(DMType type) {
		// Non relevant
	}

	public int getWidth() {
		if (getEOAttribute() != null) {
			return getEOAttribute().getWidth();
		}
		return 0;
	}

	public void setWidth(int width) {
		if (getEOAttribute() != null) {
			int oldWidth = getWidth();
			getEOAttribute().setWidth(width);
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("width", new Integer(oldWidth), new Integer(width)));
		}
	}

	public String getExternalType() {
		if (getEOAttribute() != null) {
			return getEOAttribute().getExternalType();
		}
		return null;
	}

	public void setExternalType(String externalType) {
		if (getEOAttribute() != null) {
			String oldExternalType = getExternalType();
			getEOAttribute().setExternalType(externalType);
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("externalType", oldExternalType, externalType));
		}
	}

	public String getValueType() {
		if (getEOAttribute() != null) {
			return getEOAttribute().getValueType();
		}
		return null;
	}

	public void setValueType(String valueType) {
		if (getEOAttribute() != null) {
			String oldValueType = getValueType();
			getEOAttribute().setValueType(valueType);
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("valueType", oldValueType, valueType));
		}
	}

	public boolean getAllowsNull() {
		if (getEOAttribute() != null) {
			return getEOAttribute().getAllowsNull();
		}
		return true;
	}

	public void setAllowsNull(boolean aBoolean) {
		if (getEOAttribute() != null) {
			getEOAttribute().setAllowsNull(aBoolean);
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("allowsNull", new Boolean(!aBoolean), new Boolean(aBoolean)));
		}
	}

	public DMEOPrototype getPrototype() {
		if (!isSerializing()) {
			ensureBooleanPropertyCreation();
		}
		if (getEOAttribute() != null && getEOAttribute().getPrototype() != null) {
			return getDMModel().getEOPrototypeRepository().getPrototype(getEOAttribute().getPrototype());
		}
		return null;
	}

	public void setPrototype(DMEOPrototype prototype) {
		if (getEOAttribute() != null) {
			String oldColumnName = getColumnName();
			DMEOPrototype oldPrototype = getPrototype();
			if (!isDeserializing() && oldPrototype != null && oldPrototype.getName().equals(BOOLEAN_PROTOTYPE_NAME)
					&& getDMEOEntity() != null) {
				DMProperty p = getDMEOEntity().getDMProperty(getName() + BOOLEAN_METHOD_POSTFIX);
				if (p != null) {
					p.delete();
				}
			}
			if (prototype != null) {
				getEOAttribute().setPrototype(prototype.getEOAttribute());
			} else {
				getEOAttribute().setPrototype(null);
			}
			if (oldColumnName != null) {
				setColumnName(oldColumnName);
			}
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("prototype", oldPrototype, prototype));
		}
		ensureBooleanPropertyCreation();
	}

	private boolean creatingBoolean = false;

	/**
     *
     */
	private void ensureBooleanPropertyCreation() {
		if (creatingBoolean) {
			return;
		}
		creatingBoolean = true;
		try {
			if (!isDeserializing() && getPrototype() != null && getPrototype().getName().equals(BOOLEAN_PROTOTYPE_NAME)
					&& getDMEOEntity() != null && getDMEOEntity().getDMProperty(getName() + BOOLEAN_METHOD_POSTFIX) == null) {
				DMProperty p = new DMProperty(getDMModel(), getName() + BOOLEAN_METHOD_POSTFIX, DMType.makeResolvedDMType(getDMModel()
						.getEntityNamed("boolean")), DMCardinality.SINGLE, true, true, DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
				StringBuilder sb = new StringBuilder();
				sb.append("    return ").append(getName()).append("()!=null && ").append(getName()).append("().equals(")
						.append(getProject().getPrefix()).append("Constants.TRUE_VALUE);");
				// p.setGetterCoreCode(sb.toString());

				try {
					p.setGetterCode(getGetterJavadoc() + StringUtils.LINE_SEPARATOR + getGetterHeader() + " { "
							+ StringUtils.LINE_SEPARATOR + sb.toString() + StringUtils.LINE_SEPARATOR + "}");
				} catch (ParserNotInstalledException e) {
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					e.printStackTrace();
				}

				sb = new StringBuilder();
				sb.append("    set").append(ToolBox.capitalize(getName(), true)).append("(value?").append(getProject().getPrefix())
						.append("Constants.TRUE_VALUE:").append(getProject().getPrefix()).append("Constants.FALSE_VALUE);");
				// p.setSetterCoreCode(sb.toString());

				try {
					p.setSetterCode(getSetterJavadoc() + StringUtils.LINE_SEPARATOR + getSetterHeader() + " { "
							+ StringUtils.LINE_SEPARATOR + sb.toString() + StringUtils.LINE_SEPARATOR + "}");
				} catch (ParserNotInstalledException e) {
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					e.printStackTrace();
				}

				getDMEOEntity().registerProperty(p);
			}
		} finally {
			creatingBoolean = false;
		}
	}

	/**
	 * Overrides setEntity
	 * 
	 * @see org.openflexo.foundation.dm.DMProperty#setEntity(org.openflexo.foundation.dm.DMEntity)
	 */
	@Override
	public void setEntity(DMEntity entity) {
		if (!isDeserializing()) {
			DMEOPrototype proto = getPrototype();
			setPrototype(null);// Small trick to make embedded Boolean property
			// move from one entity to another
			super.setEntity(entity);
			setPrototype(proto);
		} else {
			super.setEntity(entity);
		}
	}

	@Override
	public boolean codeIsComputable() {
		return getPrototype() != null;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class DMEOAttributeMustReferToAValidEORelationship extends ValidationRule {
		public DMEOAttributeMustReferToAValidEORelationship() {
			super(DMEOAttribute.class, "attribute_must_refer_to_a_valid_eo_attribute");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DMEOAttribute property = (DMEOAttribute) object;
			if (property.getEOAttribute() == null) {
				ValidationError error = new ValidationError(this, object, "attribute_($object.name)_must_refer_to_a_valid_eo_attribute");
				return error;
			}
			return null;
		}

	}

	public static class DMEOAttributeMustHaveAJavaClass extends ValidationRule {
		public DMEOAttributeMustHaveAJavaClass() {
			super(DMEOAttribute.class, "eoattribute_must_have_a_javaclass");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DMEOAttribute property = (DMEOAttribute) object;
			if (property.getIsClassProperty() && property.getEOAttribute() != null && property.getEOAttribute().getClassName() == null
					&& property.getEOAttribute().getPrototype() == null) {
				ValidationError error = new ValidationError(this, object, "eoattribute_($object.name)_has_no_java_class");
				for (Enumeration e = property.getDMModel().getEOPrototypeRepository().getEOPrototypeEntity().getAttributes().keys(); e
						.hasMoreElements();) {
					DMEOPrototype prototype = (DMEOPrototype) property.getDMModel().getEOPrototypeRepository().getEOPrototypeEntity()
							.getAttribute((EOAttribute) e.nextElement());
					error.addToFixProposals(new SetPrototypeAttribute(prototype));
				}
				return error;
			}
			return null;
		}

	}

	public static class SetPrototypeAttribute extends FixProposal {
		public DMEOPrototype proto;

		public SetPrototypeAttribute(DMEOPrototype aPrototype) {
			super("set_prototype_for_($object.name)_to_($proto.name)");
			proto = aPrototype;
		}

		@Override
		protected void fixAction() {
			((DMEOAttribute) getObject()).setPrototype(proto);
		}
	}

}
