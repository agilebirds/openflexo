/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.mdtuml;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.viewpoint.binding.EMFBindingFactory;

/**
 * This class defines and implements the MDT-UML technology adapter
 * 
 * @author Christophe Guychard
 * 
 */
public class MDTUMLTechnologyAdapter extends EMFTechnologyAdapter {

	protected static final Logger logger = Logger.getLogger(MDTUMLTechnologyAdapter.class.getPackage().getName());

	private static String UML_FILE_EXTENSION = ".uml";

	private static final EMFBindingFactory BINDING_FACTORY = new EMFBindingFactory();

	/**
	 * 
	 * Constructor.
	 * 
	 * @throws TechnologyAdapterInitializationException
	 */
	public MDTUMLTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getName()
	 */
	@Override
	public String getName() {
		return "MDT-UML technology adapter";
	}

	/**
	 * 
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidMetaModelFile(File aMetaModelFile,
	 *      TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager)
	 */
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager technologyContextManager) {
		return false;
	}

	/**
	 * 
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidModelFile(File aModelFile,
	 *      TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager)
	 */
	public boolean isValidModelFile(File aModelFile, TechnologyContextManager technologyContextManager) {

		boolean valid = false;
		if (aModelFile.exists()) {
			if (aModelFile.getName().endsWith(UML_FILE_EXTENSION) && aModelFile.isFile()) {
				valid = true;
			}
		}
		return valid;
	}

	/**
	 * 
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidModelFile(File aModelFile, FlexoResource<EMFMetaModel>
	 *      metaModelResource, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager)
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {
		// EMFMetaModelResource emfMetaModelResource = (EMFMetaModelResource)metaModelResource;
		return isValidModelFile(aModelFile, technologyContextManager);
	}

}
