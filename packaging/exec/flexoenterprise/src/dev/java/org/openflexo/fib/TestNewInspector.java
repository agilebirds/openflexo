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
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.Flexo;
import org.openflexo.TestInteractiveFlexoEditor;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.ModuleLoader;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.SelectionSynchronizedFIBView;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;


public class TestNewInspector {

	private static final Logger logger = FlexoLogger.getLogger(TestNewInspector.class.getPackage().getName());

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		public TestInteractiveFlexoEditor makeFlexoEditor(FlexoProject project) {
			try {
				final TestInteractiveFlexoEditor returned = new TestInteractiveFlexoEditor(project) {
					@Override
					public void objectWasClicked(FlexoModelObject object)
					{
					}

					@Override
					public void objectWasDoubleClicked(FlexoModelObject object)
					{
						logger.info("Double-click on "+object);
						if (object instanceof FlexoProcess) {
							getController().setCurrentEditedObjectAsModuleView(object);
						}
					}
					
					@Override
					public boolean displayInspectorTabForContext(String context) 
					{
						if (context.equalsIgnoreCase(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) return true;
						if (context.equalsIgnoreCase(SWLEditorConstants.SWIMMING_LANE_EDITOR)) return false;
						if (context.equalsIgnoreCase("METRICS")) return true;
						return false;
					};
				};

				final ImageIcon ACTIVE_ICON = new ImageIconResource("Resources/Flexo/WorkflowPerspective_A.gif");
				final ImageIcon SELECTED_ICON = new ImageIconResource("Resources/Flexo/WorkflowPerspective_S.gif");

				returned.getController().addToPerspectives(new FlexoPerspective<FlexoModelObject>("default") {

					@Override
					public ModuleView<? extends FlexoModelObject> createModuleViewForObject(
							FlexoModelObject object, FlexoController controller) {
						if (object instanceof FlexoProcess) {
							return (new ProcessEditorController((FlexoProcess)object, (SelectionManagingController)controller)).getDrawingView();
						}
						return new EmptyPanel<FlexoModelObject>(returned.getController(), this, object);
					}

					@Override
					public ImageIcon getActiveIcon() {
						return ACTIVE_ICON;
					}

					@Override
					public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
						return proposedObject;
					}

					@Override
					public ImageIcon getSelectedIcon() {
						return SELECTED_ICON;
					}

					@Override
					public boolean hasModuleViewForObject(
							FlexoModelObject object) {
						return (object instanceof FlexoProcess);
					}
				});

				return returned;
			} 
			catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
				return null;
			}
		}
	};

	public static FileResource BROWSER_FIB_FILE = new FileResource("Fib/ProjectBrowser.fib");
	public static FileResource PRJ_FILE = new FileResource("Prj/TestBrowser.prj");

	private static FlexoProject project = null;
	private static TestInteractiveFlexoEditor editor;

	public static FlexoProject loadProject()
	{
		File projectFile = PRJ_FILE;
		logger.info("Found project "+projectFile.getAbsolutePath());
		try {
			editor = (TestInteractiveFlexoEditor)FlexoResourceManager.initializeExistingProject(projectFile,EDITOR_FACTORY,null);
			project = editor.getProject();
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Successfully loaded project "+projectFile.getAbsolutePath());

		return project;
	}



	public static void main(String[] args) 
	{
		Flexo.initializeLoggingManager();
		FlexoLoggingManager.setKeepLogTrace(true);
		FlexoLoggingManager.setLogCount(-1);

		loadProject();
		//ModuleLoader.setProject(project);

		editor.getController().getMainPane().setLeftView(retrieveBrowser(project));
		editor.getActiveModule().focusOn();
	}

	public static SelectionSynchronizedFIBView retrieveBrowser(FlexoProject project)
	{
		return new SelectionSynchronizedFIBView<FlexoProject>(project,editor.getController(),BROWSER_FIB_FILE);
	}

	public static class ProcessEditorController extends SelectionManagingDrawingController<ProcessRepresentation> {

		public ProcessEditorController(FlexoProcess process,SelectionManagingController controller)
		{
			super(new ProcessRepresentation(process,editor), controller.getSelectionManager());
		}

		@Override
		public DrawingView<ProcessRepresentation> makeDrawingView(ProcessRepresentation drawing)
		{
			return new ProcessView(drawing,this);
		}
		
		@Override
		public ProcessView getDrawingView()
		{
			return (ProcessView)super.getDrawingView();
		}

		public static class ProcessView extends DrawingView<ProcessRepresentation> implements ModuleView<FlexoProcess> {

			private static final Logger logger = Logger.getLogger(ProcessView.class.getPackage().getName());

			public ProcessView(ProcessRepresentation aDrawing, ProcessEditorController controller)
			{
				super(aDrawing,controller);
			}

			@Override
			public void deleteModuleView()
			{
				getController().delete();
			}

			@Override
			public FlexoPerspective getPerspective()
			{
				return editor.getController().getDefaultPespective();
			}

			@Override
			public FlexoProcess getRepresentedObject()
			{
				return getModel().getProcess();
			}

			@Override
			public boolean isAutoscrolled()
			{
				return false;
			}

			@Override
			public void willHide() {
				getController().getDrawing().disableGraphicalHierarchy();
				getPaintManager().clearPaintBuffer();
			}

			@Override
			public void willShow() {
				getController().getDrawing().enableGraphicalHierarchy();
			}

		}
	}

}



