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
package org.openflexo.dre.controller.action;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.action.DeleteDocItemFolder;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DeleteDocItemFolderInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteDocItemFolderInitializer(DREControllerActionInitializer actionInitializer) {
		super(DeleteDocItemFolder.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteDocItemFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteDocItemFolder>() {
			@Override
			public boolean run(EventObject e, DeleteDocItemFolder action) {
				if (action.getFocusedObject() instanceof DocItemFolder) {
					Vector<DocItemFolder> v = new Vector<DocItemFolder>();
					v.add((DocItemFolder) action.getFocusedObject());
					if (action.getGlobalSelection() != null) {
						Enumeration en = action.getGlobalSelection().elements();
						while (en.hasMoreElements()) {
							Object o = en.nextElement();
							if (o instanceof DocItemFolder) {
								if (!v.contains(o)) {
									v.add((DocItemFolder) o);
								}
							}
						}
					}
					action.setFolders(v);
					return FlexoController.confirm(FlexoLocalization.localizedForKey("are_you_sure_you_want_to_delete ") + v.size()
							+ FlexoLocalization.localizedForKey(" objects"));
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteDocItemFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteDocItemFolder>() {
			@Override
			public boolean run(EventObject e, DeleteDocItemFolder action) {
				return true;
			}
		};
	}

}
