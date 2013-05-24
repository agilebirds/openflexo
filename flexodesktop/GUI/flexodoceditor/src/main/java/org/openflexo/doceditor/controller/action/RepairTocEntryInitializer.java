/*
 * (c) Copyright 2010-2011 AgileBirds
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
 */
package org.openflexo.doceditor.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.param.DMEOEntityParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ProcessParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.action.RepairTOCEntry;
import org.openflexo.foundation.toc.action.RepairTOCEntry.FixProposal;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class RepairTocEntryInitializer extends ActionInitializer<RepairTOCEntry, TOCEntry, TOCObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RepairTocEntryInitializer(DEControllerActionInitializer actionInitializer) {
		super(RepairTOCEntry.actionType, actionInitializer);
	}

	@Override
	protected DEControllerActionInitializer getControllerActionInitializer() {
		return (DEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RepairTOCEntry> getDefaultInitializer() {
		return new FlexoActionInitializer<RepairTOCEntry>() {
			@Override
			public boolean run(EventObject e, RepairTOCEntry action) {
				ParameterDefinition pd[] = new ParameterDefinition[4];
				pd[0] = new RadioButtonListParameter<RepairTOCEntry.FixProposal>("choice", "choose", FixProposal.DELETE,
						FixProposal.values());
				int i = 1;
				String PROCESS = FlexoLocalization.localizedForKey("process");
				String ENTITY = FlexoLocalization.localizedForKey("entity");
				String[] radios = new String[] { PROCESS, ENTITY };
				RadioButtonListParameter<String> radiosParameters = null;
				if (action.getFocusedObject().getObjectReference() == null) {
					radiosParameters = new RadioButtonListParameter<String>("object_type", "choose_object_type", PROCESS, radios);
					pd[i] = radiosParameters;
					i++;
				}
				if (action.getFocusedObject().getObjectReference() == null
						|| action.getFocusedObject().getObjectReference().getClassName().equals(FlexoProcess.class.getName())) {
					pd[i] = new ProcessParameter("object", "process", null);
					pd[i].setDepends("choice");
					pd[i].setConditional("choice=" + FixProposal.CHOOSE_OTHER_OBJECT);
					if (radiosParameters != null) {
						pd[i].setDepends("object_type");
						pd[i].setConditional("object_type=" + PROCESS);
					}
					i++;
				}
				if (action.getFocusedObject().getObjectReference() == null || action.getFocusedObject().getObjectReference() == null
						|| action.getFocusedObject().getObjectReference().getClassName().equals(DMEOEntity.class.getName())) {
					pd[i] = new DMEOEntityParameter("object", "entity", null);
					pd[i].setDepends("choice");
					pd[i].setConditional("choice=" + FixProposal.CHOOSE_OTHER_OBJECT);
					if (radiosParameters != null) {
						pd[i].setDepends("object_type");
						pd[i].setConditional("object_type=" + ENTITY);
					}
					i++;
				}
				AskParametersDialog d = AskParametersDialog.createAskParametersDialog(getProject(), getController().getFlexoFrame(),
						FlexoLocalization.localizedForKey("repair_toc_entry"), FlexoLocalization.localizedForKey("choose_how_to_repair"),
						pd);
				if (d.getStatus() == AskParametersDialog.VALIDATE) {
					if (pd[0].getValue() == FixProposal.CHOOSE_OTHER_OBJECT && pd[1].getValue() == null) {
						FlexoController.notify(FlexoLocalization.localizedForKey("you_must_choose_an_object"));
						return false;
					}
					action.setChoice((FixProposal) pd[0].getValue());
					if (pd[0].getValue() == FixProposal.CHOOSE_OTHER_OBJECT) {
						FlexoModelObject newObject = null;
						for (int k = 1; k < pd.length; k++) {
							if (pd[k] != null && pd[k].getValue() instanceof FlexoModelObject) {
								newObject = (FlexoModelObject) pd[k].getValue();
							}
						}
						action.setModelObject(newObject);
					}
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RepairTOCEntry> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RepairTOCEntry>() {
			@Override
			public boolean run(EventObject e, RepairTOCEntry action) {
				return true;
			}
		};
	}

}
