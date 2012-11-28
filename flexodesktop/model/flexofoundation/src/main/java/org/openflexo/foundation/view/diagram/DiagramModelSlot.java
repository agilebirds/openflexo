package org.openflexo.foundation.view.diagram;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
public class DiagramModelSlot extends ModelSlotImpl<View, DiagramMetaModel> {

	public DiagramModelSlot(ViewPoint viewPoint, DiagramTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public DiagramModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getFullyQualifiedName() {
		return "DiagramModelSlot";
	}

	@Override
	public String getClassNameKey() {
		return "diagram_model_slot";
	}

}
