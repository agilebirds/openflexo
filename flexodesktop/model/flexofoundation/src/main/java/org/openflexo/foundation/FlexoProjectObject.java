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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Super class for any object involved in Openflexo and beeing part of a {@link FlexoProject}<br>
 * Provides a direct access to {@link FlexoServiceManager} (and all services) through {@link FlexoProject}<br>
 * Also provides support for storing references to {@link EditionPatternInstance}
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoProjectObject extends FlexoObject implements XMLSerializable, ReferenceOwner {

	private static final Logger logger = Logger.getLogger(FlexoProjectObject.class.getPackage().getName());

	private FlexoProject project;

	/**
	 * This list contains all EPI's by reference
	 */
	private List<FlexoModelObjectReference<EditionPatternInstance>> editionPatternReferences;

	public FlexoProjectObject() {
		super();
		editionPatternReferences = new ArrayList<FlexoModelObjectReference<EditionPatternInstance>>();
	}

	public FlexoProjectObject(FlexoProject project) {
		this();
		this.project = project;
	}

	@Override
	public void delete() {

		for (FlexoModelObjectReference<EditionPatternInstance> ref : new ArrayList<FlexoModelObjectReference<EditionPatternInstance>>(
				editionPatternReferences)) {
			EditionPatternInstance epi = ref.getObject();
			if (epi != null) {
				epi.nullifyPatternActor(epi.getRoleForActor(this));
			}
		}

		editionPatternReferences.clear();
		editionPatternReferences = null;

		super.delete();

	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	public void setProject(FlexoProject project) {
		this.project = project;
	}

	@Override
	public List<DocType> getDocTypes() {
		if (getProject() != null) {
			return getProject().getDocTypes();
		}
		return super.getDocTypes();
	}

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

	public List<FlexoModelObjectReference<EditionPatternInstance>> getEditionPatternReferences() {
		return editionPatternReferences;
	}

	public void setEditionPatternReferences(List<FlexoModelObjectReference<EditionPatternInstance>> editionPatternReferences) {
		this.editionPatternReferences = editionPatternReferences;
	}

	public void addToEditionPatternReferences(final FlexoModelObjectReference<EditionPatternInstance> ref) {
		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ref.getObject();
				} catch (ClassCastException e) {
					System.out.println("Je tiens le coupable !!!");
				}
			}
		});*/
		//logger.info("****************** addToEditionPatternReferences() with " + ref);
		ref.setOwner(this);
		editionPatternReferences.add(ref);
		setChanged();
		notifyObservers(new DataModification("editionPatternReferences", null, ref));
	}

	public void removeFromEditionPatternReferences(FlexoModelObjectReference<EditionPatternInstance> ref) {
		ref.setOwner(null);
		editionPatternReferences.remove(ref);
		setChanged();
		notifyObservers(new DataModification("editionPatternReferences", ref, null));
	}

	public EditionPatternInstance getEditionPatternReference(String editionPatternId, long instanceId) {
		if (editionPatternId == null) {
			return null;
		}
		if (editionPatternReferences == null) {
			return null;
		}
		for (FlexoModelObjectReference<EditionPatternInstance> r : editionPatternReferences) {
			EditionPatternInstance epi = r.getObject();
			if (epi.getEditionPattern().getName().equals(editionPatternId) && epi.getFlexoID() == instanceId) {
				return epi;
			}
		}
		return null;
	}

	/**
	 * Return EditionPatternInstance matching supplied id represented as a string, which could be either the name of EditionPattern, or its
	 * URI<br>
	 * If many EditionPatternInstance are declared in this FlexoProjectObject, return first one
	 * 
	 * @param editionPatternId
	 * @return
	 */
	public EditionPatternInstance getEditionPatternInstance(String editionPatternId) {
		if (editionPatternId == null) {
			return null;
		}
		for (FlexoModelObjectReference<EditionPatternInstance> ref : editionPatternReferences) {
			EditionPatternInstance epi = ref.getObject();
			if (epi.getEditionPattern().getName().equals(editionPatternId)) {
				return epi;
			}
			if (epi.getEditionPattern().getURI().equals(editionPatternId)) {
				return epi;
			}
		}
		return null;
	}

	/**
	 * Return EditionPatternInstance matching supplied EditionPattern<br>
	 * If many EditionPatternInstance are declared in this FlexoProjectObject, return first one
	 * 
	 * @param editionPatternId
	 * @return
	 */
	public EditionPatternInstance getEditionPatternInstance(EditionPattern editionPattern) {
		if (editionPattern == null) {
			return null;
		}
		for (FlexoModelObjectReference<EditionPatternInstance> ref : editionPatternReferences) {
			EditionPatternInstance epi = ref.getObject();
			if (epi != null && epi.getEditionPattern() == editionPattern) {
				return epi;
			}
		}
		return null;
	}

	protected FlexoModelObjectReference<EditionPatternInstance> getEditionPatternReference(EditionPatternInstance editionPatternInstance) {
		for (FlexoModelObjectReference<EditionPatternInstance> ref : editionPatternReferences) {
			String was = ref.toString() + " serialized as " + ref.getStringRepresentation();
			try {
				EditionPatternInstance epi = ref.getObject();
				if (epi == editionPatternInstance) {
					return ref;
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
				System.out.println("OK, j'ai le soucis, was=" + was);
			}
		}
		return null;
	}

	public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance) {

		FlexoModelObjectReference<EditionPatternInstance> existingReference = getEditionPatternReference(editionPatternInstance);

		if (existingReference == null) {
			addToEditionPatternReferences(new FlexoModelObjectReference<EditionPatternInstance>(editionPatternInstance, this));
		}
	}

	public void unregisterEditionPatternReference(EditionPatternInstance editionPatternInstance) {
		FlexoModelObjectReference<EditionPatternInstance> referenceToRemove = getEditionPatternReference(editionPatternInstance);
		if (referenceToRemove == null) {
			logger.warning("Called for unregister EditionPatternReference for unexisting reference to edition pattern instance EP="
					+ editionPatternInstance.getEditionPattern().getName() + " id=" + editionPatternInstance.getFlexoID());
		} else {
			removeFromEditionPatternReferences(referenceToRemove);
		}
	}

	/**
	 * Return true is this object is somewhere involved as a primary representation pattern role in any of its EditionPatternReferences
	 * 
	 * @return
	 */
	@Deprecated
	public boolean providesSupportAsPrimaryRole() {
		if (getEditionPatternReferences() != null) {
			if (getEditionPatternReferences().size() > 0) {
				for (FlexoModelObjectReference<EditionPatternInstance> ref : editionPatternReferences) {
					EditionPatternInstance epi = ref.getObject();
					if (epi != null) {
						PatternRole<?> pr = epi.getRoleForActor(this);
						if (pr == null) {
							logger.warning("Found an EditionPatternReference with a null pattern role. Please investigate...");
						} else if (pr.getIsPrimaryRole()) {
							return true;
						}
					} else {
						logger.warning("Cannot find EditionPatternInstance for " + ref);
					}
				}
			}
		}
		return false;
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
		logger.warning("TODO: implement this");
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {
		logger.warning("TODO: implement this");
	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference<?> reference) {
		setChanged();
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		logger.warning("TODO: implement this");
	}

}
