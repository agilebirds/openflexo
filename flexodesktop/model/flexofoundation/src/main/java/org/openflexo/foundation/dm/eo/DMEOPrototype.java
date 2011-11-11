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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreeNode;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOPrototype extends DMEOAttribute {

	private static final Logger logger = Logger.getLogger(DMEOPrototype.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 */
	public DMEOPrototype(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMEOPrototype(DMModel dmModel) {
		super(dmModel);
	}

	/**
	 * Default constructor for dynamic creation
	 */
	public DMEOPrototype(DMModel dmModel, EOAttribute eoAttribute) {
		this(dmModel);
		_eoAttribute = eoAttribute;
		if (eoAttribute != null) {
			try {
				setName(eoAttribute.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();// Should not happen
			} catch (InvalidNameException e) {
				e.printStackTrace();// Should not happen
			}
		}
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.DM.DM_RO_EO_PROTOTYPE_INSPECTOR;
	}

	@Override
	public TreeNode getParent() {
		return getDMEORepository();
	}

	@Override
	public DMType getType() {
		if (getEOAttribute() == null)
			return null;

		if (_prototypeType == null || _prototypeType.getBaseEntity() == null
				|| !_prototypeType.getBaseEntity().getFullQualifiedName().equals(getEOAttribute().getClassName())) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Recompute type for DMEOPrototype " + getName());
			_prototypeType = DMType.makeResolvedDMType(getDMModel().getDMEntity(getEOAttribute().getClassName()));
			if (_prototypeType.getBaseEntity() == null) {
				logger.warning("Could not find entity: " + getEOAttribute().getClassName());
			}
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Return cached type for DMEOPrototype " + getName());
		}
		return _prototypeType;
	}

	private DMType _prototypeType = null;
}
