package org.openflexo.ve.shema;

import java.awt.event.MouseEvent;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.action.ActionSchemeAction;
import org.openflexo.foundation.view.action.ActionSchemeActionType;
import org.openflexo.foundation.view.action.NavigationSchemeAction;
import org.openflexo.foundation.view.action.NavigationSchemeActionType;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.foundation.viewpoint.GraphicalElementAction;
import org.openflexo.foundation.viewpoint.GraphicalElementAction.ActionMask;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.NavigationScheme;

public class VEMouseClickControl extends MouseClickControl {

	private GraphicalElementPatternRole patternRole = null;
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
		FlexoEditor editor = ((VEShemaController) controller).getEditor();
		ViewElement ve = null;
		if (graphicalRepresentation instanceof VEShapeGR) {
			VEShapeGR gr = (VEShapeGR) graphicalRepresentation;
			ve = gr.getDrawable();
		} else if (graphicalRepresentation instanceof VEConnectorGR) {
			VEConnectorGR gr = (VEConnectorGR) graphicalRepresentation;
			ve = gr.getDrawable();
		}
		if (ve != null && ve.getEditionPatternReference() != null) {
			for (GraphicalElementAction action : patternRole.getActions(mask)) {
				if (action.evaluateCondition(ve.getEditionPatternReference())) {
					if (action.getAbstractActionScheme() instanceof NavigationScheme) {
						NavigationSchemeAction navigationAction = new NavigationSchemeActionType(
								(NavigationScheme) action.getAbstractActionScheme(), ve.getEditionPatternReference()).makeNewAction(ve,
								null, editor);
						navigationAction.doAction();
					} else if (action.getAbstractActionScheme() instanceof ActionScheme) {
						ActionSchemeAction actionAction = new ActionSchemeActionType((ActionScheme) action.getAbstractActionScheme(),
								ve.getEditionPatternReference()).makeNewAction(ve, null, editor);
						actionAction.doAction();
					}
				}
			}
		}
	}
}
