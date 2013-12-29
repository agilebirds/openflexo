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
package org.openflexo.foundation;

import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Super class for any object involved in Openflexo and beeing part of a {@link FlexoProject}<br>
 * Provides a direct access to {@link FlexoServiceManager} (and all services) through {@link FlexoProject}<br>
 * Also provides support for storing references to {@link EditionPatternInstance}
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoProjectObject extends DefaultFlexoObject implements XMLSerializable {

	private static final Logger logger = Logger.getLogger(FlexoProjectObject.class.getPackage().getName());

	private FlexoProject project;

	public FlexoProjectObject() {
		super();
	}

	public FlexoProjectObject(FlexoProject project) {
		this();
		this.project = project;
	}

	@Override
	public boolean delete() {

		return super.delete();

	}

	public FlexoProject getProject() {
		return project;
	}

	public void setProject(FlexoProject project) {
		this.project = project;
	}

	/*@Override
	public List<DocType> getDocTypes() {
		if (getProject() != null) {
			return getProject().getDocTypes();
		}
		return super.getDocTypes();
	}*/

	// TODO: Should be refactored with injectors
	@Override
	public FlexoServiceManager getServiceManager() {
		if (getProject() != null) {
			return getProject().getServiceManager();
		}
		return null;
	}

	public InformationSpace getInformationSpace() {
		if (getServiceManager() != null) {
			return getServiceManager().getInformationSpace();
		}
		return null;
	}

	/**
	 * Return true is this object is somewhere involved as a primary representation pattern role in any of its EditionPatternReferences
	 * 
	 * @return
	 */
	/*@Deprecated
	public boolean providesSupportAsPrimaryRole() {
		if (getEditionPatternReferences() != null) {
			if (getEditionPatternReferences().size() > 0) {
				for (FlexoModelObjectReference<EditionPatternInstance> ref : editionPatternReferences) {
					EditionPatternInstance epi = ref.getObject();
					if (epi != null) {
						PatternRole<?> pr = epi.getRoleForActor(this);
						if (pr == null) {
							logger.warning("Found an EditionPatternReference with a null pattern role. Please investigate...");
						} //else if (pr.getIsPrimaryRole()) {
							// return true;
							//}
					} else {
						logger.warning("Cannot find EditionPatternInstance for " + ref);
					}
				}
			}
		}
		return false;
	}*/

}
