/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

/**
 * Test EMF Meta-Model and model loading.
 * 
 * @author gbesancon
 * 
 */
public class TestLoadEMFMetaModel extends FlexoTestCase {
	protected static final Logger logger = Logger.getLogger(TestLoadEMFMetaModel.class.getPackage().getName());

	/**
	 * Follow the link.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		instanciateTestServiceManager();
	}

	public void testLoadEMFMetaModel() {
		EMFTechnologyAdapter technologicalAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(
				EMFTechnologyAdapter.class);

		for (FlexoResourceCenter resourceCenter : serviceManager.getResourceCenterService().getResourceCenters()) {
			MetaModelRepository<FlexoResource<EMFMetaModel>, EMFModel, EMFMetaModel, EMFTechnologyAdapter> metaModelRepository = resourceCenter
					.getMetaModelRepository(technologicalAdapter);
			assertNotNull(metaModelRepository);
			Collection<FlexoResource<EMFMetaModel>> metaModelResources = metaModelRepository.getAllResources();
			for (FlexoResource<EMFMetaModel> metaModelResource : metaModelResources) {
				EMFMetaModel metaModel = ((EMFMetaModelResource) metaModelResource).getMetaModelData();
				assertNotNull(metaModel);
			}
			ModelRepository<FlexoResource<EMFModel>, EMFModel, EMFMetaModel, EMFTechnologyAdapter> modelRepository = resourceCenter
					.getModelRepository(technologicalAdapter);
			Collection<FlexoResource<EMFModel>> modelResources = modelRepository.getAllResources();
			for (FlexoResource<EMFModel> modelResource : modelResources) {
				EMFModel model = ((EMFModelResource) modelResource).getResourceData();
				assertNotNull(model);
			}
		}
	}
}