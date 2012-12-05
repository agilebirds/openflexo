package org.openflexo.model;

import junit.framework.TestCase;

import org.flexo.model.AbstractNode;
import org.flexo.model.FlexoModelObject;
import org.flexo.model.FlexoProcess;
import org.flexo.model.StartNode;
import org.flexo.model.TokenEdge;
import org.flexo.model.WKFObject;
import org.openflexo.model.exceptions.ModelDefinitionException;

public abstract class AbstractPAMELATest extends TestCase {

	/**
	 * Little hack to access the library clear() method. This is only for testing purposes.
	 */
	protected void clearModelEntityLibrary() {
		ModelEntityLibrary.clear();
	}

	protected void validateBasicModelContext(ModelContext modelContext) throws ModelDefinitionException {
		ModelEntity<FlexoModelObject> modelObjectEntity = modelContext.getModelEntity(FlexoModelObject.class);
		ModelEntity<FlexoProcess> processEntity = modelContext.getModelEntity(FlexoProcess.class);
		ModelEntity<AbstractNode> abstractNodeEntity = modelContext.getModelEntity(AbstractNode.class);
		ModelEntity<StartNode> startNodeEntity = modelContext.getModelEntity(StartNode.class);
		ModelEntity<TokenEdge> tokenEdgeEntity = modelContext.getModelEntity(TokenEdge.class);
		ModelEntity<WKFObject> wkfObjectEntity = modelContext.getModelEntity(WKFObject.class);

		assertNotNull(processEntity);
		assertNotNull(abstractNodeEntity);
		assertNotNull(startNodeEntity);
		assertNotNull(tokenEdgeEntity);
		assertNotNull(wkfObjectEntity);

		ModelProperty<? super FlexoProcess> nodesProperty = processEntity.getModelProperty(FlexoProcess.NODES);
		assertNotNull(nodesProperty);
		ModelProperty<? super FlexoProcess> fooProperty = processEntity.getModelProperty(FlexoProcess.FOO);
		assertNotNull(fooProperty);
		assertNotNull(modelObjectEntity.getModelProperty(FlexoModelObject.FLEXO_ID));
		assertNotNull(processEntity.getModelProperty(FlexoModelObject.FLEXO_ID));
		assertNotNull(wkfObjectEntity.getModelProperty(FlexoModelObject.FLEXO_ID));
		assertNotNull(wkfObjectEntity.getModelProperty(FlexoModelObject.FLEXO_ID).getSetter());

		ModelProperty<? super WKFObject> wkfObjectProcessProperty = wkfObjectEntity.getModelProperty(WKFObject.PROCESS);
		assertNotNull(wkfObjectProcessProperty);
		assertNull(wkfObjectProcessProperty.getInverseProperty());
		assertNotNull(wkfObjectProcessProperty.getSetter());
		ModelProperty<? super AbstractNode> abstractNodeProcessProperty = abstractNodeEntity.getModelProperty(WKFObject.PROCESS);
		assertNotNull(abstractNodeProcessProperty);
		assertNotNull(abstractNodeProcessProperty.getInverseProperty());
		assertNotNull(abstractNodeProcessProperty.getSetter());
		assertTrue(modelObjectEntity.getAllDescendants(modelContext).contains(processEntity));
	}
}
