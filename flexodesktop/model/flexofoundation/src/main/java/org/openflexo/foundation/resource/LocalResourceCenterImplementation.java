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
package org.openflexo.foundation.resource;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointFolder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.IProgress;

public class LocalResourceCenterImplementation implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(LocalResourceCenterImplementation.class.getPackage().getName());

	private static final File VIEWPOINT_LIBRARY_DIR = new FileResource("ViewPoints");
	private static final File ONTOLOGY_LIBRARY_DIR = new FileResource("Ontologies");
	public static final String FLEXO_ONTOLOGY_ROOT_URI = "http://www.agilebirds.com/openflexo/ontologies";

	private File localDirectory;
	private OntologyLibrary baseOntologyLibrary;
	private ViewPointLibrary viewPointLibrary;
	private File newViewPointSandboxDirectory;

	public LocalResourceCenterImplementation(File resourceCenterDirectory) {
		super();
		localDirectory = resourceCenterDirectory;
		newViewPointSandboxDirectory = new File(resourceCenterDirectory, "ViewPoints");
	}

	public static LocalResourceCenterImplementation instanciateNewLocalResourceCenterImplementation(File resourceCenterDirectory) {
		logger.info("Instanciate ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		LocalResourceCenterImplementation localResourceCenterImplementation = new LocalResourceCenterImplementation(resourceCenterDirectory);
		localResourceCenterImplementation.update();
		return localResourceCenterImplementation;
	}

	public static LocalResourceCenterImplementation instanciateTestLocalResourceCenterImplementation(File resourceCenterDirectory) {
		logger.info("Instanciate TEST ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		LocalResourceCenterImplementation localResourceCenterImplementation = new LocalResourceCenterImplementation(resourceCenterDirectory);
		return localResourceCenterImplementation;
	}

	private static void copyViewPoints(File resourceCenterDirectory, CopyStrategy copyStrategy) {
		if (VIEWPOINT_LIBRARY_DIR.getParentFile().equals(resourceCenterDirectory)) {
			return;
		}

		try {
			FileUtils.copyDirToDir(VIEWPOINT_LIBRARY_DIR, resourceCenterDirectory, copyStrategy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyOntologies(File resourceCenterDirectory, CopyStrategy copyStrategy) {
		if (ONTOLOGY_LIBRARY_DIR.getParentFile().equals(resourceCenterDirectory)) {
			return;
		}
		try {
			FileUtils.copyDirToDir(ONTOLOGY_LIBRARY_DIR, resourceCenterDirectory, copyStrategy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	@Override
	public ViewPoint getOntologyCalc(String ontologyCalcUri) {
		return retrieveViewPointLibrary().getOntologyCalc(ontologyCalcUri);
	}

	@Deprecated
	@Override
	public OntologyLibrary retrieveBaseOntologyLibrary() {
		if (baseOntologyLibrary == null) {
			File baseOntologyFolder = new File(localDirectory, "Ontologies");
			logger.info("Instantiating BaseOntologyLibrary from " + baseOntologyFolder.getAbsolutePath());
			baseOntologyLibrary = new OntologyLibrary(this, null);
			findMetaModels(baseOntologyFolder, FLEXO_ONTOLOGY_ROOT_URI, baseOntologyLibrary.getRootFolder());
			// baseOntologyLibrary.init();

			// Bug fix: compatibility with old versions:
			// If some of those ontologies were not found, try to copy default ontologies
			if (baseOntologyLibrary.getRDFSOntology() == null || baseOntologyLibrary.getRDFOntology() == null
					|| baseOntologyLibrary.getOWLOntology() == null || baseOntologyLibrary.getFlexoConceptOntology() == null) {
				copyOntologies(localDirectory, CopyStrategy.REPLACE);
				findMetaModels(baseOntologyFolder, FLEXO_ONTOLOGY_ROOT_URI, baseOntologyLibrary.getRootFolder());
			}

			logger.fine("Instantiating BaseOntologyLibrary Done. Loading some ontologies...");
			// baseOntologyLibrary.debug();
			baseOntologyLibrary.getRDFSOntology().loadWhenUnloaded();
			baseOntologyLibrary.getRDFOntology().loadWhenUnloaded();
			baseOntologyLibrary.getOWLOntology().loadWhenUnloaded();
			baseOntologyLibrary.getRDFSOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getRDFOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getOWLOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getFlexoConceptOntology().loadWhenUnloaded();
			// baseOntologyLibrary.debug();
		}
		return baseOntologyLibrary;

	}

	@Deprecated
	@Override
	public ViewPointLibrary retrieveViewPointLibrary() {
		if (viewPointLibrary == null) {
			viewPointLibrary = new ViewPointLibrary(this, retrieveBaseOntologyLibrary());
			findViewPoints(new File(localDirectory, "ViewPoints"), viewPointLibrary.getRootFolder());
		}
		return viewPointLibrary;
	}

	/*private static String findOntologyURI(File file, String baseUri) {
		String uri = null;
		if (file.getName().endsWith(".owl")) {
			uri = OWLOntology.findOntologyURI(file);
		} else if (file.getName().endsWith(".xsd")) {
			uri = XSOntology.findOntologyURI(file);
		}
		if (uri == null) {
			uri = baseUri + "/" + file.getName();
		}
		return uri;
	}*/

	private void findMetaModels(File dir, String baseUri, RepositoryFolder folder) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (File f : dir.listFiles()) {
			for (TechnologyAdapter<?, ?, ?> technologyAdapter : TechnologyAdapter.getLoadedAdapters()) {
				if (technologyAdapter.isValidMetaModelFile(f)) {
					baseOntologyLibrary.importMetaModel(technologyAdapter.loadMetaModel(f, baseOntologyLibrary));
				}
			}

			/*if (f.isFile() && (f.getName().endsWith(".owl") || f.getName().endsWith(".xsd"))) {
				String uri = findOntologyURI(f, baseUri);
				baseOntologyLibrary.importMetaModel(uri, f, folder);
			} else*/

			if (f.isDirectory() && !f.getName().equals("CVS")) {
				RepositoryFolder newFolder = new RepositoryFolder(f.getName(), folder, baseOntologyLibrary);
				findMetaModels(f, baseUri + "/" + f.getName(), newFolder);
			}
		}
	}

	private void findViewPoints(File dir, ViewPointFolder folder) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir.listFiles().length == 0) {
			copyViewPoints(localDirectory, CopyStrategy.REPLACE);
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory() && f.getName().endsWith(".viewpoint")) {
				if (f.listFiles().length > 0) {
					viewPointLibrary.importViewPoint(f, folder);
				}
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				ViewPointFolder newFolder = new ViewPointFolder(f.getName(), folder, viewPointLibrary);
				findViewPoints(f, newFolder);
			}
		}
	}

	public File getLocalDirectory() {
		return localDirectory;
	}

	@Deprecated
	@Override
	public File getNewCalcSandboxDirectory() {
		return newViewPointSandboxDirectory;
	}

	@Override
	public String toString() {
		return super.toString() + " directory=" + localDirectory.getAbsolutePath();
	}

	@Override
	public void update() {
		updateOntologiesOrganization();
		copyOntologies(localDirectory, CopyStrategy.REPLACE_OLD_ONLY);
		copyViewPoints(localDirectory, CopyStrategy.REPLACE_OLD_ONLY);
	}

	/**
	 * Called to ensure compatibility with ontologies as in 1.4.4
	 */
	private void updateOntologiesOrganization() {
		File localOntologyDir = new File(localDirectory, "Ontologies");
		File rdfFile = new File(localOntologyDir, "22-rdf-syntax-ns.owl");
		File rdfsFile = new File(localOntologyDir, "rdf-schema.owl");
		File owlFile = new File(localOntologyDir, "owl.owl");
		File fcoFile = new File(localOntologyDir, "FlexoConceptsOntology.owl");
		File archimateDir = new File(localOntologyDir, "Archimate");
		File bpmnDir = new File(localOntologyDir, "BPMN");
		File fmDir = new File(localOntologyDir, "FlexoMethodology");
		File otDir = new File(localOntologyDir, "OrganizationTree");
		File sdDir = new File(localOntologyDir, "ScopeDefinition");
		File skosDir = new File(localOntologyDir, "SKOS");
		File umlDir = new File(localOntologyDir, "UML");
		File w3cDir = new File(localOntologyDir, "www.w3.org");
		File abDir = new File(localOntologyDir, "www.agilebirds.com");
		File archiDir = new File(localOntologyDir, "www.bolton.ac.uk");
		File omgDir = new File(localOntologyDir, "www.omg.org");
		File ofDir = new File(localOntologyDir, "www.openflexo.org");
		if (!w3cDir.exists()) {
			if (rdfFile.exists()) {
				rdfFile.delete();
			}
			if (rdfsFile.exists()) {
				rdfsFile.delete();
			}
			if (owlFile.exists()) {
				owlFile.delete();
			}
			if (skosDir.exists()) {
				FileUtils.deleteDir(skosDir);
			}
		}
		if (!ofDir.exists()) {
			if (fcoFile.exists()) {
				fcoFile.delete();
			}
		}
		if (archimateDir.exists() && !archiDir.exists()) {
			FileUtils.deleteDir(archimateDir);
		}
		if (!omgDir.exists()) {
			if (bpmnDir.exists()) {
				FileUtils.deleteDir(bpmnDir);
			}
			if (umlDir.exists()) {
				FileUtils.deleteDir(umlDir);
			}
		}
		if (!abDir.exists()) {
			if (fmDir.exists()) {
				FileUtils.deleteDir(fmDir);
			}
			if (otDir.exists()) {
				FileUtils.deleteDir(otDir);
			}
			if (sdDir.exists()) {
				FileUtils.deleteDir(sdDir);
			}
		}
	}

	@Override
	public List<FlexoResource<?>> getAllResources(IProgress progress) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(String uri, Class<T> type, IProgress progress) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, String version, Class<T> type, IProgress progress) {
		return null;
	}

	@Override
	public void publishResource(FlexoResource<?> resource, String newVersion, IProgress progress) throws Exception {
		// TODO Auto-generated method stub
	}

	private HashMap<TechnologyAdapter<?, ?, ?>, ModelRepository<?, ?, ?, ?>> modelRepositories = new HashMap<TechnologyAdapter<?, ?, ?>, ModelRepository<?, ?, ?, ?>>();
	private HashMap<TechnologyAdapter<?, ?, ?>, MetaModelRepository<?, ?, ?, ?>> metaModelRepositories = new HashMap<TechnologyAdapter<?, ?, ?>, MetaModelRepository<?, ?, ?, ?>>();

	/**
	 * Retrieve model repository for a given {@link TechnologyAdapter}
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	@Override
	public <R extends FlexoResource<? extends M>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM, ? extends ModelSlot<M, MM>>> ModelRepository<R, M, MM, TA> getModelRepository(
			TA technologyAdapter) {
		ModelRepository<R, M, MM, TA> returned = (ModelRepository<R, M, MM, TA>) modelRepositories.get(technologyAdapter);
		if (returned == null) {
			returned = (ModelRepository<R, M, MM, TA>) technologyAdapter.createModelRepository(this);
		}
		return returned;
	}

	/**
	 * Retrieve meta-model repository for a given {@link TechnologyAdapter}
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	@Override
	public <R extends FlexoResource<? extends MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM, ? extends ModelSlot<M, MM>>> MetaModelRepository<R, M, MM, TA> getMetaModelRepository(
			TA technologyAdapter) {
		MetaModelRepository<R, M, MM, TA> returned = (MetaModelRepository<R, M, MM, TA>) metaModelRepositories.get(technologyAdapter);
		if (returned == null) {
			returned = (MetaModelRepository<R, M, MM, TA>) technologyAdapter.createMetaModelRepository(this);
		}
		return returned;
	}

}
