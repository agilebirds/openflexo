package org.openflexo.technologyadapter.emf.model;

import java.util.Collection;
import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.ontology.util.FlexoOntologyUtility;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.toolbox.FileResource;

public class TestLoadEMFMetaModel {
	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	@Test
	public void test() {
		ApplicationContext applicationContext = new TestApplicationContext(new FileResource("D:/gbe/work/EMF"));
		EMFTechnologyAdapter technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(
				EMFTechnologyAdapter.class);

		for (FlexoResourceCenter resourceCenter : applicationContext.getResourceCenterService().getResourceCenters()) {
			MetaModelRepository<FlexoResource<EMFMetaModel>, EMFModel, EMFMetaModel, EMFTechnologyAdapter> metaModelRepository = resourceCenter
					.getMetaModelRepository(technologicalAdapter);
			Collection<FlexoResource<EMFMetaModel>> metaModelResources = metaModelRepository.getAllResources();
			for (FlexoResource<EMFMetaModel> metaModelResource : metaModelResources) {
				EMFMetaModel metaModel = ((EMFMetaModelResource) metaModelResource).getMetaModel();
				System.out.println(FlexoOntologyUtility.toString(metaModel));
			}
			ModelRepository<FlexoResource<EMFModel>, EMFModel, EMFMetaModel, EMFTechnologyAdapter> modelRepository = resourceCenter
					.getModelRepository(technologicalAdapter);
			Collection<FlexoResource<EMFModel>> modelResources = modelRepository.getAllResources();
			for (FlexoResource<EMFModel> modelResource : modelResources) {
				EMFModel model = ((EMFModelResource) modelResource).getResourceData();
				System.out.println(FlexoOntologyUtility.toString(model));
			}
		}
	}
}