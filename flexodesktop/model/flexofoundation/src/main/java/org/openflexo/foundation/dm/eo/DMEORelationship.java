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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMRegExp;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMPropertyNameChanged;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOJoin;
import org.openflexo.foundation.dm.eo.model.EOProperty;
import org.openflexo.foundation.dm.eo.model.EORelationship;
import org.openflexo.foundation.dm.eo.model.InvalidJoinException;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;

/**
 * Represents an accessor as a get/set key-value pair mapping a data stored in a database, implemented in DB as a relationship to an other
 * table.
 * 
 * @author sguerin
 * 
 */
public class DMEORelationship extends DMEOProperty implements Bindable {

	private static final Logger logger = Logger.getLogger(DMEORelationship.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	protected EORelationship _eoRelationship;

	private boolean _isFlattenRelationship = false;
	private String _lastKnownFlattenRelationshipDefinition = null;
	private FlattenRelationshipDefinition _flattenRelationshipDefinition = null;
	private FlattenRelationshipDefinitionBindingModel _flattenRelationshipDefinitionBindingModel;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMEORelationship(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);

	}

	/**
	 * Default constructor
	 */
	public DMEORelationship(DMModel dmModel) {
		super(dmModel);
		_dmEOJoins = new Vector<DMEOJoin>();
		_dmEOJoinsNeedToBeRecomputed = true;
		_destinationEntity = null;
		_destinationType = null;
	}

	/**
	 * Default constructor for dynamic creation
	 */
	public DMEORelationship(DMModel dmModel, EORelationship eoRelationship) {
		this(dmModel);
		_eoRelationship = eoRelationship;
		if (eoRelationship != null) {
			// setName(eoRelationship.name());
			name = eoRelationship.getName();
		}
		if (eoRelationship.getIsFlattened()) {
			_isFlattenRelationship = true;
		}
	}

