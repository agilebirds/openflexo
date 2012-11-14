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

import java.io.File;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.module.FlexoResourceCenterService;

public class ProjectDialogEDITOR {

	public static FlexoEditor loadProject(File prjDir) {
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

	private static FlexoResourceCenterService getFlexoResourceCenterService() {
		return FlexoResourceCenterService.getInstance();
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

	}

}
