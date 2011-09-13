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
import org.openflexo.foundation.ontology.calc.CalcFolder;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;


public class LocalResourceCenterImplementation implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(LocalResourceCenterImplementation.class.getPackage().getName());

	public static final File CALC_LIBRARY_DIR = new FileResource("Calcs");
	public static final File ONTOLOGY_LIBRARY_DIR = new FileResource("Ontologies");
	public static final String FLEXO_ONTOLOGY_ROOT_URI = "http://www.agilebirds.com/flexo/ontologies";

	private File localDirectory;
	private OntologyLibrary baseOntologyLibrary;
	private CalcLibrary calcLibrary;
	private File newCalcSandboxDirectory;

	
	public LocalResourceCenterImplementation(File resourceCenterDirectory) {
		super();
		localDirectory = resourceCenterDirectory;
		newCalcSandboxDirectory = new File(resourceCenterDirectory, "Calcs");
	}

	public static LocalResourceCenterImplementation instanciateNewLocalResourceCenterImplementation(File resourceCenterDirectory) {
		copyCalcs(CALC_LIBRARY_DIR, resourceCenterDirectory);
		copyOntologies(ONTOLOGY_LIBRARY_DIR, resourceCenterDirectory);
		return new LocalResourceCenterImplementation(resourceCenterDirectory);
	}

	private static void copyCalcs(File calcDir, File resourceCenterDirectory) {
		File resourceCenterCalcDirectory = new File(resourceCenterDirectory, "Calcs");
		if (!resourceCenterCalcDirectory.exists()) {
			resourceCenterCalcDirectory.mkdir();
		}
		try {
			FileUtils.copyContentDirToDir(calcDir, resourceCenterCalcDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*for (File f : calcDir.listFiles()) {
			if (f.isDirectory() && f.getName().endsWith(".calc")) {
				try {
					System.out.println("Copy " + f.getAbsolutePath());
					FileUtils.copyContentDirToDir(f, new File(resourceCenterCalcDirectory, f.getName()));
				} catch (IOException e) {
					logger.warning("IOException: " + e.getMessage());
				}
			} else if (f.isDirectory()) {
				copyCalcs(f, new File(resourceCenterDirectory, f.getName()));
			}
		}*/
	}

	private static void copyOntologies(File dir, File resourceCenterDirectory) {
		File resourceCenterOntologiesDirectory = new File(resourceCenterDirectory, "Ontologies");
		if (!resourceCenterOntologiesDirectory.exists()) {
			resourceCenterOntologiesDirectory.mkdir();
		}
		try {
			FileUtils.copyContentDirToDir(dir, resourceCenterOntologiesDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public OntologyCalc getOntologyCalc(String ontologyCalcUri) {
		return retrieveCalcLibrary().getOntologyCalc(ontologyCalcUri);
	}

	@Override
	public OntologyLibrary retrieveBaseOntologyLibrary() {
		if (baseOntologyLibrary == null) {
			logger.fine("Instanciating BaseOntologyLibrary");
			baseOntologyLibrary = new OntologyLibrary(this, null);
			findOntologies(new File(localDirectory, "Ontologies"), FLEXO_ONTOLOGY_ROOT_URI,baseOntologyLibrary.getRootFolder());
			// baseOntologyLibrary.init();
			logger.fine("Instanciating BaseOntologyLibrary Done. Loading some ontologies...");
			// baseOntologyLibrary.debug();
			baseOntologyLibrary.getRDFSOntology().loadWhenUnloaded();
			baseOntologyLibrary.getRDFOntology().loadWhenUnloaded();
			baseOntologyLibrary.getOWLOntology().loadWhenUnloaded();
			baseOntologyLibrary.THING = baseOntologyLibrary.getClass(OntologyLibrary.OWL_ONTOLOGY_URI + "#Thing");
			baseOntologyLibrary.getRDFSOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getRDFOntology().updateConceptsAndProperties();
			baseOntologyLibrary.getFlexoConceptOntology().loadWhenUnloaded();
			// baseOntologyLibrary.debug();
		}
		return baseOntologyLibrary;

	}

	@Override
	public CalcLibrary retrieveCalcLibrary() {
		if (calcLibrary == null) {
			calcLibrary = new CalcLibrary(this, retrieveBaseOntologyLibrary());
			findCalcs(new File(localDirectory, "Calcs"),calcLibrary.getRootFolder());
		}
		return calcLibrary;
	}

	private void findOntologies(File dir, String baseUri, OntologyFolder folder) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir.listFiles().length == 0) {
			copyOntologies(ONTOLOGY_LIBRARY_DIR, dir.getParentFile());
		}
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".owl")) {
				String uri = FlexoOntology.findOntologyURI(f);
				if (uri == null) {
					uri = baseUri + "/" + f.getName();
				}
				baseOntologyLibrary.importOntology(uri, f, folder);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				OntologyFolder newFolder = new OntologyFolder(f.getName(), folder,baseOntologyLibrary);
				findOntologies(f, baseUri + "/" + f.getName(),newFolder);
			}
		}
	}

	private void findCalcs(File dir, CalcFolder folder) 
	{
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir.listFiles().length == 0) {
			copyCalcs(CALC_LIBRARY_DIR, dir.getParentFile());
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory() && f.getName().endsWith(".calc")) {
				calcLibrary.importCalc(f,folder);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				CalcFolder newFolder = new CalcFolder(f.getName(), folder,calcLibrary);
				findCalcs(f,newFolder);
			}
		}
	}

	@Override
	public File getNewCalcSandboxDirectory() {
		return newCalcSandboxDirectory;
	}

	@Override
	public String toString() {
		return super.toString() + " directory=" + localDirectory.getAbsolutePath();
	}

}