	/**
	 * Used for dynamic creation of normal relationship
	 */
	public static DMEORelationship createsNewDMEORelationship(DMModel dmModel, DMEOEntity dmEOEntity, String name, boolean isReadOnly,
			boolean isSettable, DMPropertyImplementationType implementationType) throws EOAccessException {
		EORelationship eoRelationship = new EORelationship();
		eoRelationship.setName(name);
		try {
			dmEOEntity.getEOEntity().addRelationship(eoRelationship);
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("EOControl management failed :" + e.getMessage());
			}
			throw new EOAccessException(e);
		}
		DMEORelationship answer = new DMEORelationship(dmModel, eoRelationship);
		dmEOEntity.registerProperty(answer);
		answer.setIsReadOnly(isReadOnly);
		answer.setIsClassProperty(true);
		answer.setIsSettable(isSettable);
		answer.setImplementationType(implementationType);
		answer.setPropagatesPrimaryKey(false);
		answer.setOwnsDestination(false);
		return answer;
	}

	/**
	 * Used for dynamic creation of flatten relationship
	 */
	public static DMEORelationship createsNewFlattenDMEORelationship(DMModel dmModel, DMEOEntity dmEOEntity, String name,
			String definition, boolean isReadOnly, boolean isSettable, DMPropertyImplementationType implementationType)
			throws EOAccessException {
		EORelationship eoRelationship = new EORelationship();
		eoRelationship.setName(name);
		eoRelationship.setDefinition(definition);
		try {
			dmEOEntity.getEOEntity().addRelationship(eoRelationship);
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("EOControl management failed :" + e.getMessage());
			}
			throw new EOAccessException(e);
		}
		DMEORelationship answer = new DMEORelationship(dmModel, eoRelationship);
		dmEOEntity.registerProperty(answer);
		answer._isFlattenRelationship = true;
		answer.setIsReadOnly(isReadOnly);
		answer.setIsClassProperty(true);
		answer.setIsSettable(isSettable);
		answer.setImplementationType(implementationType);
		answer.setPropagatesPrimaryKey(false);
		answer.setOwnsDestination(false);
		return answer;
	}

	/**
	 * Overrides getEmbeddedDMObjects
	 * 
	 * @see org.openflexo.foundation.dm.DMProperty#getEmbeddedDMObjects()
	 */
	@Override
	public Vector<DMObject> getEmbeddedDMObjects() {
		Vector<DMObject> v = super.getEmbeddedDMObjects();
		if (v instanceof EmptyVector) {
			v = new Vector<DMObject>();
		}
		v.addAll(getDMEOJoins());
		return v;
	}

	@Override
	public void delete() {
		if (getEORelationship() != null) {
			resetJoins();
			try {
				if (getDMEOEntity() != null && getDMEOEntity().getEOEntity() != null) {
					getDMEOEntity().getEOEntity().removeRelationship(getEORelationship());
				} else if (getEORelationship().getEntity() != null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("No parent DMEOEntity or no EOEntity declared for DMEOEntity. Trying to proceed anyway.");
					}
					getEORelationship().getEntity().removeRelationship(getEORelationship());
				}
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOControl management failed :" + e.getMessage());
				}
			}
		}
		super.delete();
		_eoRelationship = null;
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (getDMRepository() != null && getDMRepository().isReadOnly()) {
			return Inspectors.DM.DM_RO_EO_RELATIONSHIP_INSPECTOR;
		} else {
			if (getIsFlattenRelationship()) {
				return Inspectors.DM.DM_EO_FLATTEN_RELATIONSHIP_INSPECTOR;
			} else {
				return Inspectors.DM.DM_EO_RELATIONSHIP_INSPECTOR;
			}
		}
	}

	public EORelationship getEORelationship() {
		// logger.info("getEORelationship(), _eoRelationship="+_eoRelationship+" getDMEOEntity()="+getDMEOEntity());
		if (_eoRelationship == null) {
			if (getDMEOEntity() != null && getDMEOEntity().getEOEntity() != null) {
				try {
					// logger.info("Build EORelationship");
					_eoRelationship = getDMEOEntity().getEOEntity().relationshipNamed(getName());
				} catch (IllegalArgumentException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find EORelationship named " + getName() + " : EOControl management failed");
					}
				}
				if (_eoRelationship == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find EORelationship named " + getName());
					}
				}
			}
		}
		return _eoRelationship;
	}

	public void setEORelationship(EORelationship attribute) {
		_eoRelationship = attribute;
	}

	@Override
	public void setName(String newName) throws IllegalArgumentException, InvalidNameException {
		if (name == null || !name.equals(newName)) {
			if (!isDeserializing() && (newName == null || !DMRegExp.ENTITY_NAME_PATTERN.matcher(newName).matches())) {
				throw new InvalidNameException("'" + newName + "' is not a valid name for relationship.");
			}
			DMEntity containerEntity = getEntity();
			if (getEORelationship() != null) {
				boolean isClassProperty = _eoRelationship.getIsClassProperty();
				EOEntity entity = _eoRelationship.getEntity();
				if (entity != null) {
					entity.removeRelationship(_eoRelationship);
				}
				_eoRelationship.setName(newName);
				if (entity != null) {
					try {
						entity.addRelationship(_eoRelationship);
					} catch (IllegalArgumentException e) {
						_eoRelationship.setName(name);
						entity.addRelationship(_eoRelationship);
						throw e;
					}
					_eoRelationship.setIsClassProperty(isClassProperty);
				}
			}
			if (containerEntity != null) {
				containerEntity.unregisterProperty(this, false);
			}
			String oldName = name;
			name = newName;

			if (containerEntity != null) {
				containerEntity.registerProperty(this, false);
			}

			updateCode();

			setChanged();
			notifyObservers(new DMPropertyNameChanged(this, oldName, newName));

			if (containerEntity != null) {
				containerEntity.notifyReordering(this);
			}

		}
	}

	public boolean getIsToMany() {
		if (getIsFlattenRelationship()) {
			if (getFlattenRelationshipDefinition() != null && getFlattenRelationshipDefinition().getBindingPathLastElement() != null) {
				// isToMany iff at least one of path element is a toMany
				for (int i = 0; i < getFlattenRelationshipDefinition().getBindingPathElementCount(); i++) {
					DMEORelationship r = getFlattenRelationshipDefinition().getBindingPathElementAtIndex(i);
					if (!r.getIsFlattenRelationship() && r.getIsToMany()) {
						return true;
					}
				}
			}
			return false;
		} else {
			if (getEORelationship() != null) {
				return getEORelationship().getIsToMany();
			}
			return true;
		}
	}

	public boolean getIsManyTo() {
		DMEORelationship inv = getInverse();
		if (inv != null) {
			return inv.getIsToMany();
		}
		return false;
	}

	public void setIsToMany(boolean aBoolean) {

		if (getEORelationship() != null) {
			boolean old = getEORelationship().getIsToMany();
			if (old && aBoolean || !old && !aBoolean) {
				return;
			}
			getEORelationship().setIsToMany(aBoolean);
			updateCode();
			setChanged();
			notifyObservers(new DMAttributeDataModification("isToMany", new Boolean(!aBoolean), new Boolean(aBoolean)));
			setChanged();
			notifyObservers(new DMAttributeDataModification("cardinality", aBoolean ? DMCardinality.SINGLE : DMCardinality.VECTOR,
					!aBoolean ? DMCardinality.SINGLE : DMCardinality.VECTOR));
		}
	}

	public boolean getIsFlattenRelationship() {
		return _isFlattenRelationship;
	}

	// Normally immutable: never call this except during deserialization
	public void _setIsFlattenRelationship(boolean isFlattenRelationship) {
		if (isDeserializing()) {
			_isFlattenRelationship = isFlattenRelationship;
		}
	}

	// private String _lastKnownFlattenRelationshipDefinition = null;

	public FlattenRelationshipDefinition getFlattenRelationshipDefinition() {
		if (getEORelationship() != null) {
			if (_flattenRelationshipDefinition != null && _lastKnownFlattenRelationshipDefinition != null
					&& _lastKnownFlattenRelationshipDefinition.equals(getEORelationship().getDefinition())) {
				return _flattenRelationshipDefinition;
			} else {
				// Rebuild it
				_flattenRelationshipDefinition = new FlattenRelationshipDefinition(this, getEORelationship().getDefinition());
				_lastKnownFlattenRelationshipDefinition = _flattenRelationshipDefinition.getStringRepresentation(); // Avoid recreation
			}
		}
		return null;
	}

	public void setFlattenRelationshipDefinition(FlattenRelationshipDefinition flattenRelationshipDefinition) {
		if (getEORelationship() != null) {
			getEORelationship().setDefinition(flattenRelationshipDefinition.getStringRepresentation());
		}
		_flattenRelationshipDefinition = flattenRelationshipDefinition;
		_lastKnownFlattenRelationshipDefinition = _flattenRelationshipDefinition.getStringRepresentation(); // Avoid recreation
	}

	@Override
	public DMEOEntity getEntity() {
		return (DMEOEntity) super.getEntity();
	}

	public FlattenRelationshipDefinitionBindingModel getFlattenRelationshipDefinitionBindingModel() {
		if (_flattenRelationshipDefinitionBindingModel == null && getEntity() != null) {
			_flattenRelationshipDefinitionBindingModel = new FlattenRelationshipDefinitionBindingModel();
		}
		return _flattenRelationshipDefinitionBindingModel;
	}

	@Override
	public BindingModel getBindingModel() {
		return getFlattenRelationshipDefinitionBindingModel();
	}

	protected class FlattenRelationshipDefinitionBindingModel extends BindingModel {
		private BindingVariable _bindingVariable;

		FlattenRelationshipDefinitionBindingModel() {
			_bindingVariable = new BindingVariable(null, getEntity().getDMModel(), "");
			_bindingVariable.setVariableName("definition");
			_bindingVariable.setType(DMType.makeResolvedDMType(getEntity()));
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
		public boolean allowsNewBindingVariableCreation() {
			return false;
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
		return getEORelationship();
	}

	@Override
	public DMCardinality getCardinality() {
		if (getIsToMany()) {
			return DMCardinality.VECTOR;
		}
		return DMCardinality.SINGLE;
	}

	@Override
	public void setCardinality(DMCardinality cardinality) {
		if (cardinality == DMCardinality.SINGLE) {
			setIsToMany(false);
		} else if (cardinality == DMCardinality.VECTOR) {
			setIsToMany(true);
		}
	}

	@Override
	public DMType getType() {
		if (getIsFlattenRelationship()) {
			if (getFlattenRelationshipDefinition() != null && getFlattenRelationshipDefinition().getBindingPathLastElement() != this) {
				return getFlattenRelationshipDefinition().getBindingPathLastElementType();
			} else {
				return null;
			}
		}
		if (_destinationType == null && getDestinationEntity() != null) {
			_destinationType = DMType.makeResolvedDMType(getDestinationEntity());
		}
		return _destinationType;
	}

	@Override
	public void setType(DMType type) {
		// Non relevant
	}

	public DeleteRuleType getDeleteRule() {
		if (getEORelationship() != null) {
			return DeleteRuleType.getDeleteRule(getEORelationship().getDeleteRule());
		}
		if (!isDeserializing() && logger.isLoggable(Level.SEVERE)) {
			logger.severe("DMEORelationship: " + getName() + " has no associated EORelationship.");
		}
		return DeleteRuleType.NULLIFY;

	}

	public boolean isNullify() {
		return getDeleteRule() == null || getDeleteRule().equals(DeleteRuleType.NULLIFY);
	}

	public boolean isCascade() {
		return getDeleteRule() != null && getDeleteRule().equals(DeleteRuleType.CASCADE);
	}

	public boolean isDeny() {
		return getDeleteRule() != null && getDeleteRule().equals(DeleteRuleType.DENY);
	}

	public boolean isNoAction() {
		return getDeleteRule() != null && getDeleteRule().equals(DeleteRuleType.NO_ACTION);
	}

	public void setDeleteRule(DeleteRuleType deleteRule) {
		if (getEORelationship() != null) {
			getEORelationship().setDeleteRule(deleteRule.getEOCode());
			setChanged();
		}
	}

	public JoinSemanticType getJoinSemantic() {
		if (getEORelationship() != null) {
			return JoinSemanticType.getJoinSemanticType(getEORelationship().getJoinSemantic());
		}
		return isDeserializing() ? null : JoinSemanticType.INNER;

	}

	public void setJoinSemantic(JoinSemanticType joinSemantic) {
		if (getEORelationship() != null) {
			getEORelationship().setJoinSemantic(joinSemantic.getEOCode());
			updateCode();
			setChanged();
		}
	}

	public boolean getIsMandatory() {
		if (getEORelationship() != null) {
			return getEORelationship().getIsMandatory();
		}
		return false;

	}

	public void setIsMandatory(boolean isMandatory) {
		if (getEORelationship() != null) {
			getEORelationship().setIsMandatory(isMandatory);
			updateCode();
			setChanged();
		}
	}

	public boolean getOwnsDestination() {
		if (getEORelationship() != null) {
			return getEORelationship().getOwnsDestination();
		}
		return false;

	}

	public void setOwnsDestination(boolean ownsDestination) {
		if (getEORelationship() != null) {
			getEORelationship().setOwnsDestination(ownsDestination);
			updateCode();
			setChanged();
		}
	}

	public boolean getPropagatesPrimaryKey() {
		if (getEORelationship() != null) {
			return getEORelationship().getPropagatesPrimaryKey();
		}
		return false;

	}

	public void setPropagatesPrimaryKey(boolean propagatesPrimaryKey) {
		if (getEORelationship() != null) {
			getEORelationship().setPropagatesPrimaryKey(propagatesPrimaryKey);
			updateCode();
			setChanged();
		}
	}

	public int getNumberOfToManyFaultsToBatchFetch() {
		if (getEORelationship() != null) {
			return getEORelationship().getNumberOfToManyFaultsToBatchFetch();
		}
		return 0;

	}

	public void setNumberOfToManyFaultsToBatchFetch(int numberOfToManyFaultsToBatchFetch) {
		if (getEORelationship() != null) {
			getEORelationship().setNumberOfToManyFaultsToBatchFetch(numberOfToManyFaultsToBatchFetch);
			setChanged();
		}
	}

	public DMEORelationship getInverse() {
		try {
			EOEntity dest = getDestinationEntity().getEOEntity();
			for (EORelationship rel : dest.getRelationships()) {
				DMEORelationship inverseCandidate = getDestinationEntity().getDMEORelationship(rel);
				if (isInverseOf(inverseCandidate)) {
					return inverseCandidate;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private boolean isInverseOf(DMEORelationship inverseCandidate) {
		if (_eoRelationship.getEntity() == inverseCandidate.getDestinationEntity().getEOEntity()) {
			if (attributesMatches(_eoRelationship.getSourceAttributes(), inverseCandidate.getEORelationship().getDestinationAttributes())) {
				if (attributesMatches(_eoRelationship.getDestinationAttributes(), inverseCandidate.getEORelationship()
						.getSourceAttributes())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean attributesMatches(List<EOAttribute> att1List, List<EOAttribute> att2List) {
		if (att1List.size() == att2List.size()) {
			List<EOAttribute> arr = new Vector<EOAttribute>(att2List);
			Iterator<EOAttribute> i = att1List.iterator();
			while (i.hasNext()) {
				arr.remove(i.next());
			}
			return arr.size() == 0;
		}
		return false;
	}

	// =========================================================
	// =================== Joins management ====================
	// =========================================================

	private Vector<DMEOJoin> _dmEOJoins = new Vector<DMEOJoin>();

	private boolean _dmEOJoinsNeedToBeRecomputed = true;

	private DMEOEntity _destinationEntity = null;
	private DMType _destinationType = null;

	public Vector<DMEOJoin> getDMEOJoins() {
		if (_dmEOJoinsNeedToBeRecomputed) {
			rebuildDMEOJoins();
		}
		return _dmEOJoins;
	}

	public void addToDMEOJoins(DMEOJoin join) {
		_dmEOJoins.add(join);
		updateCode();
	}

	public void removeFromDMEOJoins(DMEOJoin join) {
		_dmEOJoins.remove(join);
		updateCode();
	}

	private void rebuildDMEOJoins() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("rebuildDMEOJoins()");
		}
		resetJoins();
		if (getEORelationship() != null) {
			// for (EOJoin j : getEORelationship().getJoins()) logger.info("Join: "+j);
			for (Iterator<EOJoin> i = getEORelationship().getJoins().iterator(); i.hasNext();) {
				EOJoin next = i.next();
				addToDMEOJoins(new DMEOJoin(this, next));
			}
			_dmEOJoinsNeedToBeRecomputed = false;
		}
	}

	public void updateJoins() {
		_dmEOJoinsNeedToBeRecomputed = true;
		getDMEOJoins();
	}

	private void resetJoins() {
		Vector<DMEOJoin> toDelete = new Vector<DMEOJoin>();
		toDelete.addAll(_dmEOJoins);
		for (DMEOJoin next : toDelete) {
			next.delete(false);
		}
		_dmEOJoins.clear();
	}

	/**
	 * Overrides parent's method by adressing an NSArray instead of a Vector<Type> in case of a EOF toMany relationship
	 */
	@Override
	public DMType getResultingType() {
		if (getIsToMany() && !isDeserializing()) {
			DMEntity NSArrayEntity = getDMModel().getEntityNamed("com.webobjects.foundation.NSArray");
			if (NSArrayEntity == null) {
				logger.warning("Could not access DMEntity com.webobjects.foundation.NSArray");
				return DMType.makeUnresolvedDMType("com.webobjects.foundation.NSArray");
			} else {
				return DMType.makeResolvedDMType(NSArrayEntity);
			}
		}
		return super.getResultingType();
	}

	/**
	 * Overrides parent's method by using a NSMutableArray parameter for a toMany relationship
	 */
	@Override
	protected String[] getSetterSignatureCandidates() {
		if (getIsToMany()) {
			String[] candidates = { getSetterName() + "(NSMutableArray)" };
			return candidates;
		} else {
			return super.getSetterSignatureCandidates();
		}
	}

	/**
	 * Overrides parent's method by using a NSMutableArray parameter for a toMany relationship
	 */
	@Override
	protected String getSetterHeader() {
		if (getIsToMany()) {
			StringBuffer methodHeader = new StringBuffer();
			methodHeader.append(getSetterModifier());
			methodHeader.append("void ");
			methodHeader.append(getSetterName());
			methodHeader.append("(");
			methodHeader.append("NSMutableArray");
			methodHeader.append(" ");
			methodHeader.append(getSetterParamName());
			methodHeader.append(")");
			return methodHeader.toString();
		} else {
			return super.getSetterHeader();
		}
	}

	public DMEOEntity getDestinationEntity() {
		if (getIsFlattenRelationship()) {
			if (getFlattenRelationshipDefinition() != null && getFlattenRelationshipDefinition().getBindingPathLastElementType() != null) {
				return (DMEOEntity) getFlattenRelationshipDefinition().getBindingPathLastElementType().getBaseEntity();
			}
			return null;
		} else {
			if (getEORelationship() != null) {
				EOEntity destinationEOEntity = getEORelationship().getDestinationEntity();
				if (destinationEOEntity != null) {
					_destinationEntity = getDMModel().getDMEOEntity(destinationEOEntity);
					if (_destinationEntity == null) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Could not find DMEOEntity named " + destinationEOEntity.getName());
						}
					} else {
						_destinationType = DMType.makeResolvedDMType(_destinationEntity);
					}
				} else {
					if (_destinationEntity != null) {
						DMEOEntity old = _destinationEntity;
						_destinationEntity = null;
						_destinationType = null;
						setChanged();
						notifyObservers(new DMAttributeDataModification("destinationEntity", old, null));
					}
				}
			}
		}
		return _destinationEntity;
	}

	public void setDestinationEntity(DMEOEntity destinationEntity, boolean performAutoJoin) {
		DMEOEntity oldDestinationEntity = getDestinationEntity();
		if (destinationEntity != oldDestinationEntity) {
			resetJoins();
			_destinationEntity = destinationEntity;
			if (_destinationEntity == null) {
				_destinationType = null;
			} else {
				_destinationType = DMType.makeResolvedDMType(_destinationEntity);
			}
			updateCode();
			if (getEORelationship() != null) {
				if (destinationEntity != null) {
					getEORelationship().setDestinationEntity(destinationEntity.getEOEntity());
				} else {
					getEORelationship().setDestinationEntity(null);
				}
			}
			// Warning: notifications must be done after udating completely the model, otherwise, observers will be notified too soon and
			// will get de-synchronized with the model.
			setChanged();
			notifyObservers(new DMAttributeDataModification("destinationEntity", oldDestinationEntity, destinationEntity));
			setChanged();
			notifyObservers(new DMAttributeDataModification("type", oldDestinationEntity, destinationEntity));
			setChanged();
			notifyObservers(new DMAttributeDataModification("DMEOJoins", null, null));
			if (destinationEntity != null && performAutoJoin) {
				// Automatic name if it was not set yet
				if (getName() != null && getName().startsWith(FlexoLocalization.localizedForKey("default_new_relationship_name"))
						&& destinationEntity.getName() != null && destinationEntity.getName().trim().length() > 2) {
					try {
						setName(Character.toLowerCase(destinationEntity.getName().charAt(0)) + destinationEntity.getName().substring(1)
								+ (getIsToMany() ? "s" : ""));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvalidNameException e) {
						e.printStackTrace();
					}
				}
				// Automatic join if possible
				boolean throwException = joinAutomatically(destinationEntity, getIsToMany());
				if (!throwException) {
					throwException = joinAutomatically(destinationEntity, !getIsToMany());
					if (throwException) {
						setIsToMany(!getIsToMany());
						if (getIsToMany() && getName() != null && !getName().endsWith("s")) {
							try {
								setName(getName() + "s");
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvalidNameException e) {
								e.printStackTrace();
							}
						} else if (!getIsToMany() && getName() != null && getName().endsWith("s")) {
							try {
								setName(getName().substring(0, getName().length() - 1));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvalidNameException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	// Info only: description of what is this relation (joins or definition when flattened)
	public String getRelationshipDefinition() {
		if (getIsFlattenRelationship()) {
			if (getFlattenRelationshipDefinition() != null) {
				return getFlattenRelationshipDefinition().getStringRepresentation();
			} else {
				return "-";
			}
		} else {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			sb.append("(");
			for (DMEOJoin join : getDMEOJoins()) {
				sb.append((isFirst ? "" : ",")
						+ (join.getSourceEntity() != null && join.getSourceAttribute() != null ? join.getSourceEntity().getName() + "."
								+ join.getSourceAttribute().getName() : join.getSourceEntity() != null ? FlexoLocalization
								.localizedForKey("unresolved_attribute") : FlexoLocalization.localizedForKey("unresolved_entity"))
						+ "-"
						+ (join.getDestinationEntity() != null && join.getDestinationAttribute() != null ? join.getDestinationEntity()
								.getName() + "." + join.getDestinationAttribute().getName()
								: join.getDestinationEntity() != null ? FlexoLocalization.localizedForKey("unresolved_attribute")
										: FlexoLocalization.localizedForKey("unresolved_entity")));
				isFirst = false;
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public void setDestinationEntity(DMEOEntity destinationEntity) {
		setDestinationEntity(destinationEntity, true);
	}

	/**
	 * Creates joins automatically if attributes with the same name (if exact case is not found, an attempt ignoring the case will be made
	 * but it won't work all the time) and same type can be found
	 * 
	 * @param destinationEntity
	 * @param isToMany
	 * @return
	 */
	private boolean joinAutomatically(DMEOEntity destinationEntity, boolean isToMany) {
		boolean throwException = false;
		if (!isToMany) {
			for (DMEOAttribute a : destinationEntity.getPrimaryKeyAttributes()) {
				DMEOAttribute source = getDMEOEntity().getAttributeNamed(a.getName());
				if (source == null) {
					source = getDMEOEntity().getAttributeNamedIgnoreCase(a.getName());
				}
				if (source != null && (source.getType() == null || source.getType().equals(a.getType()))) {
					DMEOJoin join = new DMEOJoin(this);
					addToDMEOJoins(join);
					try {
						join.setSourceAttribute(source);
					} catch (EOAccessException e) {
						e.printStackTrace();
					} catch (InvalidJoinException e) {
						// should not append
						e.printStackTrace();
					}
					try {
						join.setDestinationAttribute(a);
					} catch (EOAccessException e) {
						e.printStackTrace();
					} catch (InvalidJoinException e) {
						// should not append
						e.printStackTrace();
					}
				}
			}
		} else {
			for (DMEOAttribute a : getDMEOEntity().getPrimaryKeyAttributes()) {
				DMEOAttribute dest = destinationEntity.getAttributeNamed(a.getName());
				if (dest == null) {
					dest = destinationEntity.getAttributeNamedIgnoreCase(a.getName());
				}
				if (dest != null && (dest.getType() == null || dest.getType().equals(a.getType()))) {
					DMEOJoin join = new DMEOJoin(this);
					addToDMEOJoins(join);
					try {
						join.setSourceAttribute(a);
					} catch (EOAccessException e) {
						e.printStackTrace();
					} catch (InvalidJoinException e) {
						// should not append
						e.printStackTrace();
					}
					try {
						join.setDestinationAttribute(dest);
					} catch (EOAccessException e) {
						e.printStackTrace();
					} catch (InvalidJoinException e) {
						// should not append
						e.printStackTrace();
					}
				}
			}
		}
		return throwException;
	}

	public DMEOAttribute getPrimarySourceAttribute() {
		if (getEORelationship() != null) {
			List<EOAttribute> sourceAttributes = getEORelationship().getSourceAttributes();
			if (sourceAttributes.size() >= 1) {
				EOAttribute sourceAttribute = sourceAttributes.get(0);
				return getDMEOEntity().getAttribute(sourceAttribute);
			}
		}
		return null;
	}

	public DMEOAttribute getPrimaryDestinationAttribute() {
		if (getEORelationship() != null) {
			List<EOAttribute> destinationAttributes = getEORelationship().getDestinationAttributes();
			if (destinationAttributes.size() >= 1) {
				EOAttribute destinationAttribute = destinationAttributes.get(0);
				if (getDestinationEntity() != null) {
					return getDestinationEntity().getAttribute(destinationAttribute);
				}
			}
		}
		return null;
	}

	public DMEOJoin createNewJoin() {
		DMEOJoin newJoin = new DMEOJoin(this);
		addToDMEOJoins(newJoin);
		return newJoin;
	}

	public void deleteJoin(DMEOJoin join) {
		join.delete();
	}

	public boolean isJoinDeletable(DMEOJoin join) {
		return true;
	}

	@Override
	public DMPropertyImplementationType getImplementationType() {
		return DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class DMEORelationshipMustReferToAValidEORelationship extends
			ValidationRule<DMEORelationshipMustReferToAValidEORelationship, DMEORelationship> {
		public DMEORelationshipMustReferToAValidEORelationship() {
			super(DMEORelationship.class, "relationship_must_refer_to_a_valid_eo_relationship");
		}

		@Override
		public ValidationIssue<DMEORelationshipMustReferToAValidEORelationship, DMEORelationship> applyValidation(
				final DMEORelationship object) {
			if (object.getEORelationship() == null) {
				ValidationError<DMEORelationshipMustReferToAValidEORelationship, DMEORelationship> error = new ValidationError<DMEORelationshipMustReferToAValidEORelationship, DMEORelationship>(
						this, object, "relationship_($object.name)_must_refer_to_a_valid_eo_relationship");
				return error;
			}
			return null;
		}

	}

	public static class DMEORelationshipToManyIsRarellyMandatory extends
			ValidationRule<DMEORelationshipToManyIsRarellyMandatory, DMEORelationship> {
		public DMEORelationshipToManyIsRarellyMandatory() {
			super(DMEORelationship.class, "to_many_relation_ship_is_rarelly_mandatory");
		}

		@Override
		public ValidationIssue<DMEORelationshipToManyIsRarellyMandatory, DMEORelationship> applyValidation(final DMEORelationship object) {
			if (object.getIsToMany() && object.getEORelationship() != null && object.getEORelationship().getIsMandatory()) {
				ValidationWarning<DMEORelationshipToManyIsRarellyMandatory, DMEORelationship> error = new ValidationWarning(this, object,
						"relationship_($object.name)_is_tomany_and_mandatory");
				error.addToFixProposals(new SetRelationNotMandatory());

				return error;
			}
			return null;
		}

	}

	public static class SetRelationNotMandatory extends FixProposal<DMEORelationshipToManyIsRarellyMandatory, DMEORelationship> {

		public SetRelationNotMandatory() {
			super("make_relationship_($object.name)_not_mandatory");
		}

		@Override
		protected void fixAction() {
			getObject().getEORelationship().setIsMandatory(false);
		}
	}

	public static class ForeignKeyTypeMustMatchPrimaryKey extends ValidationRule<ForeignKeyTypeMustMatchPrimaryKey, DMEORelationship> {

		public ForeignKeyTypeMustMatchPrimaryKey() {
			super(DMEORelationship.class, "foreign_key_type_must_match_primary_key_type");
		}

		@Override
		public ValidationIssue<ForeignKeyTypeMustMatchPrimaryKey, DMEORelationship> applyValidation(DMEORelationship rel) {
			Iterator<DMEOJoin> i = rel.getDMEOJoins().iterator();
			while (i.hasNext()) {
				DMEOJoin j = i.next();
				if (j.getSourceAttribute() != null && j.getDestinationAttribute() != null) {
					if (j.getSourceAttribute().getPrototype() != j.getDestinationAttribute().getPrototype()) {
						Vector<FixProposal<ForeignKeyTypeMustMatchPrimaryKey, DMEORelationship>> v = new Vector<FixProposal<ForeignKeyTypeMustMatchPrimaryKey, DMEORelationship>>();
						if (rel.getIsToMany()) {
							v.add(new SetPrototype(j.getDestinationAttribute(), j.getSourceAttribute().getPrototype()));
							v.add(new SetPrototype(j.getSourceAttribute(), j.getDestinationAttribute().getPrototype()));
							return new ValidationError(
									this,
									j,
									"foreign_key_type_($object.sourceAttribute.prototype.name)_must_match_primary_key_type_($object.destinationAttribute.prototype.name)",
									v);
						} else {
							v.add(new SetPrototype(j.getSourceAttribute(), j.getDestinationAttribute().getPrototype()));
							v.add(new SetPrototype(j.getDestinationAttribute(), j.getDestinationAttribute().getPrototype()));
							return new ValidationError(this, j,
									"foreign_key_type_($object.destinationAttribute.prototype.name)_must_match_primary_key_type_($object.sourceAttribute.prototype.name)");
						}
					}
				}

			}
			return null;
		}

		public static class SetPrototype extends FixProposal<ForeignKeyTypeMustMatchPrimaryKey, DMEORelationship> {
			private DMEOAttribute source;
			private DMEOPrototype prototype;

			public SetPrototype(DMEOAttribute att, DMEOPrototype prototype) {
				super("set_($object.source.name)_to_prototype_($object.prototype.name)");
				this.source = att;
				this.prototype = prototype;
			}

			@Override
			protected void fixAction() {
				source.setPrototype(prototype);
			}

			public DMEOAttribute getSource() {
				return source;
			}

			public DMEOPrototype getPrototype() {
				return prototype;
			}
		}

		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType != CodeType.PROTOTYPE;
		}
	}

	/*
	 * private boolean _isJoinCurrentlyEdited = false; private DMEOEntity
	 * _editedDestinationEntity = null; private DMEOAttribute
	 * _editedSourceAttribute = null; private DMEOAttribute
	 * _editedDestinationAttribute = null;
	 * 
	 * public DMEOEntity getDestinationEntity() { if (_isJoinCurrentlyEdited) {
	 * return _editedDestinationEntity; } if (getEORelationship() != null) {
	 * EOEntity destinationEOEntity = getEORelationship().destinationEntity();
	 * if (destinationEOEntity != null) { DMEOEntity destinationEntity =
	 * getDMModel().getDMEOEntity(destinationEOEntity); if (destinationEntity ==
	 * null) { if (logger.isLoggable(Level.WARNING)) logger.warning("Could not
	 * find DMEOEntity named "+destinationEOEntity.name()); } else { return
	 * destinationEntity; } } } return null; }
	 * 
	 * public DMEOAttribute getSourceAttribute() { if (_isJoinCurrentlyEdited) {
	 * return _editedSourceAttribute; } if (getEORelationship() != null) {
	 * NSArray sourceAttributes = getEORelationship().sourceAttributes(); if
	 * (sourceAttributes.count() > 1) { if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Could not handle multi-join for relationship
	 * "+getEORelationship().name()); } else if (sourceAttributes.count() == 1) {
	 * EOAttribute sourceAttribute =
	 * (EOAttribute)sourceAttributes.objectAtIndex(0); return
	 * getDMEOEntity().getAttribute(sourceAttribute); } } return null; }
	 * 
	 * public DMEOAttribute getDestinationAttribute() { if
	 * (_isJoinCurrentlyEdited) { return _editedDestinationAttribute; } if
	 * (getEORelationship() != null) { NSArray destinationAttributes =
	 * getEORelationship().destinationAttributes(); if
	 * (destinationAttributes.count() > 1) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Could not handle
	 * multi-join for relationship "+getEORelationship().name()); } else if
	 * (destinationAttributes.count() == 1) { EOAttribute destinationAttribute =
	 * (EOAttribute)destinationAttributes.objectAtIndex(0); if
	 * (getDestinationEntity() != null) { return
	 * getDestinationEntity().getAttribute(destinationAttribute); } } } return
	 * null; }
	 * 
	 * private void resetJoin() { if (logger.isLoggable(Level.INFO)) logger.info
	 * ("Reset EOJoin"); _editedDestinationEntity = getDestinationEntity();
	 * _editedDestinationAttribute = getDestinationAttribute();
	 * _editedSourceAttribute = getSourceAttribute(); if (getEORelationship() !=
	 * null) { for (Enumeration en = new
	 * NSArray(getEORelationship().joins()).objectEnumerator();
	 * en.hasMoreElements();) { EOJoin join = (EOJoin)en.nextElement();
	 * getEORelationship().removeJoin(join); } } _isJoinCurrentlyEdited = true; }
	 * 
	 * private void tryToCreateJoin() { if ((_isJoinCurrentlyEdited) &&
	 * ((getEORelationship() != null)) && (_editedDestinationEntity != null) &&
	 * (_editedSourceAttribute != null) && (_editedDestinationAttribute != null) &&
	 * (_editedDestinationAttribute.getDMEOEntity() == _editedDestinationEntity) &&
	 * (_editedSourceAttribute.getEOAttribute() != null) &&
	 * (_editedDestinationAttribute.getEOAttribute() != null)) { if
	 * (logger.isLoggable(Level.INFO)) logger.info ("Make new EOJoin"); EOJoin
	 * join = new
	 * EOJoin(_editedSourceAttribute.getEOAttribute(),_editedDestinationAttribute.getEOAttribute());
	 * getEORelationship().addJoin(join); _isJoinCurrentlyEdited = false; //
	 * TODO support a faire pour DMEntity.getPropertiesWithThisType throw new
	 * RelationshipJoinSuccessfullyUpdated(this); } }
	 * 
	 * public void setDestinationEntity (DMEOEntity destinationEntity) {
	 * DMEOEntity oldDestinationEntity = getDestinationEntity(); if
	 * (destinationEntity != oldDestinationEntity) { if
	 * (!_isJoinCurrentlyEdited) { resetJoin(); } _editedDestinationEntity =
	 * destinationEntity; setDestinationAttribute(null); tryToCreateJoin();
	 * setChanged(); notifyObservers(new
	 * DMAttributeDataModification("destinationEntity",oldDestinationEntity,destinationEntity)); } }
	 * 
	 * public void setSourceAttribute (DMEOAttribute sourceAttribute) {
	 * DMEOAttribute oldSourceAttribute = getSourceAttribute(); if
	 * (sourceAttribute != oldSourceAttribute) { if (!_isJoinCurrentlyEdited) {
	 * resetJoin(); } _editedSourceAttribute = sourceAttribute;
	 * tryToCreateJoin(); setChanged(); notifyObservers(new
	 * DMAttributeDataModification("sourceAttribute",oldSourceAttribute,sourceAttribute)); } }
	 * 
	 * public void setDestinationAttribute (DMEOAttribute destinationAttribute) {
	 * DMEOAttribute oldDestinationAttribute = getDestinationAttribute(); if
	 * (destinationAttribute != oldDestinationAttribute) { if
	 * (!_isJoinCurrentlyEdited) { resetJoin(); } _editedDestinationAttribute =
	 * destinationAttribute; tryToCreateJoin(); setChanged();
	 * notifyObservers(new
	 * DMAttributeDataModification("destinationAttribute",oldDestinationAttribute,destinationAttribute)); } }
	 */

}