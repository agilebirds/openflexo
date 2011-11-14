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
package org.openflexo.foundation.sg.implmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ImplementationModelResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.sg.implmodel.enums.TechnologyLayer;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.xmlcode.XMLMapping;

public class ImplementationModel extends ImplModelObject implements XMLStorageResourceData {

	private static final Logger logger = Logger.getLogger(ImplementationModel.class.getPackage().getName());

	private FlexoProject _project;
	private ImplementationModelResource _resource;
	private ImplementationModelDefinition _implModelDefinition;
	private LinkedHashMap<String, TechnologyModuleImplementation> technologyModules = new LinkedHashMap<String, TechnologyModuleImplementation>(); // <Module
																																					// definition
																																					// name,
																																					// implementation>

	private boolean isAddingModule = false;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public ImplementationModel(ImplementationModelBuilder builder) {
		this(builder.definition, builder.getProject());
		builder.implementationModel = this;
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public ImplementationModel(ImplementationModelDefinition implModelDefinition, FlexoProject project) {
		super(project);
		logger.info("Created new implementation model for project " + project);
		_project = project;
		_implModelDefinition = implModelDefinition;
		setImplementationModel(this);
	}

	public ImplementationModelDefinition getImplementationModelDefinition() {
		return _implModelDefinition;
	}

	@Override
	public ImplementationModelResource getFlexoResource() {
		return _resource;
	}

	@Override
	public ImplementationModelResource getFlexoXMLFileResource() {
		return getFlexoResource();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		_resource = (ImplementationModelResource) resource;
	}

	@Override
	public void save() throws SaveResourceException {
		getFlexoResource().saveResourceData();
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	@Override
	public String getClassNameKey() {
		return "implementation_model";
	}

	@Override
	public String getName() {
		if (getImplementationModelDefinition() != null) {
			return getImplementationModelDefinition().getName();
		}
		return null;
	}

	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		if (getImplementationModelDefinition() != null) {
			getImplementationModelDefinition().setName(name);
		}
	}

	@Override
	public String getDescription() {
		if (isSerializing()) {
			return null;
		}
		if (getImplementationModelDefinition() != null) {
			return getImplementationModelDefinition().getDescription();
		}
		return super.getDescription();
	}

	@Override
	public void setDescription(String description) {
		if (getImplementationModelDefinition() != null) {
			getImplementationModelDefinition().setDescription(description);
		}
		super.setDescription(description);
	}

	@Override
	public Hashtable<String, String> getSpecificDescriptions() {
		if (isSerializing()) {
			return null;
		}
		if (getImplementationModelDefinition() != null) {
			return getImplementationModelDefinition().getSpecificDescriptions();
		}
		return super.getSpecificDescriptions();
	}

	@Override
	public boolean getHasSpecificDescriptions() {
		if (getImplementationModelDefinition() != null) {
			return getImplementationModelDefinition().getHasSpecificDescriptions();
		}
		return super.getHasSpecificDescriptions();
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + "." + getImplementationModelDefinition().getName();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getImplementationModelMapping();
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	/**
	 * Add the specified implementation and all its required module in the implementation model. <br>
	 * If there is incompatibility with existing module, an TechnologyModuleCompatibilityCheckException is thrown and no module is added.
	 * 
	 * @param technologyModuleImplementation
	 * @throws TechnologyModuleCompatibilityCheckException
	 */
	@SuppressWarnings("unchecked")
	public void addToTechnologyModules(TechnologyModuleImplementation technologyModuleImplementation)
			throws TechnologyModuleCompatibilityCheckException {

		if (containsTechnologyModule(technologyModuleImplementation.getTechnologyModuleDefinition())) {
			return;
		}

		// Clone the map to restore it in case of compatibility issue
		LinkedHashMap<String, TechnologyModuleImplementation> clone = (LinkedHashMap<String, TechnologyModuleImplementation>) technologyModules
				.clone();

		boolean currentIsAddingModule = isAddingModule;
		isAddingModule = true;
		try {
			// Add the required modules (addToTechnologyModules will be called at implementation creation)
			for (TechnologyModuleDefinition requiredModule : technologyModuleImplementation.getTechnologyModuleDefinition()
					.getRequiredModules()) {
				try {
					requiredModule.createNewImplementation(this);
				} catch (TechnologyModuleCompatibilityCheckException e) {
					e.prependMessage("Module '" + requiredModule.getName() + "' is required by module '"
							+ technologyModuleImplementation.getTechnologyModuleDefinition().getName() + "'");
					throw e;
				}
			}

			// Add specified module
			technologyModules.put(technologyModuleImplementation.getTechnologyModuleDefinition().getName(), technologyModuleImplementation);

			// Checks compatibility
			checkTechnologyModuleCompatibility();
		} catch (TechnologyModuleCompatibilityCheckException e) {
			technologyModules = clone;
			throw e;
		} finally {
			isAddingModule = currentIsAddingModule;
		}

		if (!isAddingModule) {
			setChanged();
			notifyObservers(new SGObjectAddedToListModification("technologyModules", technologyModuleImplementation));
		}
	}

	public void removeFromTechnologyModules(TechnologyModuleImplementation technologyModuleImplementation) {
		if (technologyModules.remove(technologyModuleImplementation.getTechnologyModuleDefinition().getName()) != null) {

			// Remove also all dependent modules
			for (TechnologyModuleImplementation moduleImplementation : new ArrayList<TechnologyModuleImplementation>(
					technologyModules.values())) {
				if (moduleImplementation.getTechnologyModuleDefinition().getAllRequiredModules()
						.contains(technologyModuleImplementation.getTechnologyModuleDefinition())) {
					removeFromTechnologyModules(moduleImplementation);
				}
			}

			setChanged();
			notifyObservers(new SGObjectRemovedFromListModification("technologyModules", technologyModuleImplementation));
		}
	}

	/**
	 * Perform all necessary checks to ensure compatibility between available modules.
	 * 
	 * @throws TechnologyModuleCompatibilityCheckException
	 *             if the check fails
	 */
	public void checkTechnologyModuleCompatibility() throws TechnologyModuleCompatibilityCheckException {

		if (isDeserializing()) {
			return; // No check while deserializing
		}

		// 1. Build a map of TechnologyModuleDefinition by layers.
		Map<TechnologyLayer, Set<TechnologyModuleDefinition>> layerModuleMap = new HashMap<TechnologyLayer, Set<TechnologyModuleDefinition>>();
		for (TechnologyModuleImplementation technologyModuleImplementation : getTechnologyModules()) {
			Set<TechnologyModuleDefinition> set = layerModuleMap.get(technologyModuleImplementation.getTechnologyModuleDefinition()
					.getTechnologyLayer());
			if (set == null) {
				set = new HashSet<TechnologyModuleDefinition>();
				layerModuleMap.put(technologyModuleImplementation.getTechnologyModuleDefinition().getTechnologyLayer(), set);
			}

			set.add(technologyModuleImplementation.getTechnologyModuleDefinition());
		}

		// 2. Checks all technology implementation used.
		for (TechnologyModuleImplementation implementation : technologyModules.values()) {
			TechnologyModuleDefinition moduleDefinition = implementation.getTechnologyModuleDefinition();
			Set<TechnologyModuleDefinition> possibleIncompatibleModules = moduleDefinition.getIncompatibleModules();

			if (moduleDefinition.getTechnologyLayer() != TechnologyLayer.MAIN
					&& moduleDefinition.getTechnologyLayer() != TechnologyLayer.TRANSVERSAL) {
				possibleIncompatibleModules.addAll(layerModuleMap.get(moduleDefinition.getTechnologyLayer()));
			}

			for (TechnologyModuleDefinition incompatibleModule : possibleIncompatibleModules) {
				if (incompatibleModule != moduleDefinition && !incompatibleModule.getCompatibleModules().contains(moduleDefinition)
						&& !incompatibleModule.getRequiredModules().contains(moduleDefinition)
						&& !moduleDefinition.getCompatibleModules().contains(incompatibleModule)
						&& !moduleDefinition.getRequiredModules().contains(incompatibleModule)) {
					throw new TechnologyModuleCompatibilityCheckException(moduleDefinition, incompatibleModule);
				}
			}
		}
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	public Vector<TechnologyModuleImplementation> getTechnologyModules() {
		return new Vector<TechnologyModuleImplementation>(technologyModules.values());
	}

	public void setTechnologyModules(Vector<TechnologyModuleImplementation> technologyModules) {
		this.technologyModules = new LinkedHashMap<String, TechnologyModuleImplementation>();
		for (TechnologyModuleImplementation implementation : technologyModules) {
			this.technologyModules.put(implementation.getTechnologyModuleDefinition().getName(), implementation);
		}
	}

	public boolean containsTechnologyModule(TechnologyModuleDefinition technologyModuleDefinition) {
		return technologyModules.containsKey(technologyModuleDefinition.getName());
	}

	/**
	 * Retrieve the TechnologyModuleImplementation associated to this technologyModuleDefinition.
	 * 
	 * @param technologyModuleDefinition
	 * @return the retrieved TechnologyModuleImplementation is any, null otherwise.
	 */
	public TechnologyModuleImplementation getTechnologyModule(TechnologyModuleDefinition technologyModuleDefinition) {
		return technologyModules.get(technologyModuleDefinition.getName());
	}

	/**
	 * Retrieve the TechnologyModuleImplementation associated to this technologyModuleDefinitionName.
	 * 
	 * @param technologyModuleDefinitionName
	 * @return the retrieved TechnologyModuleImplementation is any, null otherwise.
	 */
	public TechnologyModuleImplementation getTechnologyModule(String technologyModuleDefinitionName) {
		return technologyModules.get(technologyModuleDefinitionName);
	}

	/**
	 * Retrieve all technology modules available for the specified layer.
	 * 
	 * @param technologyLayer
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getTechnologyModules(TechnologyLayer technologyLayer) {
		List<TechnologyModuleImplementation> result = new Vector<TechnologyModuleImplementation>();
		for (TechnologyModuleImplementation technologyModuleImplementation : getTechnologyModules()) {
			if (technologyModuleImplementation.getTechnologyModuleDefinition().getTechnologyLayer() == technologyLayer) {
				result.add(technologyModuleImplementation);
			}
		}

		return result;
	}

	/**
	 * Retrieve all technology modules available for the GLOBAL layer.
	 * 
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getMainTechnologyModules() {
		return getTechnologyModules(TechnologyLayer.MAIN);
	}

	/**
	 * // * Retrieve all technology modules available for the TRANSVERSAL layer.
	 * 
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getTransversalTechnologyModules() {
		return getTechnologyModules(TechnologyLayer.TRANSVERSAL);
	}

	/**
	 * Retrieve all technology modules available for the GUI layer.
	 * 
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getGUITechnologyModules() {
		return getTechnologyModules(TechnologyLayer.GUI);
	}

	/**
	 * Retrieve all technology modules available for the BUSINESS LOGIC layer.
	 * 
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getBusinessLogicTechnologyModules() {
		return getTechnologyModules(TechnologyLayer.BUSINESS_LOGIC);
	}

	/**
	 * Retrieve all technology modules available for the DAO layer.
	 * 
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getDAOTechnologyModules() {
		return getTechnologyModules(TechnologyLayer.DAO);
	}

	/**
	 * Retrieve all technology modules available for the DATABASE layer.
	 * 
	 * @return the retrieved technology modules.
	 */
	public List<TechnologyModuleImplementation> getDatabaseTechnologyModules() {
		return getTechnologyModules(TechnologyLayer.DATABASE);
	}
}
