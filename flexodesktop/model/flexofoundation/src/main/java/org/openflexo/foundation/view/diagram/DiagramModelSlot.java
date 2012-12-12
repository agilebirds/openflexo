package org.openflexo.foundation.view.diagram;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPatternRolePathElement.ConnectorPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPatternRolePathElement.ShapePatternRolePathElement;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
public class DiagramModelSlot extends ModelSlot<View, DiagramMetaModel> {

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
	public BindingVariable<?> makePatternRolePathElement(PatternRole pr, Bindable container) {
		if (pr instanceof ShapePatternRole) {
			return new ShapePatternRolePathElement((ShapePatternRole) pr, container);
		} else if (pr instanceof ConnectorPatternRole) {
			return new ConnectorPatternRolePathElement((ConnectorPatternRole) pr, container);
		} else {
			return null;
		}
	}

}
