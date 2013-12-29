package org.openflexo.foundation.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.resource.FlexoProjectReference;
import org.openflexo.foundation.utils.FlexoObjectReference;

public class RemoveImportedProject extends FlexoAction<RemoveImportedProject, FlexoProjectObject, FlexoProjectObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(RemoveImportedProject.class
			.getPackage().getName());

	public static final FlexoActionType<RemoveImportedProject, FlexoProjectObject, FlexoProjectObject> actionType = new FlexoActionType<RemoveImportedProject, FlexoProjectObject, FlexoProjectObject>(
			"remove_project") {

		@Override
		public RemoveImportedProject makeNewAction(FlexoProjectObject focusedObject, Vector<FlexoProjectObject> globalSelection,
				FlexoEditor editor) {
			return new RemoveImportedProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProjectObject object, Vector<FlexoProjectObject> globalSelection) {
			return isEnabled(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(FlexoProjectObject object, Vector<FlexoProjectObject> globalSelection) {
			return object != null && object.getProject() != null && object.getProject().getProjectData() != null
					&& object.getProject().getProjectData().getImportedProjects().size() > 0;
		}
	};

	static {
		// FlexoObject.addActionForClass(actionType, FlexoProject.class);
	}

	private String projectToRemoveURI;

	public RemoveImportedProject(FlexoProjectObject focusedObject, Vector<FlexoProjectObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getProject().getProjectData() != null) {
			String projectToRemoveURI = getProjectToRemoveURI();
			FlexoProjectReference projectReferenceWithURI = getProject().getProjectData().getProjectReferenceWithURI(projectToRemoveURI);
			if (projectReferenceWithURI != null) {
				List<FlexoObjectReference<?>> toDelete = new ArrayList<FlexoObjectReference<?>>();
				for (FlexoObjectReference<?> ref : getProject().getObjectReferences()) {
					if (projectToRemoveURI.equals(ref.getReferringProject(true).getURI())) {
						toDelete.add(ref);
					}
				}
				for (FlexoObjectReference<?> objectReference : toDelete) {
					objectReference.delete();
				}
				projectReferenceWithURI.delete();
			}
		}
	}

	public FlexoProject getProject() {
		return getFocusedObject().getProject();
	}

	public String getProjectToRemoveURI() {
		return projectToRemoveURI;
	}

	public void setProjectToRemoveURI(String projectToRemoveURI) {
		this.projectToRemoveURI = projectToRemoveURI;
	}

}
