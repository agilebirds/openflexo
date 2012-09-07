package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.FlexoProject;

public class ImportProject extends FlexoAction<ImportProject, FlexoProject, FlexoProject> {

	public static final FlexoActionType<ImportProject, FlexoProject, FlexoProject> actionType = new FlexoActionType<ImportProject, FlexoProject, FlexoProject>(
			"import_project") {

		@Override
		public ImportProject makeNewAction(FlexoProject focusedObject, Vector<FlexoProject> globalSelection, FlexoEditor editor) {
			return new ImportProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProject object, Vector<FlexoProject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoProject object, Vector<FlexoProject> globalSelection) {
			return object != null;
		}
	};

	public ImportProject(FlexoProject focusedObject, Vector<FlexoProject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProject project = getFocusedObject();
	}

}
