package org.openflexo.technologyadapter.diagram;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.FreeModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;

public class FreeDiagramModelSlotInstanceConfiguration extends FreeModelSlotInstanceConfiguration<Diagram, FreeDiagramModelSlot> {

	private static final Logger logger = Logger.getLogger(ModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoResourceCenter<?> resourceCenter;
	protected DiagramResource diagramResource;

	protected FreeDiagramModelSlotInstanceConfiguration(FreeDiagramModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewResource) {
			resourceUri = getAction().getFocusedObject().getProject().getURI() + "/Diagrams/myDiagram";
			relativePath = "/";
			filename = "myDiagram.diagram";
		}
	}

}
