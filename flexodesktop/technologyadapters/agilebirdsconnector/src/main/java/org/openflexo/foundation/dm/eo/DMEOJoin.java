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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.dm.eo.model.EOJoin;
import org.openflexo.foundation.dm.eo.model.EORelationship;
import org.openflexo.foundation.dm.eo.model.InvalidJoinException;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOJoin extends DMObject implements DMEOObject, InspectableObject {

	private static final Logger logger = Logger.getLogger(DMEOJoin.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================
	private DMEORelationship _dmEORelationship;
	private DMEOEntity _sourceEntity;
	private DMEOEntity _destinationEntity;

	private EOJoin _eoJoin;

	/**
	 * Default constructor
	 */
	public DMEOJoin(DMEORelationship dmEORelationship) {
		super(dmEORelationship.getDMModel());
		_dmEORelationship = dmEORelationship;
		_eoJoin = null;
	}

	/**
	 * Default constructor
	 */
	public DMEOJoin(DMEOEntity sourceEntity) {
		super(sourceEntity.getDMModel());
		_sourceEntity = sourceEntity;
		_eoJoin = null;
	}

	/**
	 * Default constructor
	 */
	public DMEOJoin(DMEORelationship dmEORelationship, EOJoin eoJoin) {
		this(dmEORelationship);
		_eoJoin = eoJoin;
	}

	public DMEORelationship getDMEORelationship() {
		return _dmEORelationship;
	}

	public void setDMEORelationship(DMEORelationship relationship) throws EOAccessException, InvalidJoinException {
		_dmEORelationship = relationship;
		try {
			tryToCreateJoin();
		} catch (InvalidJoinException e) {
			_dmEORelationship = null;
			throw e;
		}
	}

	@Override
	public String getName() {
		return FlexoLocalization.localizedForKey(getClassNameKey());
	}

	@Override
	public void setName(String aName) {
		// not relevant
	}

	/**
	 * Overrides isNameValid
	 * 
	 * @see org.openflexo.foundation.dm.DMObject#isNameValid()
	 */
	@Override
	public boolean isNameValid() {
		return true; // joins don't have name
	}

	/**
	 * Return a Vector of embedded DMObjects at this level.
	 * 
	 * @return null
	 */
	@Override
	public Vector getEmbeddedDMObjects() {
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getDMEORelationship() == null) {
			return "UNDEFINED_JOIN/" + Integer.toHexString(hashCode());
		}
		return getDMEORelationship().getFullyQualifiedName() + ".JOIN";
	}

	@Override
	public DMEOModel getDMEOModel() {
		if (getDMEORelationship() == null) {
			return null;
		}
		return getDMEORelationship().getDMEOModel();
	}

	@Override
	public String getInspectorName() {
		// Never inspected by its own
		return null;
	}

	public EORelationship getEORelationship() {
		return getDMEORelationship().getEORelationship();
	}

	private DMEOAttribute _sourceAttribute;

	private DMEOAttribute _destinationAttribute;

	public DMEOAttribute getSourceAttribute() {
		if (_eoJoin != null) {
			EOAttribute source = _eoJoin.getSourceAttribute();
			_sourceAttribute = getDMEORelationship().getDMEOEntity().getDMEOAttribute(source);
		}
		return _sourceAttribute;
	}

	public DMEOAttribute getDestinationAttribute() {
		if (_eoJoin != null) {
			EOAttribute destination = _eoJoin.getDestinationAttribute();
			if (destination != null) {
				_destinationAttribute = getDestinationEntity().getDMEOAttribute(destination);
			}
		}
		return _destinationAttribute;
	}

	public boolean isJoinValid() {
		if (getDMEORelationship() == null) {
			return getSourceAttribute() != null && getDestinationAttribute() != null;
		}
		return _eoJoin != null;
	}

	public void setSourceAttribute(DMEOAttribute sourceAttribute) throws EOAccessException, InvalidJoinException {
		if (sourceAttribute != getSourceAttribute()) {
			DMEOAttribute oldSourceAttribute = _sourceAttribute;
			_sourceAttribute = sourceAttribute;
			if (_eoJoin != null) {
				deleteEOJoin();
			}
			tryToCreateJoin();
			notifyObservers(new DMAttributeDataModification("sourceAttribute", oldSourceAttribute, sourceAttribute));
		}
	}

	public void setDestinationAttribute(DMEOAttribute destinationAttribute) throws EOAccessException, InvalidJoinException {
		if (destinationAttribute != getDestinationAttribute()) {
			DMEOAttribute oldDestinationAttribute = _destinationAttribute;
			_destinationAttribute = destinationAttribute;
			if (_eoJoin != null) {
				deleteEOJoin();
			}
			tryToCreateJoin();
			notifyObservers(new DMAttributeDataModification("destinationAttribute", oldDestinationAttribute, destinationAttribute));
		}
	}

	private void deleteEOJoin() {
		if (_eoJoin != null) {
			getDMEORelationship().getEORelationship().removeJoin(_eoJoin);
			_eoJoin = null;
		}
	}

	private void tryToCreateJoin() throws EOAccessException, InvalidJoinException {
		if (getDMEORelationship() == null) {
			return;
		}
		if (getEORelationship() != null && getDestinationEntity() != null && _sourceAttribute != null && _destinationAttribute != null
				&& _destinationAttribute.getDMEOEntity() == getDestinationEntity() && _sourceAttribute.getEOAttribute() != null
				&& _destinationAttribute.getEOAttribute() != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Make new EOJoin");
			}
			try {
				_eoJoin = new EOJoin(getEORelationship(), _sourceAttribute.getEOAttribute(), _destinationAttribute.getEOAttribute());
				getEORelationship().addJoin(_eoJoin);
				// for (EOJoin j : getEORelationship().getJoins()) logger.info("Join: "+j);
				// TODO support a faire pour DMEntity.getPropertiesWithThisType

			} catch (IllegalArgumentException e) {
				throw new EOAccessException(e);
			} catch (IllegalStateException e) {
				throw new EOAccessException(e);
			}
			setChanged();
		}

	}

	@Override
	public boolean delete() {
		delete(true);
		return true;
	}

	public void delete(boolean deleteEOJoin) {
		if (deleteEOJoin) {
			deleteEOJoin();
		}
		getDMEORelationship().removeFromDMEOJoins(this);
	}

	@Override
	public boolean isDeletable() {
		if (_dmEORelationship != null) {
			return _dmEORelationship.isDeletable();
		}
		return true;
	}

	// ==========================================================================
	// ======================== TreeNode implementation
	// =========================
	// ==========================================================================

	@Override
	public Vector<DMObject> getOrderedChildren() {
		return EmptyVector.EMPTY_VECTOR(DMObject.class);
	}

	@Override
	public DMObject getParent() {
		return getDMEORelationship();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "join";
	}

	public static class JoinMustBeValid extends ValidationRule {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public JoinMustBeValid() {
			super(DMEOJoin.class, "join_must_be_valid");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue applyValidation(Validable object) {
			if (!((DMEOJoin) object).isJoinValid()) {
				ValidationError err = new ValidationError(this, object, "join_is_not_valid_for_$object");
				err.addToFixProposals(new DeleteJoin());
				return err;
			}
			return null;
		}

		public static class DeleteJoin extends FixProposal {

			/**
			 * @param aMessage
			 */
			public DeleteJoin() {
				super("remove_join");
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				((DMEOJoin) getObject()).delete();
			}

		}
	}

	public DMEOEntity getDestinationEntity() {
		if (getDMEORelationship() != null) {
			return getDMEORelationship().getDestinationEntity();
		}
		return _destinationEntity;
	}

	public DMEOEntity getSourceEntity() {
		if (getDMEORelationship() != null) {
			return getDMEORelationship().getDMEOEntity();
		}
		return _sourceEntity;
	}

	// Relevant only if relationship is not set
	public void setDestinationEntity(DMEOEntity destinationEntity) {
		_destinationEntity = destinationEntity;
	}

	// Relevant only if relationship is not set
	public void setSourceEntity(DMEOEntity sourceEntity) {
		_sourceEntity = sourceEntity;
	}
}
