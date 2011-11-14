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
package org.openflexo.components.widget.binding;

import java.awt.Color;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.openflexo.components.widget.binding.BindingSelectorPanel.BindingColumnElement;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.dm.eo.FlattenRelationshipDefinition;

public class FlattenRelationshipDefinitionSelector extends BindingSelector {

	private FlattenRelationshipDefinitionInfo _flattenRelationshipDefinitionInfo;

	@Override
	public FlattenRelationshipDefinition getEditedObject() {
		return (FlattenRelationshipDefinition) super.getEditedObject();
	}

	protected class FlattenRelationshipDefinitionInfo extends BindingModel implements Bindable {
		private DMEOEntity _sourceEntity;
		private BindingVariable _bindingVariable;
		// protected RootBindingColumnListModel _rootListModel;
		private BindingDefinition _bindingDefinition;

		FlattenRelationshipDefinitionInfo(DMEOEntity sourceEntity) {
			_sourceEntity = sourceEntity;
			_bindingVariable = new BindingVariable(null, sourceEntity.getDMModel(), "");
			_bindingVariable.setVariableName("definition");
			_bindingVariable.setType(DMType.makeResolvedDMType(sourceEntity));
			// _rootListModel = new RootBindingColumnListModel();
			_bindingDefinition = new BindingDefinition("flattenRelationshipDefinition", DMType.makeObjectDMType(sourceEntity.getProject()),
					sourceEntity, BindingDefinitionType.GET, true);
			setBindingDefinition(_bindingDefinition);
			setBindable(this);
		}

		@Override
		public int getBindingVariablesCount() {
			return 1;
		}

		@Override
		public BindingVariable getBindingVariableAt(int index) {
			return _bindingVariable;
		}

		@Override
		public BindingModel getBindingModel() {
			return this;
		}

		@Override
		public boolean allowsNewBindingVariableCreation() {
			return false;
		}

	}

	public FlattenRelationshipDefinitionSelector(BindingValue flattenRelationshipDefinition, DMEOEntity sourceEntity) {
		super(flattenRelationshipDefinition);
		setSourceEntity(sourceEntity);
		activateNormalBindingMode();
	}

	public void updateCustomPanel(BindingValue editedObject) {
		super.updateCustomPanel(editedObject);
	}

