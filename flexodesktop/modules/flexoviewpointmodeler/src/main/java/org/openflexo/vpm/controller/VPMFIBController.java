package org.openflexo.vpm.controller;

import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionSchemeObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.action.AddEditionPattern;
import org.openflexo.foundation.viewpoint.action.CreateEditionAction;
import org.openflexo.foundation.viewpoint.action.CreateModelSlot;
import org.openflexo.foundation.viewpoint.action.CreatePatternRole;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Represents the controller used in VPM (ViewPointModeller)<br>
 * Extends FlexoFIBController by supporting features relative to VPM module
 * 
 * 
 * @author sylvain
 */
public class VPMFIBController extends FlexoFIBController {

	protected static final Logger logger = FlexoLogger.getLogger(VPMFIBController.class.getPackage().getName());

	public VPMFIBController(FIBComponent component) {
		super(component);
	}

	public VPMFIBController(FIBComponent component, VPMController controller) {
		super(component, controller);
	}

	@Override
	public VPMController getFlexoController() {
		return (VPMController) super.getFlexoController();
	}

	public ModelSlot createModelSlot(VirtualModel virtualModel) {
		CreateModelSlot createModelSlot = CreateModelSlot.actionType.makeNewAction(virtualModel, null, getEditor());
		createModelSlot.doAction();
		return createModelSlot.getNewModelSlot();
	}

	public PatternRole createPatternRole(EditionPattern editionPattern) {
		CreatePatternRole createPatternRole = CreatePatternRole.actionType.makeNewAction(editionPattern, null, getEditor());
		createPatternRole.doAction();
		return createPatternRole.getNewPatternRole();
	}

	public EditionAction createEditionAction(EditionSchemeObject object) {
		CreateEditionAction createEditionAction = CreateEditionAction.actionType.makeNewAction(object, null, getEditor());
		createEditionAction.doAction();
		return createEditionAction.getNewEditionAction();
	}

	public EditionPattern createEditionPattern(EditionPattern editionPattern) {
		if (editionPattern instanceof VirtualModel) {
			AddEditionPattern addEditionPattern = AddEditionPattern.actionType.makeNewAction((VirtualModel) editionPattern, null,
					getEditor());
			addEditionPattern.switchNewlyCreatedEditionPattern = false;
			addEditionPattern.doAction();
			return addEditionPattern.getNewEditionPattern();
		} else if (editionPattern != null) {
			AddEditionPattern addEditionPattern = AddEditionPattern.actionType.makeNewAction(editionPattern.getVirtualModel(), null,
					getEditor());
			addEditionPattern.switchNewlyCreatedEditionPattern = false;
			addEditionPattern.doAction();
			addEditionPattern.getNewEditionPattern().setParentEditionPattern(editionPattern);
			return addEditionPattern.getNewEditionPattern();
		}
		logger.warning("Unexpected null edition pattern");
		return null;
	}
}
