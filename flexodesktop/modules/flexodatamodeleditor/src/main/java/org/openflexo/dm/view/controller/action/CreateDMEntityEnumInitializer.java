package org.openflexo.dm.view.controller.action;

import org.openflexo.dm.view.DMPackageView;
import org.openflexo.dm.view.DMRepositoryView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateDMEntityEnum;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDMEntityEnumInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMEntityEnumInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMEntityEnum.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMEntityEnum> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMEntityEnum>() {
			@Override
			public boolean run(ActionEvent e, CreateDMEntityEnum action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMEntityEnum> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMEntityEnum>() {
			@Override
			public boolean run(ActionEvent e, CreateDMEntityEnum action) {
				if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getRepository()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEntity in DMRepositoryView");
					}
					DMRepositoryView repView = (DMRepositoryView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					repView.getPackageTable().selectObject(action.getPackage());
					repView.getEntityTable().selectObject(action.getNewEntity());
				} else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getPackage()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEntity in DMPackageView");
					}
					DMPackageView packageView = (DMPackageView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					packageView.getEntityTable().selectObject(action.getNewEntity());
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_ENTITY_ENUMERATION_ICON;
	}

}