	@Override
	protected FlattenRelationshipDefinition makeBinding() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeBindingValue() " + getBindingDefinition());
		}
		if (_flattenRelationshipDefinitionInfo != null) {
			FlattenRelationshipDefinition returned = new FlattenRelationshipDefinition(_flattenRelationshipDefinitionInfo._sourceEntity,
					null);
			return returned;
		} else {
			return null;
		}
	}

	@Override
	protected AbstractListModel getRootListModel() {
		if (_flattenRelationshipDefinitionInfo != null) {
			return getListModelFor(DMType.makeResolvedDMType(_flattenRelationshipDefinitionInfo._sourceEntity));
		}
		return ((BindingSelectorPanel) _selectorPanel).EMPTY_MODEL;
	}

	@Override
	protected void valueSelected(int index, JList list, AbstractBinding flattenRelationshipDefinition) {
		if (flattenRelationshipDefinition instanceof FlattenRelationshipDefinition) {
			FlattenRelationshipDefinition definition = (FlattenRelationshipDefinition) flattenRelationshipDefinition;
			BindingColumnElement selectedValue = (BindingColumnElement) list.getSelectedValue();
			if (selectedValue.getElement() instanceof DMProperty) {
				if (selectedValue.getElement() != definition.getBindingPathElementAtIndex(index)) {
					definition.setBindingPathElementAtIndex((DMProperty) selectedValue.getElement(), index);
					setEditedObject(definition);
					fireEditedObjectChanged();
				}
			}
		}
	}

	public DMEOEntity getSourceEntity() {
		if (_flattenRelationshipDefinitionInfo != null) {
			return _flattenRelationshipDefinitionInfo._sourceEntity;
		}
		return null;
	}

	public void setSourceEntity(DMEOEntity sourceEntity) {
		if (sourceEntity != null && sourceEntity != getSourceEntity()) {
			_flattenRelationshipDefinitionInfo = new FlattenRelationshipDefinitionInfo(sourceEntity);
			if (_selectorPanel != null) {
				_selectorPanel.update();
			}
		}
	}

	@Override
	protected ResizablePanel createCustomPanel(AbstractBinding editedObject) {
		_selectorPanel = new FlattenRelationshipDefinitionSelectorPanel();
		_selectorPanel.init();
		/*if (_bindable != null) {
			setBindingModel(_bindable.getBindingModel());
		}*/
		return _selectorPanel;
	}

	protected class FlattenRelationshipDefinitionSelectorPanel extends BindingSelectorPanel {

		protected FlattenRelationshipDefinitionSelectorPanel() {
			super(FlattenRelationshipDefinitionSelector.this);
			// TODO Auto-generated constructor stub
		}

		protected class RelationshipsOnlyListModel extends BindingColumnListModel {
			private DMEOEntity _entity;
			private Vector<BindingColumnElement> _elements;

			RelationshipsOnlyListModel(DMEOEntity entity) {
				super();
				_entity = entity;
				_elements = new Vector<BindingColumnElement>();
				updateValues();
			}

			@Override
			public void updateValues() {
				_elements.clear();
				for (DMEORelationship r : _entity.getOrderedRelationships()) {
					_elements.add(new BindingColumnElement(r, r.getType()));
				}
			}

			@Override
			public int getUnfilteredSize() {
				return _elements.size();
			}

			@Override
			public BindingColumnElement getUnfilteredElementAt(int index) {
				return _elements.elementAt(index);
			}

		}

		class EmptyColumnListModel extends BindingColumnListModel {
			@Override
			public int getUnfilteredSize() {
				return 0;
			}

			@Override
			public BindingColumnElement getUnfilteredElementAt(int index) {
				return null;
			}

		}

		protected boolean displayOptions() {
			return false;
		}

		@Override
		protected void update() {
			BindingValue bindingValue = getEditedObject();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("update with " + bindingValue);
			}
			/*if (bindingValue != null) {
				setBindingDefinition(bindingValue.getBindingDefinition());
			}*/
			listAtIndex(0).setModel(getRootListModel());

			int lastUpdatedList = 0;
			if (bindingValue != null && bindingValue.getBindingPath() != null) {
				for (int i = 0; i < bindingValue.getBindingPath().size(); i++) {
					BindingPathElement pathElement = bindingValue.getBindingPath().elementAt(i);
					if (i + 1 == getVisibleColsCount()) {
						makeNewJList();
					}
					if (!((bindingValue.isConnected()) && (bindingValue.isLastBindingPathElement(pathElement, i)))) {
						listAtIndex(i + 1).setModel(getListModelFor(pathElement.getType()));
						lastUpdatedList = i + 1;
					}
					listAtIndex(i).removeListSelectionListener(this);
					if (pathElement instanceof DMProperty) {
						BindingColumnElement propertyElementToSelect = (listAtIndex(i).getModel()).getElementFor(pathElement);
						listAtIndex(i).setSelectedValue(propertyElementToSelect, true);
						// listAtIndex(i).setSelectedValue(pathElement, true);
					}
					listAtIndex(i).addListSelectionListener(this);
				}
			}

			// Remove unused lists
			int lastVisibleList = defaultVisibleColCount - 1;
			if (lastUpdatedList > lastVisibleList) {
				lastVisibleList = lastUpdatedList;
			}

			// logger.info("Last visible: "+lastVisibleList);
			// logger.info("Last updated: "+lastUpdatedList);

			int currentSize = getVisibleColsCount();
			for (int i = lastVisibleList + 1; i < currentSize; i++) {
				JList toRemove = listAtIndex(getVisibleColsCount() - 1);
				deleteJList(toRemove);
			}
			// Sets model to null for visible but unused lists
			for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
				JList list = listAtIndex(i);
				list.setModel(EMPTY_MODEL);
			}

			// Set connect button state
			_connectButton.setEnabled((bindingValue != null) && (bindingValue.isBindingValid()));

			if (bindingValue != null) {
				getTextField().setForeground(bindingValue.isBindingValid() ? Color.BLACK : Color.RED);
			}

			updateSearchedTypeLabel();
		}

		@Override
		protected BindingColumnListModel makeColumnListModel(DMType type) {
			if (type.getKindOfType() == DMType.KindOfType.RESOLVED && type.getBaseEntity() instanceof DMEOEntity) {
				return new RelationshipsOnlyListModel((DMEOEntity) type.getBaseEntity());
			} else {
				return EMPTY_MODEL;
			}
		}

	}

}
