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
package org.openflexo.foundation.dm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.ProcessBusinessDataRepository;

public class GenerateProcessesBusinessDataDMEntity extends
		FlexoAction<GenerateProcessesBusinessDataDMEntity, ProcessBusinessDataRepository, DMObject> {

	private static final Logger logger = Logger.getLogger(GenerateProcessesBusinessDataDMEntity.class.getPackage().getName());

	public static FlexoActionType<GenerateProcessesBusinessDataDMEntity, ProcessBusinessDataRepository, DMObject> actionType = new FlexoActionType<GenerateProcessesBusinessDataDMEntity, ProcessBusinessDataRepository, DMObject>(
			"generate_processes_business_data", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {
		@Override
		public GenerateProcessesBusinessDataDMEntity makeNewAction(ProcessBusinessDataRepository focusedObject,
				Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new GenerateProcessesBusinessDataDMEntity(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ProcessBusinessDataRepository object, Vector<DMObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(ProcessBusinessDataRepository object, Vector<DMObject> globalSelection) {
			return true;
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, ProcessBusinessDataRepository.class);
	}

	GenerateProcessesBusinessDataDMEntity(ProcessBusinessDataRepository focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Generate processes business data");
		getFocusedObject().generateProcessBusinessData();
	}
}
