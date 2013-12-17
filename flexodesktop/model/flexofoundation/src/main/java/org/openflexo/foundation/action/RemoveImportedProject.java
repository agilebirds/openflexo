package org.openflexo.foundation.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.flexo.model.FlexoModelObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.FlexoProjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference;

public class RemoveImportedProject extends FlexoAction<RemoveImportedProject, FlexoModelObject, FlexoModelObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(RemoveImportedProject.class
			.getPackage().getName());

	public static final FlexoActionType<RemoveImportedProject, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<RemoveImportedProject, FlexoModelObject, FlexoModelObject>(
			"remove_project") {

		@Override
		public RemoveImportedProject makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new RemoveImportedProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return isEnabled(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object.getProject() != null && object.getProject().getProjectData() != null
					&& object.getProject().getProjectData().getImportedProjects().size() > 0;
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoProject.class);
	}

	private String projectToRemoveURI;

	public RemoveImportedProject(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getProject().getProjectData() != null) {
			String projectToRemoveURI = getProjectToRemoveURI();
			FlexoProjectReference projectReferenceWithURI = getProject().getProjectData().getProjectReferenceWithURI(projectToRemoveURI);
			if (projectReferenceWithURI != null) {
				List<FlexoModelObjectReference<?>> toDelete = new ArrayList<FlexoModelObjectReference<?>>();
				for (FlexoModelObjectReference<?> ref : getProject().getObjectReferences()) {
					if (projectToRemoveURI.equals(ref.getEnclosingProjectIdentifier())) {
						toDelete.add(ref);
					}
				}
				for (FlexoModelObjectReference<?> objectReference : toDelete) {
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
