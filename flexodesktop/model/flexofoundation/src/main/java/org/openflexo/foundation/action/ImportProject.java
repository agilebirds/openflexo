package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.ProjectDataResource;
import org.openflexo.foundation.resource.FlexoProjectReference;

public class ImportProject extends FlexoAction<ImportProject, FlexoProjectObject, FlexoProjectObject> {

	public static final FlexoActionType<ImportProject, FlexoProjectObject, FlexoProjectObject> actionType = new FlexoActionType<ImportProject, FlexoProjectObject, FlexoProjectObject>(
			"import_project") {

		@Override
		public ImportProject makeNewAction(FlexoProjectObject focusedObject, Vector<FlexoProjectObject> globalSelection, FlexoEditor editor) {
			return new ImportProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProjectObject object, Vector<FlexoProjectObject> globalSelection) {
			return object != null && object.getProject() != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoProjectObject object, Vector<FlexoProjectObject> globalSelection) {
			return object != null && object.getProject() != null;
		}
	};

	static {
		// FlexoProjectObject.addActionForClass(actionType, FlexoWorkflow.class);
		// FlexoProjectObject.addActionForClass(actionType, FlexoProcess.class);
		// FlexoObject.addActionForClass(actionType, FlexoProject.class);
	}

	private FlexoProject projectToImport;

	public ImportProject(FlexoProjectObject focusedObject, Vector<FlexoProjectObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProject project = getImportingProject();
		ProjectDataResource resource = project.getProjectDataResource();
		FlexoProjectReference projectReference = resource.getFactory().newInstance(FlexoProjectReference.class).init(projectToImport);
		project.getProjectData().addToImportedProjects(projectReference);
	}

	public FlexoProject getImportingProject() {
		return getFocusedObject().getProject();
	}

	public FlexoProject getProjectToImport() {
		return projectToImport;
	}

	public void setProjectToImport(FlexoProject projectToImport) {
		this.projectToImport = projectToImport;
	}

}
