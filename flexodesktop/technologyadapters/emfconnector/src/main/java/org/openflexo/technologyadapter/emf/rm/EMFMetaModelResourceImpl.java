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
package org.openflexo.technologyadapter.emf.rm;

import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.toolbox.IProgress;

/**
 * EMF MetaModel Resource Implementation.
 * 
 * @author gbesancon
 */
public abstract class EMFMetaModelResourceImpl extends FlexoFileResourceImpl<EMFMetaModel> implements EMFMetaModelResource {

	@Override
	public EMFMetaModel getMetaModel() {
		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#loadResourceData(org.openflexo.toolbox.IProgress)
	 */
	@Override
	public EMFMetaModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException {
		EMFMetaModel returned = new EMFMetaModel(getURI(), getFile(), (EMFTechnologyAdapter) getTechnologyAdapter());
		return returned;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#save(org.openflexo.toolbox.IProgress)
	 */
	@Override
	public void save(IProgress progress) {

	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#update()
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

}
