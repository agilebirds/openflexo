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
package org.openflexo.foundation.wkf.node;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.IntegerStaticBinding;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Activity related to execution of a sub-process within a loop
 * 
 * @author sguerin
 * 
 */
public class LoopSubProcessNode extends SubProcessNode {

	private static final Logger logger = Logger.getLogger(LoopSubProcessNode.class.getPackage().getName());

	private LoopType loopType = LoopType.WHILE;

	public static final String CONDITION = "condition";
	private AbstractBinding _condition;

	public static final String ITERATOR = "iterator";
	private BindingValue _iterator;

	public static final String BEGIN_VALUE = "beginValue";
	private AbstractBinding _beginValue = new IntegerStaticBinding(BEGIN_VALUE_BINDING_DEFINITION(), this, 0);

	public static final String END_VALUE = "endValue";
	private AbstractBinding _endValue = new IntegerStaticBinding(BEGIN_VALUE_BINDING_DEFINITION(), this, 10);

	public static final String INCREMENT_VALUE = "incrementValue";
	private AbstractBinding _incrementValue = new IntegerStaticBinding(BEGIN_VALUE_BINDING_DEFINITION(), this, 1);

	public static final String ITERATION_COLLECTION = "iterationCollection";
	private AbstractBinding _iterationCollection;

	public static final String COLLECTION_ITEM = "collectionItem";
	private BindingValue _collectionItem;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public LoopSubProcessNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public LoopSubProcessNode(FlexoProcess process) {
		super(process);
	}

	/**
	 * Dynamic constructor with ServiceInterface...
	 */
	public LoopSubProcessNode(FlexoProcess process, ServiceInterface _interface) {
		this(process);
		setServiceInterface(_interface);
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.wkf.node.AbstractActivityNode#delete()
	 */
	@Override
	public final boolean delete() {
		super.delete();
		deleteObservers();
		return true;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "sequential_sub_process";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.LOOP_SUB_PROCESS_NODE_INSPECTOR;
	}

	@Override
	public boolean mightHaveOperationPetriGraph() {
		return false;
	}

	public AbstractBinding getCondition() {
		if (isBeingCloned()) {
			return null;
		}
		return _condition;
	}

	public void setCondition(AbstractBinding condition) {
		AbstractBinding oldBindingValue = _condition;
		_condition = condition;
		if (_condition != null) {
			_condition.setOwner(this);
			_condition.setBindingDefinition(CONDITION_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(CONDITION, oldBindingValue, condition));
	}

	public BindingValue getIterator() {
		if (isBeingCloned()) {
			return null;
		}
		return _iterator;
	}

	public void setIterator(BindingValue iterator) {
		AbstractBinding oldBindingValue = _iterator;
		_iterator = iterator;
		if (_iterator != null) {
			_iterator.setOwner(this);
			_iterator.setBindingDefinition(ITERATOR_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(ITERATOR, oldBindingValue, iterator));
	}

	public AbstractBinding getBeginValue() {
		if (isBeingCloned()) {
			return null;
		}
		return _beginValue;
	}

	public void setBeginValue(AbstractBinding beginValue) {
		AbstractBinding oldBindingValue = _beginValue;
		_beginValue = beginValue;
		if (_beginValue != null) {
			_beginValue.setOwner(this);
			_beginValue.setBindingDefinition(BEGIN_VALUE_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(BEGIN_VALUE, oldBindingValue, beginValue));
	}

	public AbstractBinding getEndValue() {
		if (isBeingCloned()) {
			return null;
		}
		return _endValue;
	}

	public void setEndValue(AbstractBinding endValue) {
		AbstractBinding oldBindingValue = _endValue;
		_endValue = endValue;
		if (_endValue != null) {
			_endValue.setOwner(this);
			_endValue.setBindingDefinition(END_VALUE_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(END_VALUE, oldBindingValue, endValue));
	}

	public AbstractBinding getIncrementValue() {
		if (isBeingCloned()) {
			return null;
		}
		return _incrementValue;
	}

	public void setIncrementValue(AbstractBinding incrementValue) {
		AbstractBinding oldBindingValue = _incrementValue;
		_incrementValue = incrementValue;
		if (_incrementValue != null) {
			_incrementValue.setOwner(this);
			_incrementValue.setBindingDefinition(INCREMENT_VALUE_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(INCREMENT_VALUE, oldBindingValue, incrementValue));
	}

	public AbstractBinding getIterationCollection() {
		if (isBeingCloned()) {
			return null;
		}
		return _iterationCollection;
	}

	public void setIterationCollection(AbstractBinding iterationCollection) {
		AbstractBinding oldBindingValue = _iterationCollection;
		_iterationCollection = iterationCollection;
		if (_iterationCollection != null) {
			_iterationCollection.setOwner(this);
			_iterationCollection.setBindingDefinition(ITERATION_COLLECTION_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(ITERATION_COLLECTION, oldBindingValue, iterationCollection));
	}

	public BindingValue getCollectionItem() {
		if (isBeingCloned()) {
			return null;
		}
		return _collectionItem;
	}

	public void setCollectionItem(BindingValue collectionItem) {
		AbstractBinding oldBindingValue = _collectionItem;
		_collectionItem = collectionItem;
		if (_collectionItem != null) {
			_collectionItem.setOwner(this);
			_collectionItem.setBindingDefinition(COLLECTION_ITEM_BINDING_DEFINITION());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(COLLECTION_ITEM, oldBindingValue, collectionItem));
	}

	public WKFBindingDefinition CONDITION_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, CONDITION, Boolean.class, BindingDefinitionType.GET, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public WKFBindingDefinition ITERATOR_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, ITERATOR, Number.class, BindingDefinitionType.GET_SET, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public WKFBindingDefinition BEGIN_VALUE_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, BEGIN_VALUE, Number.class, BindingDefinitionType.GET, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public WKFBindingDefinition END_VALUE_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, END_VALUE, Number.class, BindingDefinitionType.GET, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public WKFBindingDefinition INCREMENT_VALUE_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, INCREMENT_VALUE, Number.class, BindingDefinitionType.GET, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public WKFBindingDefinition ITERATION_COLLECTION_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, ITERATION_COLLECTION, List.class, BindingDefinitionType.GET,
					false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public WKFBindingDefinition COLLECTION_ITEM_BINDING_DEFINITION() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, COLLECTION_ITEM, Object.class, BindingDefinitionType.GET_SET,
					false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public LoopType getLoopType() {
		return loopType;
	}

	public void setLoopType(LoopType loopType) {
		if (this.loopType != loopType) {
			LoopType oldValue = loopType;
			this.loopType = loopType;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("loopType", oldValue, loopType));
		}
	}

}
