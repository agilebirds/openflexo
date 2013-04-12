package org.openflexo.foundation.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;

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
			return object != null && object.getProject() != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object.getProject() != null;
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, RoleList.class);
		FlexoModelObject.addActionForClass(actionType, FlexoWorkflow.class);
		FlexoModelObject.addActionForClass(actionType, FlexoProject.class);
	}

	private FlexoProject importingProject;

	public RemoveImportedProject(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getImportingProject().getProjectData() != null) {
			String projectToRemoveURI = getProjectToRemoveURI();
			FlexoProjectReference projectReferenceWithURI = getImportingProject().getProjectData().getProjectReferenceWithURI(
					projectToRemoveURI);
			if (projectReferenceWithURI != null) {
				List<FlexoModelObjectReference<?>> toDelete = new ArrayList<FlexoModelObjectReference<?>>();
				for (FlexoModelObjectReference<?> ref : getImportingProject().getObjectReferences()) {
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

	public String getProjectToRemoveURI() {
		if (getFocusedObject() instanceof FlexoWorkflow) {
			return ((FlexoWorkflow) getFocusedObject()).getProjectURI();
		} else if (getFocusedObject() instanceof RoleList) {
			return ((RoleList) getFocusedObject()).getWorkflow().getProjectURI();
		} else if (getFocusedObject() instanceof FlexoProject) {
			return ((FlexoProject) getFocusedObject()).getProjectURI();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Don't know how to retrieve project URI from " + getFocusedObject());
			}
			return null;
		}
	}

	public FlexoProject getImportingProject() {
		return importingProject;
	}

	public void setImportingProject(FlexoProject importingProject) {
		this.importingProject = importingProject;
	}

}
