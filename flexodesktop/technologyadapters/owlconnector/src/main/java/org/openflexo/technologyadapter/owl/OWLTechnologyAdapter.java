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
package org.openflexo.technologyadapter.owl;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.owl.model.OWLMetaModelRepository;
import org.openflexo.technologyadapter.owl.model.OWLModelRepository;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResourceImpl;
import org.openflexo.technologyadapter.owl.viewpoint.binding.OWLBindingFactory;

/**
 * This class defines and implements the OWL technology adapter
 * 
 * @author sylvain, luka
 * 
 */
public class OWLTechnologyAdapter extends TechnologyAdapter<OWLOntology, OWLOntology> {

	private static final Logger logger = Logger.getLogger(OWLTechnologyAdapter.class.getPackage().getName());

	private static final OWLBindingFactory BINDING_FACTORY = new OWLBindingFactory();

	public OWLTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "OWL technology adapter";
	}

	@Override
	public OWLModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new OWLModelSlot(viewPoint, this);
	}

	@Override
	public OWLModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		return new OWLModelSlot(virtualModel, this);
	}

	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		// TODO: also check that file is valid
		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".owl");
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		return OWLOntology.findOntologyURI(aMetaModelFile);
	}

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<OWLOntology> metaModelResource,
			TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		// TODO: also check that file is valid and maps a valid XML model conform to supplied meta-model
		// return aModelFile.getName().endsWith(".owl");
		return false;
	}

	@Override
	public boolean isValidModelFile(File aModelFile, TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		return aModelFile.isFile() && aModelFile.getName().endsWith(".owl");
	}

	@Override
	public FlexoResource<OWLOntology> retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {

		// logger.info("Retrieving OWL MetaModelResource for " + aMetaModelFile.getAbsolutePath());

		OWLOntologyLibrary ontologyLibrary = (OWLOntologyLibrary) technologyContextManager;
		OWLOntologyResource ontologyResource = OWLOntologyResourceImpl.retrieveOWLOntologyResource(aMetaModelFile, ontologyLibrary);
		logger.info("Found OWL ontology " + ontologyResource.getURI() + " file:" + aMetaModelFile.getAbsolutePath());
		return ontologyResource;

	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public FlexoResource<OWLOntology> retrieveModelResource(File aModelFile, FlexoResource<OWLOntology> metaModelResource,
			TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		// logger.info("Retrieving OWL ModelResource for " + aModelFile.getAbsolutePath());

		/*OWLOntologyLibrary ontologyLibrary = (OWLOntologyLibrary) technologyContextManager;
		OWLOntologyResource ontologyResource = new OWLOntologyResource(retrieveModelURI(aModelFile, technologyContextManager),
				ontologyLibrary);
		ontologyResource.setServiceManager(getTechnologyAdapterService().getFlexoServiceManager());
		logger.info("Found OWL ontology " + ontologyResource.getURI() + " file:" + aModelFile.getAbsolutePath());
		return ontologyResource;*/
		return null;
	}

	@Override
	public FlexoResource<OWLOntology> retrieveModelResource(File aModelFile,
			TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLOntologyResource createEmptyModel(FlexoProject project, String filename, String modelUri,
			FlexoResource<OWLOntology> metaModel, TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewOWLModel(), project=" + project);
		}
		logger.info("-------------> Create ontology for " + project.getProjectName());

		File owlFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		OWLOntologyLibrary ontologyLibrary = (OWLOntologyLibrary) technologyContextManager;
		OWLOntologyResource returned = OWLOntologyResourceImpl.makeOWLOntologyResource(modelUri, owlFile, ontologyLibrary);
		try {
			returned.save(null);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return returned;
	}

	@Override
	public FlexoResource<OWLOntology> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<OWLOntology> metaModelResource,
			TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLModelRepository createModelRepository(FlexoResourceCenter resourceCenter) {
		return new OWLModelRepository(this, resourceCenter);
	}

	@Override
	public OWLMetaModelRepository createMetaModelRepository(FlexoResourceCenter resourceCenter) {
		return new OWLMetaModelRepository(this, resourceCenter);
	}

	@Override
	public TechnologyContextManager<OWLOntology, OWLOntology> createTechnologyContextManager(
			FlexoResourceCenterService resourceCenterService) {
		return new OWLOntologyLibrary(this, resourceCenterService);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public String retrieveModelURI(File aModelFile, FlexoResource<OWLOntology> metaModelResource,
			TechnologyContextManager<OWLOntology, OWLOntology> technologyContextManager) {
		return OWLOntology.findOntologyURI(aModelFile);
	}

	/**
	 * Provides a hook to finalize initialization of a TechnologyAdapter.<br>
	 * This method is called:
	 * <ul>
	 * <li>after all TechnologyAdapter have been loaded</li>
	 * <li>after all {@link FlexoResourceCenter} have been initialized</li>
	 * </ul>
	 */
	@Override
	public void initialize() {
		getTechnologyContextManager().init();
	}

	/**
	 * Provides a hook to detect when a new resource center was added or discovered
	 * 
	 * @param newResourceCenter
	 */
	@Override
	public void resourceCenterAdded(FlexoResourceCenter newResourceCenter) {
		getTechnologyContextManager().init();
	}

	@Override
	public OWLOntologyLibrary getTechnologyContextManager() {
		return (OWLOntologyLibrary) super.getTechnologyContextManager();
	}

	public OWLOntologyLibrary getOntologyLibrary() {
		return getTechnologyContextManager();
	}

	@Override
	public OWLBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

}
