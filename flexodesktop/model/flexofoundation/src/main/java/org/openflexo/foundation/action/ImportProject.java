package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoPamelaResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ProjectData;

public class ImportProject extends FlexoAction<ImportProject, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<ImportProject, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<ImportProject, FlexoModelObject, FlexoModelObject>(
			"import_project") {

		@Override
		public ImportProject makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new ImportProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object.getProject() != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object.getProject() != null;
		}
	};

	private FlexoProject projectToImport;

	public ImportProject(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProject project = getImportingProject();
		FlexoPamelaResource<ProjectData> resource = (FlexoPamelaResource<ProjectData>) project.getProjectData(true).getFlexoResource();
		FlexoProjectReference projectReference = resource.getModelFactory().newInstance(FlexoProjectReference.class).init(projectToImport);
		project.getProjectData(true).addToImportedProjects(projectReference);
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
