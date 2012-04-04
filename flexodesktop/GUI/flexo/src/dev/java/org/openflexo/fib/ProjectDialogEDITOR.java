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
package org.openflexo.fib;

import java.awt.event.ActionEvent;
import java.io.File;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.module.FlexoResourceCenterService;
import org.openflexo.module.ModuleLoader;

public class ProjectDialogEDITOR {

	protected static FlexoEditor loadProject(File prjDir) {
		FlexoResourceCenter resourceCenter = getFlexoResourceCenterService().getFlexoResourceCenter();
		FlexoEditor editor = null;
		try {
			editor = FlexoResourceManager.initializeExistingProject(prjDir, EDITOR_FACTORY, resourceCenter);
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.getProject().setResourceCenter(getFlexoResourceCenterService().getFlexoResourceCenter());
		if (editor == null) {
			System.exit(-1);
		}
		return editor;
	}

	private static ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	private static FlexoResourceCenterService getFlexoResourceCenterService() {
		return FlexoResourceCenterService.instance();
	}

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new FlexoTestEditor(project);
		}
	};

	public static class FlexoTestEditor extends DefaultFlexoEditor {
		public FlexoTestEditor(FlexoProject project) {
			super(project);
		}

		@Override
		public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionInitializer<? super A> getInitializerFor(
				FlexoActionType<A, T1, T2> actionType) {
			FlexoActionInitializer<A> init = new FlexoActionInitializer<A>() {

				@Override
				public boolean run(ActionEvent event, A action) {
					boolean reply = action.getActionType().isEnabled(action.getFocusedObject(), action.getGlobalSelection(),
							FlexoTestEditor.this);
					if (!reply) {
						System.err.println("ACTION NOT ENABLED :" + action.getClass() + " on object "
								+ (action.getFocusedObject() != null ? action.getFocusedObject().getClass() : "null focused object"));
					}
					return reply;
				}

			};
			return init;
		}
	}

}
