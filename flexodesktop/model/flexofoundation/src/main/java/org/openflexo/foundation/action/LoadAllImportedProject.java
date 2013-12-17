package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.resource.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;

public class LoadAllImportedProject extends FlexoAction<LoadAllImportedProject, FlexoProjectObject, FlexoProjectObject> {

	public static final FlexoActionType<LoadAllImportedProject, FlexoProjectObject, FlexoProjectObject> actionType = new FlexoActionType<LoadAllImportedProject, FlexoProjectObject, FlexoProjectObject>(
			"load_all_imported_project") {

		@Override
		public LoadAllImportedProject makeNewAction(FlexoProjectObject focusedObject, Vector<FlexoProjectObject> globalSelection,
				FlexoEditor editor) {
			return new LoadAllImportedProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProjectObject object, Vector<FlexoProjectObject> globalSelection) {
			return object != null && object.getProject() != null && object.getProject().getProjectData() != null
					&& object.getProject().getProjectData().getImportedProjects().size() > 0;
		}

		@Override
		public boolean isEnabledForSelection(FlexoProjectObject object, Vector<FlexoProjectObject> globalSelection) {
			return object != null && object.getProject() != null && !object.getProject().areAllImportedProjectsLoaded();
		}
	};

	static {
		// FlexoObject.addActionForClass(actionType, GeneratedOutput.class);
		// FlexoObject.addActionForClass(actionType, GenerationRepository.class);
		// FlexoObject.addActionForClass(actionType, FlexoProject.class);
	}

	public LoadAllImportedProject(FlexoProjectObject focusedObject, Vector<FlexoProjectObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProject project = getImportingProject();
		if (!loadImportedProjects(project)) {
			throw new ProjectLoadingCancelledException();
		}
	}

	private boolean loadImportedProjects(FlexoProject project) {
		boolean loaded = true;
		if (project.getProjectData() != null) {
			for (FlexoProjectReference ref : project.getProjectData().getImportedProjects()) {
				FlexoProject referredProject = ref.getReferredProject(true);
				if (referredProject != null) {
					loaded &= loadImportedProjects(referredProject);
				} else {
					loaded = false;
				}
			}
		}
		return loaded;
	}

	public FlexoProject getImportingProject() {
		return getFocusedObject().getProject();
	}

}
