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
package org.openflexo.foundation.dm.eo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class EOJoin extends EOObject {
	private static final Logger logger = FlexoLogger.getLogger(EOJoin.class.getPackage().getName());

	private static final String SOURCE_ATTRIBUTE_KEY = "sourceAttribute";

	private static final String DESTINATION_ATTRIBUTE_KEY = "destinationAttribute";

	private EOAttribute sourceAttribute;

	private EOAttribute destinationAttribute;

	private EORelationship relationship;

	/**
	 * @param m
	 * @return
	 */
	public static EOJoin createJoinFromMap(Map<Object, Object> map, EORelationship relationship) {
		EOJoin join = new EOJoin(relationship);
		join.setOriginalMap(map);
		return join;
	}

	private EOJoin(EORelationship relationship) {
		this.relationship = relationship;
	}

	public EOJoin(EORelationship relationship, EOAttribute source, EOAttribute destination) throws IllegalArgumentException,
			InvalidJoinException {
		this(relationship);
		setOriginalMap(new HashMap<Object, Object>());
		setSourceAttribute(source);
		setDestinationAttribute(destination);
	}

	public EOAttribute getDestinationAttribute() {
		return destinationAttribute;
	}

	public void setDestinationAttribute(EOAttribute destinationAttribute) throws InvalidJoinException {
		if (relationship != null) {
			if (destinationAttribute != null && destinationAttribute.getEntity() != relationship.getDestinationEntity())
				throw new IllegalArgumentException(
						"The destination attribute that has been set is not from the destination entity of the relationship");
			checkJoinValidity(sourceAttribute, destinationAttribute);
		}
		if (this.destinationAttribute != destinationAttribute) {
			if (relationship != null && this.destinationAttribute != null)
				relationship.removeFromDestinationAttributes(this.destinationAttribute);
			this.destinationAttribute = destinationAttribute;
			if (relationship != null && destinationAttribute != null) {
				relationship.addToDestinationAttributes(destinationAttribute);
			}
			if (destinationAttribute != null) {
				getOriginalMap().put(DESTINATION_ATTRIBUTE_KEY, destinationAttribute.getName());
			} else {
				getOriginalMap().remove(DESTINATION_ATTRIBUTE_KEY);
			}
		}
	}

	public EOAttribute getSourceAttribute() {
		return sourceAttribute;
	}

	private void checkJoinValidity(EOAttribute src, EOAttribute dest) throws InvalidJoinException {
		if (src != null && dest != null) {
			String srcExternalType = src.getExternalType();
			String destExternalType = dest.getExternalType();
			if (srcExternalType == null && src.getPrototype() != null)
				srcExternalType = src.getPrototype().getExternalType();
			if (destExternalType == null && dest.getPrototype() != null)
				destExternalType = dest.getPrototype().getExternalType();

			if (srcExternalType != null && destExternalType != null) {
				if (!srcExternalType.equals(destExternalType)) {
					throw new InvalidJoinException(srcExternalType + " cannot be join with " + destExternalType);
				}
			}
		}
	}

	public void setSourceAttribute(EOAttribute sourceAttribute) throws IllegalArgumentException, InvalidJoinException {
		if (relationship != null) {
			if (sourceAttribute != null && sourceAttribute.getEntity() != relationship.getEntity())
				throw new IllegalArgumentException(
						"The source attribute that has been set is not from the source entity of the relationship");
			checkJoinValidity(sourceAttribute, destinationAttribute);
		}
		if (this.sourceAttribute != sourceAttribute) {
			if (relationship != null && this.sourceAttribute != null)
				relationship.removeFromSourceAttributes(this.sourceAttribute);
			this.sourceAttribute = sourceAttribute;
			if (relationship != null && sourceAttribute != null) {
				relationship.addToSourceAttributes(sourceAttribute);
			}
			if (sourceAttribute != null) {
				getOriginalMap().put(SOURCE_ATTRIBUTE_KEY, sourceAttribute.getName());
			} else {
				getOriginalMap().remove(SOURCE_ATTRIBUTE_KEY);
			}
		}
	}

	/**
	 * Overrides resolveObjects
	 * 
	 * @throws InvalidJoinException
	 * @throws IllegalArgumentException
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#resolveObjects()
	 */
	@Override
	protected void resolveObjects() {
		String src = (String) getOriginalMap().get(SOURCE_ATTRIBUTE_KEY);
		if (src != null) {
			EOAttribute att = getRelationship().getEntity().attributeNamed(src);
			if (att != null) {
				try {
					setSourceAttribute(att);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not resolve source attribute " + src + " in entity named "
							+ getRelationship().getEntity().getName());
			}
		}
		String dest = (String) getOriginalMap().get(DESTINATION_ATTRIBUTE_KEY);
		if (dest != null) {
			if (getRelationship().getDestinationEntity() != null) {
				EOAttribute att = getRelationship().getDestinationEntity().attributeNamed(dest);
				if (att != null) {
					try {
						setDestinationAttribute(att);

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not resolve destination attribute " + src + " in entity named "
								+ getRelationship().getDestinationEntity().getName());
				}
			} else if (logger.isLoggable(Level.WARNING))
				logger.warning("Destination attribute is not null but the destination entity cannot be resolved");
		}
	}

	public EORelationship getRelationship() {
		return relationship;
	}

	public void setRelationship(EORelationship relationship) {
		if (getRelationship() != relationship) {
			if (getRelationship() != null) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("This should not happen: trying to change join from relationship");
				if (getSourceAttribute() != null)
					getRelationship().removeFromSourceAttributes(getSourceAttribute());
				if (getDestinationAttribute() != null)
					getRelationship().removeFromDestinationAttributes(getDestinationAttribute());
			}
			this.relationship = relationship;
			if (relationship != null) {
				if (getSourceAttribute() != null)
					if (getSourceAttribute().getEntity() == relationship.getEntity())
						relationship.addToSourceAttributes(getSourceAttribute());
					else
						try {
							setSourceAttribute(null);
						} catch (InvalidJoinException e) {
							// NEVER APPEND because arg is null
						}
				if (getDestinationAttribute() != null)
					if (getDestinationAttribute().getEntity() == relationship.getDestinationEntity())
						relationship.addToDestinationAttributes(getDestinationAttribute());
					else
						try {
							setDestinationAttribute(null);
						} catch (InvalidJoinException e) {
							// NEVER APPEND because arg is null
						}
			}
		}
	}

	/**
     * 
     */
	public void synchronizeObjectWithOriginalMap() {
		Map<Object, Object> map = getOriginalMap();
		if (getSourceAttribute() != null)
			map.put(SOURCE_ATTRIBUTE_KEY, getSourceAttribute().getName());
		else
			map.remove(SOURCE_ATTRIBUTE_KEY);
		if (getDestinationAttribute() != null)
			map.put(DESTINATION_ATTRIBUTE_KEY, getDestinationAttribute().getName());
		else
			map.remove(DESTINATION_ATTRIBUTE_KEY);
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#delete()
	 */
	@Override
	public void delete() {
		try {
			setSourceAttribute(null);
			setDestinationAttribute(null);
		} catch (InvalidJoinException e) {
			// NEVER APPEND because arg is null
		}
		if (relationship != null) {
			relationship.removeJoin(this);
		}
	}

	/**
	 * Overrides clearObjects
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#clearObjects()
	 */
	@Override
	protected void clearObjects() {
		sourceAttribute = null;
		destinationAttribute = null;
	}

	public String getPListRepresentation() {
		return FlexoPropertyListSerialization.getPListRepresentation(getMapRepresentation());
	}

	/**
	 * @return
	 */
	public Map<Object, Object> getMapRepresentation() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (getSourceAttribute() != null)
			map.put(SOURCE_ATTRIBUTE_KEY, getSourceAttribute().getName());
		else
			map.remove(SOURCE_ATTRIBUTE_KEY);
		if (getDestinationAttribute() != null)
			map.put(DESTINATION_ATTRIBUTE_KEY, getDestinationAttribute().getName());
		else
			map.remove(DESTINATION_ATTRIBUTE_KEY);
		return map;
	}

}
