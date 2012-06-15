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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyFolder;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointFolder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

public class LocalResourceCenterImplementation implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(LocalResourceCenterImplementation.class.getPackage().getName());

	private static final File CALC_LIBRARY_DIR = new FileResource("ViewPoints");
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
		LocalResourceCenterImplementation localResourceCenterImplementation = new LocalResourceCenterImplementation(resourceCenterDirectory);
		localResourceCenterImplementation.update();
		return localResourceCenterImplementation;
	}

	private static void copyViewPoints(File resourceCenterDirectory, CopyStrategy copyStrategy) {
		if (CALC_LIBRARY_DIR.getParentFile().equals(resourceCenterDirectory)) {
			return;
		}

		try {
			FileUtils.copyDirToDir(CALC_LIBRARY_DIR, resourceCenterDirectory, copyStrategy);
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

	@Override
	public ViewPoint getOntologyCalc(String ontologyCalcUri) {
		return retrieveViewPointLibrary().getOntologyCalc(ontologyCalcUri);
	}

	@Override
	public OntologyLibrary retrieveBaseOntologyLibrary() {
		if (baseOntologyLibrary == null) {
			logger.fine("Instantiating BaseOntologyLibrary");
			baseOntologyLibrary = new OntologyLibrary(this, null);
			findOntologies(new File(localDirectory, "Ontologies"), FLEXO_ONTOLOGY_ROOT_URI, baseOntologyLibrary.getRootFolder());
			// baseOntologyLibrary.init();
			logger.fine("Instantiating BaseOntologyLibrary Done. Loading some ontologies...");
			// baseOntologyLibrary.debug();
			baseOntologyLibrary.getRDFSOntology().loadWhenUnloaded();
			baseOntologyLibrary.getRDFOntology().loadWhenUnloaded();
			baseOntologyLibrary.getOWLOntology().loadWhenUnloaded();
			baseOntologyLibrary.THING = baseOntologyLibrary.getOWLOntology().getClass(OntologyLibrary.OWL_THING_URI);
			baseOntologyLibrary.getRDFSOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getRDFOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getOWLOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getFlexoConceptOntology().loadWhenUnloaded();
			// baseOntologyLibrary.debug();
		}
		return baseOntologyLibrary;

	}

	@Override
	public ViewPointLibrary retrieveViewPointLibrary() {
		if (viewPointLibrary == null) {
			viewPointLibrary = new ViewPointLibrary(this, retrieveBaseOntologyLibrary());
			findViewPoints(new File(localDirectory, "ViewPoints"), viewPointLibrary.getRootFolder());
		}
		return viewPointLibrary;
	}

	private void findOntologies(File dir, String baseUri, OntologyFolder folder) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir.listFiles().length == 0) {
			copyOntologies(localDirectory, CopyStrategy.REPLACE);
		}
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".owl")) {
				String uri = FlexoOntology.findOntologyURI(f);
				if (uri == null) {
					uri = baseUri + "/" + f.getName();
				}
				baseOntologyLibrary.importOntology(uri, f, folder);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				OntologyFolder newFolder = new OntologyFolder(f.getName(), folder, baseOntologyLibrary);
				findOntologies(f, baseUri + "/" + f.getName(), newFolder);
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
		copyOntologies(localDirectory, CopyStrategy.REPLACE_OLD_ONLY);
		copyViewPoints(localDirectory, CopyStrategy.REPLACE_OLD_ONLY);
	}
}
