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
package org.openflexo.foundation.bindings;

import java.text.Collator;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.xmlcode.XMLMapping;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.Typed;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class BindingDefinition extends FlexoModelObject implements InspectableObject, Typed {

	static final Logger logger = Logger.getLogger(BindingDefinition.class.getPackage().getName());

	private FlexoModelObject _owner;

	private String _variableName;

	private DMType _type;

	private boolean _isMandatory;

	// private boolean _isSettable;

	// private DMCardinality cardinality = DMCardinality.SINGLE;

	private BindingDefinitionType _bindingDefinitionType = BindingDefinitionType.GET;

	protected transient FlexoProject _project;

	public static enum BindingDefinitionType {
		GET, SET, GET_SET, EXECUTE
	}

	public BindingDefinition(FlexoModelObject owner) {
		super(owner != null ? owner.getProject() : null);
		_owner = owner;
		if (owner != null)
			_project = owner.getProject();
	}

	/*public BindingDefinition(String variableName, DMEntity type, FlexoModelObject owner, boolean mandatory)
	{
	    this(variableName,(type != null ? DMType.makeResolvedDMType(type) : null) ,owner,mandatory);
	}

	public BindingDefinition(String variableName, DMEntity type, FlexoModelObject owner, DMCardinality cardinality, boolean mandatory)
	{
	    this(variableName,(type != null ? DMType.makeResolvedDMType(type) : null) ,owner,cardinality,mandatory);
	}*/

	public BindingDefinition(String variableName, DMType type, FlexoModelObject owner, BindingDefinitionType bindingType, boolean mandatory) {
		this(owner);
		_variableName = variableName;
		_type = type;
		_isMandatory = mandatory;
		_bindingDefinitionType = bindingType;
	}

	/*public BindingDefinition(String variableName, DMType type, FlexoModelObject owner, DMCardinality cardinality, boolean mandatory)
	{
	    this(variableName, type, owner, mandatory);
	    this.cardinality = cardinality;
	}*/

	@Override
	public boolean equals(Object object) {
		if (object instanceof BindingDefinition) {
			BindingDefinition bd = (BindingDefinition) object;
			if (_variableName == null) {
				if (bd._variableName != null)
					return false;
			} else {
				if (!_variableName.equals(bd._variableName))
					return false;
			}
			return ((_owner == bd._owner) && (_type == bd._type) && (_isMandatory == bd._isMandatory));
		} else {
			return super.equals(object);
		}
	}

	public boolean getIsMandatory() {
		return _isMandatory;
	}

	public void setIsMandatory(boolean mandatory) {
		_isMandatory = mandatory;
		setChanged();
	}

	public boolean getIsSettable() {
		return (getBindingDefinitionType() == BindingDefinitionType.SET || getBindingDefinitionType() == BindingDefinitionType.GET_SET);
	}

	public void setIsSettable(boolean settable) {
		if (getBindingDefinitionType() == BindingDefinitionType.GET) {
			_bindingDefinitionType = BindingDefinitionType.GET_SET;
			setChanged();
		}
		// Otherwise, it is already the case
	}

	public FlexoModelObject getOwner() {
		return _owner;
	}

	public void setOwner(FlexoModelObject owner) {
		_owner = owner;
	}

	@Override
	public DMType getType() {
		return _type;
	}

	@Override
	public void setType(DMType type) {
		logger.info("setType() with " + type);
		if ((type == null && _type != null) || (type != null && !type.equals(_type))) {
			DMType oldType = _type;
			if (oldType != null) {
				oldType.removeFromTypedWithThisType(this);
			}
			_type = type;
			if (type != null) {
				type.addToTypedWithThisType(this);
			}
			setChanged();
		}
	}

	public String getVariableName() {
		if (_variableName == null)
			return "null";
		return _variableName;
	}

	public void setVariableName(String variableName) {
		_variableName = variableName;
		setChanged();
	}

	public String getTypeName() {
		if (getType() != null) {
			return getType().getStringRepresentation();
		}
		return null;
	}

	public String getSimplifiedTypeName() {
		if (getType() != null) {
			return getType().getSimplifiedStringRepresentation();
		}
		return "null";
	}

	public void setTypeName(String fullyQualifiedEntityName) {
		setType(DMType.makeResolvedDMType(getDMModel().getDMEntity(fullyQualifiedEntityName)));
	}

	public DMModel getDMModel() {
		return getProject().getDataModel();
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public String getFullyQualifiedName() {
		return "BINDING_DEFINITION." + getVariableName() + "." + getTypeName();
	}

	@Override
	public XMLMapping getXMLMapping() {
		if (getOwner() != null) {
			return getOwner().getXMLMapping();
		}
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		if (getOwner() != null) {
			return getOwner().getXMLResourceData();
		}
		return null;
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		// Not inspectable by its own
		return null;
	}

	public static final Comparator<BindingDefinition> bindingDefinitionComparator = new BindingDefinitionComparator();

	/**
	 * Used to sort binding definition according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	public static class BindingDefinitionComparator implements Comparator<BindingDefinition> {

		BindingDefinitionComparator() {

		}

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(BindingDefinition o1, BindingDefinition o2) {
			String s1 = o1.getVariableName();
			String s2 = o2.getVariableName();
			if ((s1 != null) && (s2 != null))
				return Collator.getInstance().compare(s1, s2);
			else
				return 0;
		}

	}

	/**
	 * Relevant only this this is implementing Bindable interface
	 * 
	 * @param bd
	 * @param depth
	 * @return
	 */
	public Vector<BindingValue> searchMatchingBindingValue(Bindable bindable, int depth) {
		Vector<BindingValue> returned = new Vector<BindingValue>();

		BindingModel bindingModel = bindable.getBindingModel();
		for (int i = 0; i < bindingModel.getBindingVariablesCount(); i++) {
			BindingValue current = new BindingValue(null, null);
			current.setBindingDefinition(this);
			current.setBindingVariable(bindingModel.getBindingVariableAt(i));
			returned.addAll(searchMatchingBindingValue(bindable, current, bindingModel.getBindingVariableAt(i).getType(), depth));
		}

		return returned;
	}

	private Vector<BindingValue> searchMatchingBindingValue(Bindable bindable, BindingValue current, DMType currentType, int depth) {
		Vector<BindingValue> returned = new Vector<BindingValue>();
		if (depth < 0)
			return returned;
		if (getType() == null)
			return returned;
		if (currentType == null)
			return returned;
		if (getType().getBaseEntity() == null)
			return returned;
		if (getType().getBaseEntity().isAncestorOf(currentType.getBaseEntity())) {
			BindingValue matchingBV = current.clone();
			matchingBV.connect();
			returned.add(matchingBV);
		}
		if (depth == 1)
			return returned;
		for (Enumeration en = currentType.getBaseEntity().getAccessibleProperties().elements(); en.hasMoreElements();) {
			DMProperty nextProperty = (DMProperty) en.nextElement();
			BindingValue newCurrent = current.clone();
			newCurrent.addBindingPathElement(nextProperty);
			returned.addAll(searchMatchingBindingValue(bindable, newCurrent, nextProperty.getType(), depth - 1));
		}
		return returned;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "binding_definition";
	}

	public boolean supportDirectActionEncoding() {
		if (getType() == null)
			return false;
		if (getType().isString())
			return true;
		if (getType().isInteger())
			return true;
		if (getType().isChar())
			return true;
		if (getType().isBoolean())
			return true;
		if (getType().isEOEntity()) {
			DMEOEntity eoentity = (DMEOEntity) getType().getBaseEntity();
			if (eoentity.getPrimaryKeyAttributes().size() == 1) {
				DMEOAttribute pk = eoentity.getPrimaryKeyAttributes().get(0);
				return isASerializablePrimaryKey(pk);
			}
		}
		return false;
	}

	private boolean isASerializablePrimaryKey(DMEOAttribute pk) {
		if (pk.getType() == null)
			return false;
		if (pk.getType().isString())
			return true;
		if (pk.getType().isInteger())
			return true;
		return false;
	}

	public String getTypeStringRepresentation() {
		if (getType() == null)
			return FlexoLocalization.localizedForKey("no_type");
		else
			return getType().getSimplifiedStringRepresentation();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof DMEntityClassNameChanged && observable == getType().getBaseEntity()) {
			// do nothing : no cached code
		} else if (dataModification instanceof EntityDeleted && observable == getType().getBaseEntity()) {
			// do nothing : no cached code
		}
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public BindingDefinitionType getBindingDefinitionType() {
		return _bindingDefinitionType;
	}

}