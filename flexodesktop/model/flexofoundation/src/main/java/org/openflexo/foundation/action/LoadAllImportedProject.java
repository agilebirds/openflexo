package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;

public class LoadAllImportedProject extends FlexoAction<LoadAllImportedProject, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<LoadAllImportedProject, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<LoadAllImportedProject, FlexoModelObject, FlexoModelObject>(
			"load_all_imported_project") {

		@Override
		public LoadAllImportedProject makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new LoadAllImportedProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object.getProject() != null && object.getProject().getProjectData() != null
					&& object.getProject().getProjectData().getImportedProjects().size() > 0;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object.getProject() != null && !object.getProject().areAllImportedProjectsLoaded();
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, GeneratedOutput.class);
		FlexoModelObject.addActionForClass(actionType, GenerationRepository.class);
		FlexoModelObject.addActionForClass(actionType, FlexoProject.class);
	}

	public LoadAllImportedProject(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
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
				loaded &= referredProject != null;
			}
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
