package org.openflexo.ve.diagram;

import java.awt.event.MouseEvent;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.view.action.ActionSchemeAction;
import org.openflexo.foundation.view.action.ActionSchemeActionType;
import org.openflexo.foundation.view.action.NavigationSchemeActionType;
import org.openflexo.foundation.view.diagram.action.NavigationSchemeAction;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementAction.ActionMask;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.NavigationScheme;
import org.openflexo.foundation.viewpoint.ActionScheme;

public class VEMouseClickControl extends MouseClickControl {

	private GraphicalElementPatternRole<?> patternRole = null;
	private GraphicalElementAction.ActionMask mask;

	public VEMouseClickControl(GraphicalElementAction.ActionMask mask, GraphicalElementPatternRole patternRole) {
		super(mask.name(), MouseButton.LEFT, mask == ActionMask.DoubleClick ? 2 : 1, mask == ActionMask.ShiftClick,
				mask == ActionMask.CtrlClick, mask == ActionMask.MetaClick, mask == ActionMask.AltClick);
		this.patternRole = patternRole;
		this.mask = mask;
	}

	@Override
	public void handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event) {
		super.handleClick(graphicalRepresentation, controller, event);
		FlexoEditor editor = ((DiagramController) controller).getEditor();
		DiagramElement diagramElement = null;
		if (graphicalRepresentation instanceof DiagramShapeGR) {
			DiagramShapeGR gr = (DiagramShapeGR) graphicalRepresentation;
			diagramElement = gr.getDrawable();
		} else if (graphicalRepresentation instanceof DiagramConnectorGR) {
			DiagramConnectorGR gr = (DiagramConnectorGR) graphicalRepresentation;
			diagramElement = gr.getDrawable();
		}
		if (diagramElement != null && diagramElement.getEditionPatternInstance() != null) {
			for (GraphicalElementAction action : patternRole.getActions(mask)) {
				if (action.evaluateCondition(diagramElement.getEditionPatternInstance())) {
					if (action.getAbstractActionScheme() instanceof NavigationScheme) {
						NavigationSchemeAction navigationAction = new NavigationSchemeActionType(
								(NavigationScheme) action.getAbstractActionScheme(), diagramElement.getEditionPatternInstance())
								.makeNewAction(diagramElement, null, editor);
						navigationAction.doAction();
					} else if (action.getAbstractActionScheme() instanceof ActionScheme) {
						ActionSchemeAction actionAction = new ActionSchemeActionType((ActionScheme) action.getAbstractActionScheme(),
								diagramElement.getEditionPatternInstance()).makeNewAction(diagramElement, null, editor);
						actionAction.doAction();
					}
				}
			}
		}
	}
}
