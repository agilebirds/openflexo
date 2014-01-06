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
package org.openflexo.foundation.ws;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class ExternalWSFolder extends WSFolder {

	private static final Logger logger = FlexoLogger.getLogger(ExternalWSFolder.class.getPackage().getName());

	/**
	 * @param dl
	 */
	public ExternalWSFolder(FlexoWSLibrary dl) {
		super(dl);
	}

	public Vector<ExternalWSService> getExternalWSServices() {
		return getWSLibrary().getExternalWSServices();
	}

	@Override
	public Vector<ExternalWSService> getWSServices() {
		return getExternalWSServices();
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "EXTERNAL_WS_FOLDER";
	}

	@Override
	public String getLocalizedDescription() {
		return FlexoLocalization.localizedForKey("ws_external_folder_description");
	}

	@Override
	public String getName() {

		return "ws_external_ws_folder";
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public boolean delete() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("delete: ExternalWSFolder");
		}
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
		return getName();
	}

}