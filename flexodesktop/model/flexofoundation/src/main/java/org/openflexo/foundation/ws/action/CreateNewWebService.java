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
package org.openflexo.foundation.ws.action;

import java.util.Vector;

import org.openflexo.dataimporter.DataImporterLoader.KnownDataImporter;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ws.WSObject;

public class CreateNewWebService extends AbstractCreateNewWebService<CreateNewWebService> {

	public static final FlexoActionType<CreateNewWebService, WSObject, WSObject> actionType = new FlexoActionType<CreateNewWebService, WSObject, WSObject>(
			"ws_add_webservice...", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateNewWebService makeNewAction(WSObject focusedObject, Vector<WSObject> globalSelection, FlexoEditor editor) {
			return new CreateNewWebService(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WSObject object, Vector<WSObject> globalSelection) {
			return KnownDataImporter.WSDL_IMPORTER.isAvailable();
		}

		@Override
		public boolean isEnabledForSelection(WSObject object, Vector<WSObject> globalSelection) {
			return true;
		}

	};

	public CreateNewWebService(WSObject focusedObject, Vector<WSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
